package org.xiaoheshan.channelHandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.transport.message.DrpcRequest;
import org.xiaoheshan.transport.message.MessageFormatConstant;
import org.xiaoheshan.transport.message.RequestPayload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * 4byte magic(魔术值)
 * 1byte version(版本)
 * 2byte header length(首部长度)
 * 4byte full length(报文总长度)
 * 1byte serialize
 * 1byte compress
 * 1byte requestType
 * 8byte requestId
 * body
 * 出站时 第一个经过的处理器
 */
@Slf4j
public class DrpcMessageEncoder extends MessageToByteEncoder<DrpcRequest> implements Serializable {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, DrpcRequest drpcRequest, ByteBuf byteBuf) throws Exception {

        // 4个字节魔术值
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);

        // 1个字节版本号
        byteBuf.writeByte(MessageFormatConstant.VERSION);

        // 2个字节头部长度
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);

        // 移动写指针 为后面留位
        byteBuf.writerIndex(byteBuf.writerIndex() + MessageFormatConstant.FULL_FIELD_LENGTH);

        // 3个类型
        byteBuf.writeByte(drpcRequest.getSerializeType());
        byteBuf.writeByte(drpcRequest.getCompressType());
        byteBuf.writeByte(drpcRequest.getRequestType());

        // 8个字节请求id
        byteBuf.writeLong(drpcRequest.getRequestId());

        byte[] bodyBytes = getBodyBytes(drpcRequest.getRequestPayload());

        // 写入请求体
        byteBuf.writeBytes(bodyBytes);

        int writerIndex = byteBuf.writerIndex();
        byteBuf.writerIndex(MessageFormatConstant.MAGIC.length
                + MessageFormatConstant.VERSION_LENGTH
                + MessageFormatConstant.HEADER_FIELD_LENGTH);
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + bodyBytes.length);

        byteBuf.writerIndex(writerIndex);

    }

    private byte[] getBodyBytes(RequestPayload requestPayload) {
        // todo 针对不同消息类型做不同的请求 心跳 ?
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(requestPayload);
            return baos.toByteArray();

            // todo 压缩
        } catch (IOException e) {
            log.error("序列化时出现异常");
            throw new RuntimeException(e);
        }
    }
}
