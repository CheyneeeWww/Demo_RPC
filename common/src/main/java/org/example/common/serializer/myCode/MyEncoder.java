package org.example.common.serializer.myCode;

import org.example.common.Message.MessageType;
import org.example.common.Message.RpcRequest;
import org.example.common.Message.RpcResponse;
import org.example.common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.trace.TraceContext;

/**
 * @Author cnwang
 * @Date created in 0:42 2025/3/25
 */
@Slf4j
@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        log.debug("Encoding message of type: {}", msg.getClass());

        String traceMsg = TraceContext.getTraceId()+";"+TraceContext.getSpanId();
        byte[] traceBytes = traceMsg.getBytes();
        out.writeInt(traceBytes.length);
        out.writeBytes(traceBytes);
        //1.写入消息类型
        if(msg instanceof RpcRequest){
            out.writeShort(MessageType.REQUEST.getCode());
        } else if(msg instanceof RpcResponse){
            out.writeShort(MessageType.RESPONSE.getCode());
        }else {
            log.error("Unknown message type: {}", msg.getClass());
            throw new IllegalArgumentException("Unknown message type: " + msg.getClass());
        }
        //2.写入序列化方式
        out.writeShort(serializer.getType());
        //得到序列化数组
        byte[] serializeBytes = serializer.serialize(msg);
        if (serializeBytes == null || serializeBytes.length == 0) {
            throw new IllegalArgumentException("Serialized message is empty");
        }
        //3.写入长度
        out.writeInt(serializeBytes.length);
        //4.写入序列化数组
        out.writeBytes(serializeBytes);
    }
}
