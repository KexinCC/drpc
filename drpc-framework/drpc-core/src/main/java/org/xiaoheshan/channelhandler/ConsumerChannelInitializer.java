package org.xiaoheshan.channelhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.xiaoheshan.channelhandler.handler.DrpcRequestEncoder;
import org.xiaoheshan.channelhandler.handler.DrpcResponseDecoder;
import org.xiaoheshan.channelhandler.handler.MySimpleInBoundHandler;

public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new LoggingHandler(LogLevel.DEBUG))  // 日志处理器
                .addLast(new DrpcRequestEncoder())            // 编码器
                .addLast(new DrpcResponseDecoder())           // 入站解码器
                .addLast(new MySimpleInBoundHandler());       // 处理结果
    }
}
