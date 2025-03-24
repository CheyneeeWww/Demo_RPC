package Client.proxy;

import Client.retry.guavaRetry;
import Client.rpcClient.RpcClient;
import Client.rpcClient.impl.NettyRpcClient;
import common.Message.RpcRequest;
import common.Message.RpcResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import Client.serviceCenter.ServiceCenter;
import Client.serviceCenter.ZKServiceCenter;
/**
 * @Author cnwang
 * @Date created in 21:14 2025/3/12
 */
public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;

    public ClientProxy() throws InterruptedException{
        serviceCenter = new ZKServiceCenter();
        rpcClient = new NettyRpcClient(serviceCenter);
    }

    @Override
    public Object invoke(Object proxy,Method method,Object[] args) throws Throwable{
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramsType(method.getParameterTypes()).build();

        RpcResponse response;
        if(serviceCenter.checkRetry(request.getInterfaceName())){
            response = new guavaRetry().sendServiceWithRetry(request,rpcClient);
        }else{
            response = rpcClient.sendRequest(request);
        }
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
        return (T)o;
    }
}
