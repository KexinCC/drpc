package org.xiaoheshan.proxy.hander;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.DrpcBootstrap;
import org.xiaoheshan.NettyBootstrapInitializer;
import org.xiaoheshan.discovery.Registry;
import org.xiaoheshan.exception.DiscoveryException;
import org.xiaoheshan.exception.NetWorkException;
import org.xiaoheshan.transport.message.DrpcRequest;
import org.xiaoheshan.transport.message.RequestPayload;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 该类封装了客户端通信的基础逻辑 每一个代理对象的远程调用过程都封装在invoke方法中
 * 1. 发现可用服务 2.建立连接 3.发送请求 4.得到结果
 */
@Slf4j
public class RpcConsumerInvocationHandler implements InvocationHandler {

    private final Registry registry;
    private final Class<?> interfaceRef;


    public RpcConsumerInvocationHandler(Registry registry, Class<?> interfaceRef) {
        this.registry = registry;
        this.interfaceRef = interfaceRef;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 我们调用sayHi() 事实上会走进这个代码段当中
        // 我们已经知道 method 和对应的 args
//        log.info("method:{}", method.getName());
//        log.info("args:{}", args);


        // 1. 发现服务
        // 传入服务的名字 返回ip+端口
        InetSocketAddress address = registry.lookup(interfaceRef.getName());
        if (log.isDebugEnabled()) {
            log.debug("服务调用方 发现了服务[{}]的可用主机[{}]", interfaceRef.getName(), address.toString());
        }

        // 2. 尝试从全局缓存中获取一个通道
        Channel channel = getAvailableChannel(address);
        if (log.isDebugEnabled()) {
            log.debug("以获取和[{}]建立的连接通道 地址[{}]", interfaceRef.getName(), address.toString());
        }

        /*
            --------------------------封装报文------------------------
         */

        RequestPayload requestPayload = RequestPayload
                .builder()
                .interfaceName(interfaceRef.getName())
                .methodName(method.getName())
                .parametersType(method.getParameterTypes())
                .parametersValue(args)
                .returnType(method.getReturnType())
                .build();


        DrpcRequest drpcRequest = DrpcRequest.builder()
                .requestId(1L)
                .compressType((byte) 1)
                .requestType((byte) 1)
                .serializeType((byte) 1)
                .requestPayload(requestPayload)
                .build();


//                /*
//                  -------------------------同步策略--------------------
//                 */
//                ChannelFuture channelFuture = channel.writeAndFlush(new Object());
//                if (channelFuture.isDone()) {
//                    Object object = channelFuture.getNow();
//                } else if (channelFuture.isSuccess()) {
//                    // 捕获异常
//                    Throwable cause = channelFuture.cause();
//                    throw new RuntimeException(cause);
//                }

        /*
            ------------------------异步策略----------------
         */

        // 4.写出报文
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        // 将CompletableFuture暴露
        DrpcBootstrap.PENDING_REQUEST.put(1L, completableFuture);


        // 写出了请求 这个请求的实例会进入 pipeline 执行
        channel.writeAndFlush(drpcRequest)
                .addListener((ChannelFutureListener) promise -> {
                    if (!promise.isSuccess()) {
                        completableFuture.completeExceptionally(promise.cause());
                    }
                });

        // 5.获取响应结果
        // 如果没有地方处理 cb 这里会阻塞 等待complete 方法执行
        return completableFuture.get(10, TimeUnit.SECONDS);
    }


    /**
     * 根据地址获取通道
     *
     * @param address InetSocketAddress
     * @return Channel
     */
    private Channel getAvailableChannel(InetSocketAddress address) {

        // 1. 尝试从缓存获取
        Channel channel = DrpcBootstrap.CHANNEL_CACHE.get(address);


        // 2. 建立连接
        if (channel == null) {

            /*  await() 方法会阻塞 等待连接成功 netty还提供了异步处理逻辑
                sync() await() 都会阻塞当前线程 获取返回值 因为连接的过程是异步的
                如果发生了异常 sync() 会主动在主线程抛出异常 await() 不会 异常在子线程中处理 需要使用future  */
            CompletableFuture<Channel> channelFuture = new CompletableFuture<>();

            // 使用addListener执行的异步操作
            NettyBootstrapInitializer
                    .getBootstrap()
                    .connect(address)
                    .addListener((ChannelFutureListener) promise -> {
                        if (promise.isDone()) {
                            // 异步 等待策略?
                            if (log.isDebugEnabled()) {
                                log.debug("已经和[{}]成功建立连接", address);
                            }
                            channelFuture.complete(promise.channel());
                        } else if (!promise.isSuccess()) {
                            channelFuture.completeExceptionally(promise.cause());
                        }
                    })
                    .channel();

            // 阻塞获取channel
            try {
                channel = channelFuture.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("获取通道时发生异常", e);
                throw new DiscoveryException(e);
            }

            // 缓存 channel 本身
            DrpcBootstrap.CHANNEL_CACHE.put(address, channel);
        }


        if (channel == null) {
            log.error("获取或建立与[{}]通道时发生了异常", address);
            throw new NetWorkException("获取通道时发生了异常");
        }

        return channel;
    }
}
