package common.service;

import common.pojo.User;

/**
 * @Author cnwang
 * @Date created in 20:45 2025/3/12
 */
public interface UserService {
    User getUserByUserId(Integer id);
    Integer insertUserId(User user);
}
