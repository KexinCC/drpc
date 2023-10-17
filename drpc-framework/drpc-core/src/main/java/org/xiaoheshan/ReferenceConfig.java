package org.xiaoheshan;

import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.discovery.Registry;
import org.xiaoheshan.discovery.Registry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

@Slf4j
public class ReferenceConfig<T> {
    public Class<T> interfaceRef;

    private Registry registry;



    /**
     * 代理设计模式 生成一个api接口的代理对象 返回一个代理对象
     * @return 生成的代理对象
     */
    public T get() {
        // 此处使用了动态代理完成了一些工作

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Class[] classes = new Class[]{interfaceRef};

        // 使用动态代理完成了一些工作
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                // 我们调用sayHi() 事实上会走进这个代码段当中
                // 我们已经知道 method 和对应的 args
                log.info("method:{}", method.getName());
                log.info("args:{}", args);

                // 传入服务的名字 返回ip+端口
                InetSocketAddress address = registry.lookup(interfaceRef.getName());
                if (log.isDebugEnabled()) {
                    log.debug("服务调用方 发现了服务[{}]的可用主机[{}]", interfaceRef.getName(), address.toString());
                }


                // 2.使用netty连接服务器 发送调用服务的名字+方法名字+参数列表 得到结果
                System.out.println("hello proxy");
                return null;
            }
        });

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
