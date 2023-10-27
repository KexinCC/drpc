package org.xiaoheshan.channelHandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoheshan.DrpcBootstrap;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

public class MySimpleInBoundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        /*
         * 处理入站请求 并且服务器返回的结果放在 CompletableFuture 之中
         */
        String result = msg.toString(Charset.defaultCharset());  // 服务提供方给予的结果
        CompletableFuture<Object> completableFuture = DrpcBootstrap.PENDING_REQUEST.get(1L);
        completableFuture.complete(result);
    }
}
