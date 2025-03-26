package org.example.provider;

import lombok.extern.slf4j.Slf4j;
import org.example.provider.impl.UserServiceImpl;
import org.example.server.Server.RpcServer;
import org.example.server.Server.impl.NettyRPCRPCServer;
import org.example.server.provider.ServiceProvider;
import org.example.service.UserService;
import org.example.RpcApplication;

/**
 * @Author cnwang
 * @Date created in 16:23 2025/3/26
 */
@Slf4j
public class ProviderTest {

    public static void main(String[] args) throws InterruptedException {
        RpcApplication.initialize();
        String ip=RpcApplication.getRpcConfig().getHost();
        int port=RpcApplication.getRpcConfig().getPort();
        // 创建 UserService 实例
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider(ip, port);
        // 发布服务接口到 ServiceProvider
        serviceProvider.provideServiceInterface(userService);  // 可以设置是否支持重试

        // 启动 RPC 服务器并监听端口
        RpcServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(port);  // 启动 Netty RPC 服务，监听 port 端口
        log.info("RPC 服务端启动，监听端口" + port);
    }

}
