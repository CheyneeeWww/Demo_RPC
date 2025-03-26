package org.example.common.serializer.myCode;

import org.example.common.exception.SerializeException;
import org.example.common.Message.MessageType;
import org.example.common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.example.common.trace.TraceContext;

import java.util.Arrays;
import java.util.List;

/**
 * @Author cnwang
 * @Date created in 0:43 2025/3/25
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<6){
            return;
        }

        int traceLength = in.readInt();
        byte[] traceBytes = new byte[traceLength];
        in.readBytes(traceBytes);
        serializeTraceMsg(traceBytes);

        //1.读取消息类型
        short messageType = in.readShort();
        // 现在还只支持request与response请求
        if(messageType != MessageType.REQUEST.getCode() &&
                messageType != MessageType.RESPONSE.getCode()){
            log.warn("暂不支持此种数据, messageType: {}", messageType);
            return;
        }
        //2.读取序列化的方式&类型
        short serializerType = in.readShort();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if(serializer == null) {
            log.error("不存在对应的序列化器, serializerType: {}", serializerType);
            throw new SerializeException("不存在对应的序列化器, serializerType: " + serializerType);
        }//3.读取序列化数组长度
        int length = in.readInt();
        if(in.readableBytes()<length){
            return;
        }
        //4.读取序列化数组
        byte[] bytes=new byte[length];
        in.readBytes(bytes);
        log.debug("Received bytes: {}", Arrays.toString(bytes));
        Object deserialize= serializer.deserialize(bytes, messageType);
        out.add(deserialize);
    }
    //解析并存储traceMsg
    private void serializeTraceMsg(byte[] traceByte){
        String traceMsg=new String(traceByte);
        String[] msgs=traceMsg.split(";");
        if(!msgs[0].equals("")) TraceContext.setTraceId(msgs[0]);
        if(!msgs[1].equals("")) TraceContext.setParentSpanId(msgs[1]);
    }


}
