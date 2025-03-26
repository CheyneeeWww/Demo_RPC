package org.example.server.ratelimit;

/**
 * @Author cnwang
 * @Date created in 3:21 2025/3/25
 */
public interface RateLimit {
    boolean getToken();
}
