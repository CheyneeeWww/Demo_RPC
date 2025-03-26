package org.example.service;

import org.example.annotation.Retryable;
import org.example.pojo.User;

/**
 * @Author cnwang
 * @Date created in 13:41 2025/3/26
 */
public interface UserService {

    @Retryable
    User getUserByUserId(Integer id);

    @Retryable
    Integer insertUserId(User user);
}
