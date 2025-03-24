package Server.server;

/**
 * @Author cnwang
 * @Date created in 21:35 2025/3/12
 */
public interface RpcServer {
    void start(int port);
    void stop();
}
