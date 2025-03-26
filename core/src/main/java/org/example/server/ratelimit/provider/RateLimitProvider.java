package org.example.server.ratelimit.provider;
import lombok.extern.slf4j.Slf4j;
import org.example.server.ratelimit.RateLimit;
import org.example.server.ratelimit.impl.TokenBucketRateLimitImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author cnwang
 * @Date created in 3:48 2025/3/25
 */
@Slf4j
public class RateLimitProvider {
    private Map<String, RateLimit> rateLimitMap=new ConcurrentHashMap<>();

    // 默认的限流桶容量和令牌生成速率
    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_RATE = 100;

    public RateLimit getRateLimit(String interfaceName){
        return rateLimitMap.computeIfAbsent(interfaceName, key -> {
            RateLimit rateLimit = new TokenBucketRateLimitImpl(DEFAULT_RATE, DEFAULT_CAPACITY);
            log.info("为接口 [{}] 创建了新的限流策略: {}", interfaceName, rateLimit);
            return rateLimit;
        });
    }
}
