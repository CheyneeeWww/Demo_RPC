package org.example.consumer;

import org.example.common.util.ConfigUtil;
import org.example.config.RpcConfig;

/**
 * @Author cnwang
 * @Date created in 14:31 2025/3/26
 */
public class ConsumerTestConfig {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtil.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
