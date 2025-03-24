package Client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * @Author cnwang
 * @Date created in 12:33 2025/3/14
 */
public interface ServiceCenter {
    InetSocketAddress serviceDiscovery(String serviceName);
}
