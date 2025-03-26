package org.example.server.ratelimit.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.server.ratelimit.RateLimit;

/**
 * @Author cnwang
 * @Date created in 3:22 2025/3/25
 */
@Slf4j
public class TokenBucketRateLimitImpl implements RateLimit {
    private static int rate;
    private static int capacity;
    private volatile int curCapacity;
    private volatile long lastTimestamp;
    public TokenBucketRateLimitImpl(int rate,int capacity){
        this.rate =rate;
        this.capacity=capacity;
        this.curCapacity = capacity;
        this.lastTimestamp = System.currentTimeMillis();
    }

    @Override
    public boolean getToken() {
        // 优化：同步仅限于关键部分，减少锁竞争
        synchronized (this) {
            // 如果当前桶还有剩余，就直接返回
            if (curCapacity > 0) {
                curCapacity--;
                return true;
            }

            long currentTimestamp = System.currentTimeMillis();
            // 如果距离上一次请求的时间大于 RATE 的时间间隔
            if (currentTimestamp - lastTimestamp >= rate) {
                // 计算这段时间内生成的令牌数量
                int generatedTokens = (int) ((currentTimestamp - lastTimestamp) / rate);
                if (generatedTokens > 1) {
                    // 只添加剩余令牌，确保不会超过桶的容量
                    curCapacity = Math.min(capacity, curCapacity + generatedTokens - 1);
                }
                // 更新时间戳
                lastTimestamp = currentTimestamp;
                return true;
            }
            return false;  // 如果无法获取令牌，返回 false
        }
    }
}
