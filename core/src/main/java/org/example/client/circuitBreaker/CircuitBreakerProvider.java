package org.example.client.circuitBreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author cnwang
 * @Date created in 3:58 2025/3/25
 */
@Slf4j
public class CircuitBreakerProvider {

    // 用一个 Map 存储每个服务的熔断器实例，key 为服务名，value 为该服务的熔断器
    private Map<String, CircuitBreaker> circuitBreakerMap = new ConcurrentHashMap<>();

    public synchronized CircuitBreaker getCircuitBreaker(String serviceName) {
        // 使用 computeIfAbsent，避免手动同步
        return circuitBreakerMap.computeIfAbsent(serviceName, key -> {
            log.info("服务 [{}] 不存在熔断器，创建新的熔断器实例", serviceName);
            // 创建并返回新熔断器
            return new CircuitBreaker(1, 0.5, 10000);
        });
    }
}
