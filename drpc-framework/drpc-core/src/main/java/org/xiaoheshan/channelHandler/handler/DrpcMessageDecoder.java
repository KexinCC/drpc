package org.xiaoheshan.channelHandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.xiaoheshan.transport.message.MessageFormatConstant;

public class DrpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }

    public DrpcMessageDecoder() {
        /**
         * maxFrameLength
         */
        super(
                MessageFormatConstant.MAX_FRAME_LENGTH,
                MessageFormatConstant.MAX_FRAME_LENGTH + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH,
                MessageFormatConstant.FULL_FIELD_LENGTH,
                MessageFormatConstant.MAX_FRAME_LENGTH + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH + MessageFormatConstant.FULL_FIELD_LENGTH,
                0
        );
    }



}
