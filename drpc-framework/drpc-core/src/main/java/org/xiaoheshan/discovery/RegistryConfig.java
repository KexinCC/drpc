package org.xiaoheshan.discovery;

import org.xiaoheshan.Constant;
import org.xiaoheshan.discovery.impl.NacosRegistry;
import org.xiaoheshan.discovery.impl.ZookeeperRegistry;
import org.xiaoheshan.exception.DiscoveryException;

public class RegistryConfig {

    // 维护一个zk实例

    private final String connectString;

    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }

    /**
     * 可以使用简单工厂方法
     * @return 具体的注册中心实例
     */
    public Registry getRegistry() {
        String registryType = getRegistryType(connectString, true).toLowerCase().trim();
        if (registryType.equals("zookeeper")) {
            String host = getRegistryType(connectString, false);
            return new ZookeeperRegistry(host, Constant.TIME_OUT );
        } else if (registryType.equals("nacos")) {
            String host = getRegistryType(connectString, false);
            return new NacosRegistry(host, Constant.TIME_OUT);
        }
        throw new DiscoveryException("未发现合适的注册中心");
    }

    public String getRegistryType(String connectString,boolean ifType) {
        String[] typeAndHost = connectString.split("://");
        if (typeAndHost.length != 2) {
            throw new RuntimeException("给定的注册中心url不合法");
        }
        if (ifType) {
            return typeAndHost[0];
        } else {
            return typeAndHost[1];
        }

    }



}
