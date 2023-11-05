package org.xiaoheshan.discovery.impl;

import org.xiaoheshan.ServiceConfig;
import org.xiaoheshan.discovery.Registry;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosRegistry implements Registry {
    public NacosRegistry(String host, int timeout) {
    }

    @Override
    public void register(ServiceConfig<?> serviceConfig) {

    }

    @Override
    public List<InetSocketAddress> lookup(String name) {
        return null;
    }
}
