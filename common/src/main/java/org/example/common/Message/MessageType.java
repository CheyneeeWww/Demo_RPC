package org.example.common.Message;

import lombok.AllArgsConstructor;

/**
 * @Author cnwang
 * @Date created in 0:41 2025/3/25
 */
@AllArgsConstructor
public enum MessageType {
    REQUEST(0),RESPONSE(1);
    private int code;
    public int getCode(){
        return code;
    }
}
