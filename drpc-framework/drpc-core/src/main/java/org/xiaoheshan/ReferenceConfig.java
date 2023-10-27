package org.xiaoheshan;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.discovery.Registry;
import org.xiaoheshan.exception.NetWorkException;
import org.xiaoheshan.proxy.hander.RpcConsumerInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ReferenceConfig<T> {
    public Class<T> interfaceRef;
    private Registry registry;


    /**
     * 代理设计模式 生成一个api接口的代理对象 返回一个代理对象
     *
     * @return 生成的代理对象
     */
    public T get() {
        // 此处使用了动态代理完成了一些工作

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        var classes = new Class[]{interfaceRef};
        InvocationHandler invocationHandler = new RpcConsumerInvocationHandler(registry, interfaceRef);

        // 使用动态代理完成了一些工作
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, invocationHandler);

        return (T) helloProxy;

    }

    public Class<T> getInterfaceRef() {
        return interfaceRef;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }


    public void setInterfaceRef(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }


}
