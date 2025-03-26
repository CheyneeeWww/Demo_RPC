package org.example.client.serviceCenter;

import org.example.common.Message.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @Author cnwang
 * @Date created in 12:33 2025/3/14
 */
public interface ServiceCenter {
    InetSocketAddress serviceDiscovery(RpcRequest request);
    boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature);
    void close();
}
