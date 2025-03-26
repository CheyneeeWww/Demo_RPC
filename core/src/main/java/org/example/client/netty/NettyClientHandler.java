package org.example.client.netty;

import org.example.common.Message.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author cnwang
 * @Date created in 14:55 2025/3/13
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception{
        AttributeKey<RpcResponse> RESPONSE_KEY = AttributeKey.valueOf("RPCResponse");
        ctx.channel().attr(RESPONSE_KEY).set(response);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        log.error("Channel exception occurred", cause);
        ctx.close();
    }
}
