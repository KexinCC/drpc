package org.xiaoheshan.loadbalancer.impl;

import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.exception.LoadBalancerException;
import org.xiaoheshan.loadbalancer.AbstractLoadBalancer;
import org.xiaoheshan.loadbalancer.Selector;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new RobinSelector(serviceList);
    }

    private static class RobinSelector implements Selector {

        private final List<InetSocketAddress> serviceList;
        private final AtomicInteger index;

        public RobinSelector(List<InetSocketAddress> serviceLIst) {
            this.serviceList = serviceLIst;
            this.index = new AtomicInteger(0);
        }

        @Override

        public InetSocketAddress getNext() {
            if (serviceList == null || serviceList.isEmpty()) {
                log.error("负载均衡选取节点时 服务列表为空");
                throw new LoadBalancerException();
            }
            InetSocketAddress address = serviceList.get(index.get());
            if (index.get() == serviceList.size() - 1) {
                index.set(0);
            } else {
                index.incrementAndGet();
            }
            return address;
        }

        @Override
        public void reBalance() {

        }
    }
}
