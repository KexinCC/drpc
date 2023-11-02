package org.xiaoheshan.channelHandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.enumeration.RequestType;
import org.xiaoheshan.serialize.SerializeFactory;
import org.xiaoheshan.serialize.Serializer;
import org.xiaoheshan.transport.message.DrpcRequest;
import org.xiaoheshan.transport.message.MessageFormatConstant;
import org.xiaoheshan.transport.message.RequestPayload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


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
public class DrpcRequestDecoder extends LengthFieldBasedFrameDecoder {

    public DrpcRequestDecoder() {
        /**
         * maxFrameLength 超过最大帧长度的报文直接丢弃
         */
        //找到当前报文的总长度 , 截取报文 并解析
        super(
                MessageFormatConstant.MAX_FRAME_LENGTH,

                // 总长度字段的偏移量
                MessageFormatConstant.MAGIC.length
                        + MessageFormatConstant.VERSION_LENGTH
                        + MessageFormatConstant.HEADER_FIELD_LENGTH,


                // 长度的字段的长度 FULL_FIELD_LENGTH 4
                MessageFormatConstant.FULL_FIELD_LENGTH,

                // todo 负载的适配长度
                -(MessageFormatConstant.MAGIC.length
                        + MessageFormatConstant.VERSION_LENGTH
                        + MessageFormatConstant.HEADER_FIELD_LENGTH
                        + MessageFormatConstant.FULL_FIELD_LENGTH),

                0
        );
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf byteBuf) {
            return decodeFrame(byteBuf);
        }
        return null;
    }

    private Object decodeFrame(ByteBuf byteBuf) {
        // 1.解析魔术
        byte[] magic = new byte[MessageFormatConstant.MAGIC.length];
        byteBuf.readBytes(magic);

        for (int i = 0; i < magic.length; i++) {
            if (magic[i] != MessageFormatConstant.MAGIC[i]) {
                throw new RuntimeException("The request obtained is not legitimate");
            }
        }

        // 2.解析版本
        byte version = byteBuf.readByte();
        if (version > MessageFormatConstant.VERSION) {
            throw new RuntimeException("The requested version obtained is not reasonable");
        }

        // 3.解析头部长度
        short headLength = byteBuf.readShort();

        // 4.解析总长度
        int fullLength = byteBuf.readInt();

        // 5.序列化类型
        byte serializeType = byteBuf.readByte();

        // 6.压缩类型
        byte compressType = byteBuf.readByte();

        // 7.请求类型 todo: 判断是不是心跳检测
        byte requestType = byteBuf.readByte();


        // 8.请求id
        long requestId = byteBuf.readLong();

        // 封装一个RequestPayload
        DrpcRequest drpcRequest = new DrpcRequest();
        drpcRequest.setRequestId(requestId);
        drpcRequest.setRequestType(requestType);
        drpcRequest.setSerializeType(serializeType);
        drpcRequest.setCompressType(compressType);

        // 心跳请求没有负载 此处可以直接返回
        if (requestType == RequestType.HEART_HEAT.getID()) {
            return drpcRequest;
        }

        int payloadLength = fullLength - headLength;
        byte[] payload = new byte[payloadLength];
        byteBuf.readBytes(payload);

        // todo 解压缩

        // 反序列化
        Serializer serializer = SerializeFactory.getSerializer(drpcRequest.getSerializeType()).getSerializer();
        RequestPayload requestPayload = serializer.deserialize(payload, RequestPayload.class);
        drpcRequest.setRequestPayload(requestPayload);

        if (log.isDebugEnabled()) {
            log.debug("请求[{}]已经完成了报文的解码", drpcRequest.getRequestId());
        }


        return drpcRequest;
    }


}
