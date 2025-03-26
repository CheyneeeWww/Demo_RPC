package org.example.client.proxy;

import org.example.client.circuitBreaker.CircuitBreaker;
import org.example.client.circuitBreaker.CircuitBreakerProvider;
import org.example.client.retry.guavaRetry;
import org.example.client.rpcClient.RpcClient;
import org.example.client.rpcClient.impl.NettyRpcClient;
import org.example.common.Message.RequestType;
import org.example.common.Message.RpcRequest;
import org.example.common.Message.RpcResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

import org.example.client.serviceCenter.ServiceCenter;
import org.example.client.serviceCenter.ZKServiceCenter;
import lombok.extern.slf4j.Slf4j;
import org.example.trace.interceptor.ClientTraceInterceptor;

/**
 * @Author cnwang
 * @Date created in 21:14 2025/3/12
 */
@Slf4j
public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;
    private CircuitBreakerProvider circuitBreakerProvider;

    public ClientProxy() throws InterruptedException{
        serviceCenter = new ZKServiceCenter();
        circuitBreakerProvider = new CircuitBreakerProvider();
    }

    @Override
    public Object invoke(Object proxy,Method method,Object[] args) throws Throwable{
        //trace记录
        ClientTraceInterceptor.beforeInvoke();

        RpcRequest request = RpcRequest.builder()
                .type(RequestType.NORMAL)
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramsType(method.getParameterTypes()).build();

        //熔断器
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        if(!circuitBreaker.allowRequest()){
            log.warn("熔断器开启，请求被拒绝: {}", request);
            return null;
        }
        RpcResponse response;
        String methodSignature = getMethodSignature(request.getInterfaceName(), method);
        log.info("方法签名: " + methodSignature);
        InetSocketAddress serviceAddress = serviceCenter.serviceDiscovery(request);
        rpcClient = new NettyRpcClient(serviceAddress);
        if (serviceCenter.checkRetry(serviceAddress, methodSignature)) {
            //调用retry框架进行重试操作
            try {
                log.info("尝试重试调用服务: {}", methodSignature);
                response = new guavaRetry().sendServiceWithRetry(request, rpcClient);
            } catch (Exception e) {
                log.error("重试调用失败: {}", methodSignature, e);
                circuitBreaker.recordFailure();
                throw e;  // 将异常抛给调用者
            }
        } else {
            //只调用一次
            response = rpcClient.sendRequest(request);
        }
        //记录response的状态，上报给熔断器
        if (response != null) {
            if (response.getCode() == 200) {
                circuitBreaker.recordSuccess();
            } else if (response.getCode() == 500) {
                circuitBreaker.recordFailure();
            }
            log.info("收到响应: {} 状态码: {}", request.getInterfaceName(), response.getCode());
        }

        //trace上报
        ClientTraceInterceptor.afterInvoke(method.getName());

        return response != null ? response.getData() : null;
    }
    public <T>T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
        return (T)o;
    }
    // 根据接口名字和方法获取方法签名
    private String getMethodSignature(String interfaceName, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(interfaceName).append("#").append(method.getName()).append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            sb.append(parameterTypes[i].getName());
            if (i < parameterTypes.length - 1) {
                sb.append(",");
            } else{
                sb.append(")");
            }
        }
        return sb.toString();
    }

    //关闭创建的资源
    //注：如果在需要C-S保持长连接的场景下无需调用close方法
    public void close(){
        rpcClient.close();
        serviceCenter.close();
    }
}
