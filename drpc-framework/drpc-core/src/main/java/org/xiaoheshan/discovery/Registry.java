package org.xiaoheshan.discovery;

import org.xiaoheshan.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 注册中心
 */
public interface Registry {
    /**
     * 注册服务
     * @param serviceConfig 服务内容的配置
     */
    void register(ServiceConfig<?> serviceConfig);

    /**
     * 从注册中心拉取一个可用的服务
     * @param name 服务的名称
     * @return 服务的ip+端口
     */
    List<InetSocketAddress> lookup(String name);

}
