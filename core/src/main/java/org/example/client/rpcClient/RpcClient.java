package org.example.client.rpcClient;

import org.example.common.Message.RpcRequest;
import org.example.common.Message.RpcResponse;

/**
 * @Author cnwang
 * @Date created in 22:35 2025/3/12
 */
public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
    void close();
}
