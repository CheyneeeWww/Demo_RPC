package Server.server.work;


import Server.provider.ServiceProvider;
import common.Message.RpcRequest;
import common.Message.RpcResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @Author cnwang
 * @Date created in 21:36 2025/3/12
 */
@AllArgsConstructor
public class WorkThread implements Runnable {
    private Socket socket;
    private ServiceProvider serviceProvider;

    @Override
    public void run(){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            RpcResponse rpcResponse = getResponse(rpcRequest);
            oos.writeObject(rpcResponse);
            oos.flush();
        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    private RpcResponse getResponse(RpcRequest rpcRequest){
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serviceProvider.getService(interfaceName);
        Method method = null;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamsType());
            Object invoke = method.invoke(service,rpcRequest.getParams());
            return RpcResponse.success(invoke);

        }catch(NoSuchMethodException|IllegalAccessException| InvocationTargetException e){
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }
    }
}
