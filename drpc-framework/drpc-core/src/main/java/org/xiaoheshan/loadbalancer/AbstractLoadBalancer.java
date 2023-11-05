package org.xiaoheshan.loadbalancer;

import org.xiaoheshan.DrpcBootstrap;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractLoadBalancer implements LoadBalancer {


    // 一个服务 匹配一个Selector
    private final Map<String, Selector> cache = new ConcurrentHashMap<>(8);

    @Override
    public InetSocketAddress selectServerAddress(String serviceName) {
        Selector selector = cache.get(serviceName);
        if (selector == null) {
            List<InetSocketAddress> serviceList = DrpcBootstrap.getInstance().getRegistry().lookup(serviceName);
            selector = getSelector(serviceList);
            cache.put(serviceName, selector);
        }

        return selector.getNext();
    }

    /**
     * 由子类进行拓展
     * @param serviceList
     * @return 负载均衡方法选择器
     */
    protected abstract Selector getSelector(List<InetSocketAddress> serviceList);

}
