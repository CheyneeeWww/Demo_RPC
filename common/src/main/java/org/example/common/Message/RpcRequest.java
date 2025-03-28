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
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable{
    private RequestType type=RequestType.NORMAL;
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramsType;
    public static RpcRequest heartBeat() {
        return RpcRequest.builder().type(RequestType.HEARTBEAT).build();
    }
}
