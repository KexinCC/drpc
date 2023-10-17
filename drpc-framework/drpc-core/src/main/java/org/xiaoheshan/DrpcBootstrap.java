package org.xiaoheshan;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.discovery.Registry;
import org.xiaoheshan.discovery.RegistryConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class DrpcBootstrap {
    private static final DrpcBootstrap drpcBootstrap = new DrpcBootstrap();

    private String appname = "default";
    private ProtocolConfig protocolConfig;

    private final int port = 8088;
    private Registry registry;
    // 维护已经发布且已经发布的服务列表
    // K:interface 全限定名称  V:serviceConfig
    public static final Map<String, ServiceConfig<?>> SERVICE_LIST = new ConcurrentHashMap<>(16);

    private DrpcBootstrap() {

    }

    public static DrpcBootstrap getInstance() {
        return drpcBootstrap;
    }

    /**
     * 用来定义当前应用的名字
     *
     * @param appname app的名字
     * @return this
     */
    public DrpcBootstrap application(String appname) {
        this.appname = appname;
        return this;
    }

    /**
     * 用来配置一个注册中心
     *
     * @param registryConfig 注册中心配置
     * @return this实例
     */
    public DrpcBootstrap registry(RegistryConfig registryConfig) {
        this.registry = registryConfig.getRegistry();
        return this;
    }


    /**
     * 配置当前暴露的服务使用的协议
     *
     * @param protocolConfig 协议的封装
     * @return this实例
     */
    public DrpcBootstrap protocol(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
        if (log.isDebugEnabled()) {
            log.debug("当前工程使用: {}序列化", protocolConfig.toString());
        }
        return this;
    }


    /**
     * 发布服务
     *
     * @param service 独立封装需要发布的服务
     * @return this实例
     */
    public DrpcBootstrap publish(ServiceConfig<?> service) {
        // 抽象了注册中心的概念
        registry.register(service);
        SERVICE_LIST.put(service.getInterfaceProvider().getName(), service);

        // 当服务调用方通过参数列表发起调用时， 提供方怎么知道使用哪一个实现？
        // (1)new (2)spring beanFactory (3)自己维护映射关系


        return this;
    }

    /**
     * 发布服务
     *
     * @param service 批量发布服务
     * @return this实例
     */
    public DrpcBootstrap publish(List<ServiceConfig<?>> service) {
        service.forEach(this::publish);
        return this;
    }


    /**
     * 启动netty服务
     */
    public void start() {
        // 创建 eventLoop
        NioEventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);

        try {
            // 2.需要一个服务器引导程序
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 3.配置服务器
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(null);
                        }
                    });

            // 4.绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    /* ---------------------------------------服务调用方相关api----------------------------*/


    public DrpcBootstrap reference(ReferenceConfig<?> reference) {

        // reference 需要一个注册中心
        reference.setRegistry(registry);

        return this;
    }

}
