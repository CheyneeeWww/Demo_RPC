package org.example.server.serviceRegister;

import java.net.InetSocketAddress;

/**
 * @Author cnwang
 * @Date created in 13:08 2025/3/14
 */
public interface ServiceRegister {
    void register(Class<?> clazz, InetSocketAddress serviceAddress);
}
