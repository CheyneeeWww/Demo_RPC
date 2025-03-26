package org.example.config;

import org.example.client.serviceCenter.balance.impl.ConsistencyHashBalance;
import org.example.common.serializer.mySerializer.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.server.serviceRegister.impl.ZKServiceRegister;

/**
 * @Author cnwang
 * @Date created in 15:41 2025/3/26
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcConfig {
    //名称
    private String name = "rpc";
    //端口
    private Integer port = 9999;
    //主机名
    private String host = "localhost";
    //版本号
    private String version = "1.0.0";
    //注册中心
    private String registry = new ZKServiceRegister().toString();
    //序列化器
    private String serializer = Serializer.getSerializerByCode(3).toString();
    //负载均衡
    private String loadBalance = new ConsistencyHashBalance().toString();
}
