package org.example.server.Netty;

import org.example.common.serializer.myCode.MyDecoder;
import org.example.common.serializer.myCode.MyEncoder;
import org.example.common.serializer.mySerializer.Serializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import org.example.server.provider.ServiceProvider;

/**
 * @Author cnwang
 * @Date created in 20:30 2025/3/13
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //自定义编解码器
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(3)));
        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));

    }
}
