package Client.rpcClient;

import common.Message.RpcRequest;
import common.Message.RpcResponse;

/**
 * @Author cnwang
 * @Date created in 22:35 2025/3/12
 */
public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
