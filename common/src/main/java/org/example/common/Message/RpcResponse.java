package org.example.common.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author cnwang
 * @Date created in 20:47 2025/3/12
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable{
    private int code;
    private String message;
    private Class<?> dataType;
    private Object data;
    public static RpcResponse success(Object data){
        return RpcResponse.builder().code(200).dataType(data.getClass()).data(data).build();
    }

    public static RpcResponse fail(String msg){
        return RpcResponse.builder().code(500).message(msg).build();
    }
}
