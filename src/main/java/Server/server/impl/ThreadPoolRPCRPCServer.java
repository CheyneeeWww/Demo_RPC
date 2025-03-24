package Server.server.impl;

import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.server.work.WorkThread;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author cnwang
 * @Date created in 21:37 2025/3/12
 */
public class ThreadPoolRPCRPCServer implements RpcServer {
    private final ThreadPoolExecutor threadPool;
    private ServiceProvider serviceProvider;
    public ThreadPoolRPCRPCServer(ServiceProvider serviceProvider){
        threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                1000,60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100));
        this.serviceProvider = serviceProvider;
    }
    public ThreadPoolRPCRPCServer(ServiceProvider serviceProvider,
                                  int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue){
        threadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port){
        System.out.println("服务端启动了");
        try{
            ServerSocket serviceSocket = new ServerSocket();
            while(true){
                Socket socket = serviceSocket.accept();
                threadPool.execute(new WorkThread(socket,serviceProvider));
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void stop(){

    }
}
