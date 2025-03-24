package Server.server.impl;

import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.server.work.WorkThread;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author cnwang
 * @Date created in 21:36 2025/3/12
 */
@AllArgsConstructor
public class SimpleRPCRPCServer implements RpcServer{
    private ServiceProvider serviceProvide;
    @Override
    public void start(int port){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动了");
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new WorkThread(socket,serviceProvide)).start();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(){

    }
}
