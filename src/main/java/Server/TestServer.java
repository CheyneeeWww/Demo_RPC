package Server;

import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.server.impl.NettyRPCRPCServer;
import Server.server.impl.SimpleRPCRPCServer;
import common.service.Impl.UserServiceImpl;
import common.service.UserService;

/**
 * @Author cnwang
 * @Date created in 21:34 2025/3/12
 */
public class TestServer {
    public static void main(String[] args){
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1",9999);
        serviceProvider.provideServiceInterface(userService);
        RpcServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
