package org.xiaoheshan;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.channelhandler.ConsumerChannelInitializer;

/**
 * 提供Bootstrap单例
 * todo: 拓展性?
 */
@Slf4j
public class NettyBootstrapInitializer {

    @Getter
    private static final Bootstrap bootstrap = new Bootstrap();

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                //选择初始化一个什么样的channel
                .channel(NioSocketChannel.class)
                .handler(new ConsumerChannelInitializer() {
                });
    }

    private NettyBootstrapInitializer() {
    }

}
