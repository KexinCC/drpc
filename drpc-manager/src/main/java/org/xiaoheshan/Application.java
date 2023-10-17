package org.xiaoheshan;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.xiaoheshan.utils.zookeeper.ZookeeperNode;
import org.xiaoheshan.utils.zookeeper.ZookeeperUtils;

import java.io.IOException;
import java.util.List;


@Slf4j
public class Application {
    public static void main(String[] args) throws IOException {
        // 创建基础目录

        ZooKeeper zooKeeper;
        String basePath = "/drpc-metadata";
        String providerPath = basePath + "/providers";
        String consumerPath = basePath + "/consumers";


        // 创建zookeeper实例，建立连接
        zooKeeper = ZookeeperUtils.createZookeeper();

        ZookeeperNode baseNode = new ZookeeperNode(basePath, null);
        ZookeeperNode providerNode = new ZookeeperNode(providerPath, null);
        ZookeeperNode consumerNode = new ZookeeperNode(consumerPath, null);

        List.of(baseNode, providerNode, consumerNode).forEach(node -> {
            ZookeeperUtils.createNode(zooKeeper, node, null, CreateMode.PERSISTENT);
        });

        ZookeeperUtils.close(zooKeeper);

    }
}
