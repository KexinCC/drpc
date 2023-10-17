package org.xiaoheshan.utils.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.xiaoheshan.Constant;
import org.xiaoheshan.exception.ZookeeperException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZookeeperUtils {

    public static ZooKeeper createZookeeper() {
        String connectString = Constant.DEFAULT_ZK;
        int sessionTimeout = Constant.TIME_OUT;

        return createZookeeper(connectString, sessionTimeout);
    }

    public static ZooKeeper createZookeeper(String connectString, int sessionTimeout) {
        CountDownLatch countDownLatch = new CountDownLatch(1);


        try {
            // 创建zookeeper实例，建立连接
            final ZooKeeper zooKeeper = new ZooKeeper(connectString, sessionTimeout, event -> {
                // 只有连接成功才放行
                switch (event.getState()) {
                    case SyncConnected -> {
                       log.debug("zookeeper 客户端连接成功");
                        countDownLatch.countDown();
                    }
                    case AuthFailed -> System.out.println("注册失败");
                    default -> System.out.println("default status");
                }
            });

            // 等待连接成功
            countDownLatch.await();

            return zooKeeper;

        } catch (IOException | InterruptedException e) {
            log.error("创建zookeeper实例时发生异常");
            throw new ZookeeperException(e);
        }
    }

    /**
     * 创建一个zookeeper节点
     *
     * @param zooKeeper  zk实例
     * @param node       node列表
     * @param watcher    自定义watcher
     * @param createMode node的模式
     * @return 创建状态
     */
    public static boolean createNode(
            ZooKeeper zooKeeper,
            ZookeeperNode node,
            Watcher watcher,
            CreateMode createMode) {

        try {
            if (zooKeeper.exists(node.getNodePath(), watcher) == null) {
                String result = zooKeeper.create(node.getNodePath(), node.getData(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.info("根节点[{}]，成功创建", result);
                return true;
            } else {
                if (log.isDebugEnabled()) {
                    log.info("[{}]节点已经存在无需创建", node.getNodePath());
                }
                return false;
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("创建基础目录是发生异常", e);
            throw new ZookeeperException(e);
        }

    }

    /**
     * 关闭zookeeper的方法
     * @param zooKeeper zookeeper 实例
     */
    public static void close(ZooKeeper zooKeeper) {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            log.error("关闭zookeeper时发生错误");
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断节点是否村
     * @param zooKeeper zookeeper 实例
     * @param node 节点路径
     * @param watcher watcher
     * @return true 存在 | false 不存在
     */
    public static boolean exist(ZooKeeper zooKeeper, String node, Watcher watcher) {
        try {
            return zooKeeper.exists(node, watcher) != null;
        } catch (KeeperException | InterruptedException e) {
            log.error("判断[{}]节点是否存在",node,e);
            throw new ZookeeperException(e);
        }
    }

    /**
     * 查询一个节点的子元素
     *
     * @param zookeeper   zk实例
     * @param serviceFullName 服务节点
     * @return 子元素列表
     */
    public static List<String> getChildren(ZooKeeper zookeeper, String serviceFullName, Watcher watcher) {

        try {
            return zookeeper.getChildren(serviceFullName, watcher);
        } catch (KeeperException | InterruptedException e) {
            log.error("获取节点[{}]的元素时发生异常", serviceFullName, e);
            throw new RuntimeException(e);
        }


    }
}
