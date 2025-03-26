package org.example.client.netty;

import io.netty.handler.timeout.IdleStateHandler;
import org.example.common.serializer.myCode.MyDecoder;
import org.example.common.serializer.myCode.MyEncoder;
import org.example.common.serializer.mySerializer.Serializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * @Author cnwang
 * @Date created in 15:01 2025/3/13
 */
@Slf4j
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    public NettyClientInitializer(){}

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 使用自定义的编码器和解码器
        try {
            // 根据传入的序列化器类型初始化编码器
            pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(3)));
            pipeline.addLast(new MyDecoder());
            pipeline.addLast(new NettyClientHandler());
            pipeline.addLast(new MDCChannelHandler());

            pipeline.addLast(new IdleStateHandler(0, 8, 0, TimeUnit.SECONDS));
            pipeline.addLast(new HeartbeatHandler());

            log.info("Netty client pipeline initialized with serializer type: {}",Serializer.getSerializerByCode(3).toString());
        } catch (Exception e) {
            log.error("Error initializing Netty client pipeline", e);
            throw e;  // 重新抛出异常，确保管道初始化失败时处理正确
        }
    }
}
