package Message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author cnwang
 * @Date created in 20:47 2025/3/12
 */
@Data
@Builder
public class RpcRequest implements Serializable{
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramsType;
}
