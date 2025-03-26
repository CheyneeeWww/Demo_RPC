package org.example.common.Message;

import lombok.AllArgsConstructor;

/**
 * @Author cnwang
 * @Date created in 23:06 2025/3/26
 */
@AllArgsConstructor
public enum RequestType {
    NORMAL(0), HEARTBEAT(1);
    private int code;

    public int getCode() {
        return code;
    }
}