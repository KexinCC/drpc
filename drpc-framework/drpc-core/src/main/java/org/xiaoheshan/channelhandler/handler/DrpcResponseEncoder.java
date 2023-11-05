package org.xiaoheshan.channelhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.compress.CompressFactory;
import org.xiaoheshan.compress.Compressor;
import org.xiaoheshan.serialize.SerializeFactory;
import org.xiaoheshan.serialize.Serializer;
import org.xiaoheshan.transport.message.DrpcResponse;
import org.xiaoheshan.transport.message.MessageFormatConstant;

import java.io.Serializable;


/**
 * 4byte magic(魔术值)
 * 1byte version(版本)
 * 2byte header length(首部长度)
 * 4byte full length(报文总长度)
 * 1byte serialize
 * 1byte compress
 * 1byte code
 * 8byte requestId
 * body
 * 出站时 第一个经过的处理器
 */
@Slf4j
public class DrpcResponseEncoder extends MessageToByteEncoder<DrpcResponse> implements Serializable {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, DrpcResponse drpcResponse, ByteBuf byteBuf) throws Exception {

        // 4个字节魔术值
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);

        // 1个字节版本号
        byteBuf.writeByte(MessageFormatConstant.VERSION);

        // 2个字节头部长度
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);

        // 移动写指针 为后面留位
        byteBuf.writerIndex(byteBuf.writerIndex() + MessageFormatConstant.FULL_FIELD_LENGTH);

        // 3个类型
        byteBuf.writeByte(drpcResponse.getSerializeType());
        byteBuf.writeByte(drpcResponse.getCompressType());
        byteBuf.writeByte(drpcResponse.getCode());

        // 8个字节请求id
        byteBuf.writeLong(drpcResponse.getRequestId());


        // 序列化
        Serializer serializer = SerializeFactory.getSerializer(drpcResponse.getSerializeType()).getSerializer();
        byte[] bodyBytes  = serializer.serialize(drpcResponse.getBody());

        // 压缩
        Compressor compressor = CompressFactory.getCompressor(drpcResponse.getCompressType()).getCompressor();
        bodyBytes = compressor.compress(bodyBytes);


        if (bodyBytes != null) {
            byteBuf.writeBytes(bodyBytes);
        }

        // 心跳请求 bodyLength 为0
        int bodyLength = bodyBytes == null ? 0 : bodyBytes.length;


        int writerIndex = byteBuf.writerIndex();
        byteBuf.writerIndex(MessageFormatConstant.MAGIC.length
                + MessageFormatConstant.VERSION_LENGTH
                + MessageFormatConstant.HEADER_FIELD_LENGTH);
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + bodyLength);

        byteBuf.writerIndex(writerIndex);

        if (log.isDebugEnabled()) {
            log.debug("生产者对响应[{}]完成编码工作", drpcResponse.getRequestId());
        }

    }
}
