package org.xiaoheshan.discovery.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.xiaoheshan.Constant;
import org.xiaoheshan.ServiceConfig;
import org.xiaoheshan.discovery.AbstractRegistry;
import org.xiaoheshan.exception.DiscoveryException;
import org.xiaoheshan.exception.NetWorkException;
import org.xiaoheshan.utils.NetUtils;
import org.xiaoheshan.utils.zookeeper.ZookeeperNode;
import org.xiaoheshan.utils.zookeeper.ZookeeperUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {

    private final ZooKeeper zookeeper;

    public ZookeeperRegistry() {
        this.zookeeper = ZookeeperUtils.createZookeeper();
    }

    public ZookeeperRegistry(String connectString, int timeout) {
        this.zookeeper = ZookeeperUtils.createZookeeper(connectString, timeout);
    }

    @Override
    public void register(ServiceConfig<?> serviceConfig) {
        // 服务名称的节点
        String parent = Constant.BASE_PROVIDERS_PATH + "/" + serviceConfig.getInterfaceProvider().getName();


        // 这是一个持久节点
        if (!ZookeeperUtils.exist(zookeeper, parent, null)) {
            ZookeeperNode zookeeperNode = new ZookeeperNode(parent, null);
            boolean flag = ZookeeperUtils.createNode(zookeeper, zookeeperNode, null, CreateMode.PERSISTENT);
        }


        // 创建本机临时节点
        // 服务提供方的端口一般自己设定， 需要一个获取ip的方法
        // ip通常是一个局域网的ip
        // todo:后续处理端口号的问题
        String node = parent + "/" + NetUtils.getHostIp() + ":" + 8088;
        if (!ZookeeperUtils.exist(zookeeper, node, null)) {
            ZookeeperNode zookeeperNode = new ZookeeperNode(node, null);
            boolean flag = ZookeeperUtils.createNode(zookeeper, zookeeperNode, null, CreateMode.EPHEMERAL);
        }

        if (log.isDebugEnabled()) {
            log.debug("服务 {},已经被注册", serviceConfig.getInterfaceProvider().getName());
        }
    }

    @Override
    public InetSocketAddress lookup(String serviceName) {
        // 1.找到服务对应的节点
        String serviceFullName = Constant.BASE_PROVIDERS_PATH + "/" + serviceName;

        // 2.从zk中获取它的子节点
        List<String> children = ZookeeperUtils.getChildren(zookeeper, serviceFullName, null);

        List<InetSocketAddress> socketList = children.stream().map(ipString -> {
            String[] ipAndPort = ipString.split(":");
            String ip = ipAndPort[0];
            int port = Integer.parseInt(ipAndPort[1]);
            return new InetSocketAddress(ip, port);
        }).toList();

        if (socketList.size() == 0) {
            throw new DiscoveryException("未发现任何可用的节点");
        }

        return socketList.get(0);


    }
}
