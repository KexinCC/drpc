package org.xiaoheshan.channelhandler.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.DrpcBootstrap;
import org.xiaoheshan.transport.message.DrpcResponse;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class MySimpleInBoundHandler extends SimpleChannelInboundHandler<DrpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DrpcResponse drpcResponse) throws Exception {

        // 服务提供方给予的结果
        Object returnValue = drpcResponse.getBody();

        /*
         * 处理入站请求 并且服务器返回的结果放在 CompletableFuture 之中
         */
        CompletableFuture<Object> completableFuture = DrpcBootstrap.PENDING_REQUEST.get(1L);
        completableFuture.complete(returnValue);

        if (log.isDebugEnabled()) {
            log.debug("已经寻找到编号为[{}]的compeleteFutre处理响应结果", drpcResponse.getRequestId());
        }
    }
}
