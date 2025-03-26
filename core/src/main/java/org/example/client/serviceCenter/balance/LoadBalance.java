package org.example.client.serviceCenter.balance;

import java.util.List;

/**
 * @Author cnwang
 * @Date created in 1:57 2025/3/25
 */
public interface LoadBalance {
    String balance(List<String> addressList);
    void addNode(String node);
    void delNode(String node);
}
