package org.xiaoheshan.channelHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.xiaoheshan.channelHandler.handler.DrpcMessageEncoder;
import org.xiaoheshan.channelHandler.handler.MySimpleInBoundHandler;

public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new LoggingHandler(LogLevel.DEBUG))  // 日志处理器
                .addLast(new DrpcMessageEncoder())            // 编码器
                .addLast(new MySimpleInBoundHandler());

    }
}
