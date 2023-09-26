package org.xiaoheshan;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperTest {

    ZooKeeper zooKeeper = null;
    CountDownLatch countDownLatch = new CountDownLatch(1);

    // 定义连接参数
    String connectString = "127.0.0.1:21810";
    // 定义超时时间
    int sessionTimeout = 10000;


    @Before
    public void createZk() {
        try {
            // new MyWatch() 默认的监听器
            this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, event -> {
                // 只有连接成功才放行
                switch (event.getState()) {
                    case SyncConnected -> {
                        System.out.println("客户端连接成功");
                        countDownLatch.countDown();
                    }
                    case AuthFailed -> System.out.println("注册失败");

                    default -> System.out.println("default status");

                }

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreatePNode() {
        try {
            // 会等待连接成功
            countDownLatch.await();
            String result = zooKeeper.create("/xiaoheshan", "hello".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("result = " + result);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.zooKeeper != null) {
                try {
                    this.zooKeeper.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Test
    public void testDeleteNode() {
        try {
            // version: 乐观锁  version不匹配，删除失败，也可以无视版本号
            this.zooKeeper.delete("/xiaoheshan", -1);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.zooKeeper != null) {
                try {
                    this.zooKeeper.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testExistNode() {
        try {
            this.zooKeeper.setData("/xiaoheshan", "hello".getBytes(), -1);

            Stat exists = this.zooKeeper.exists("/xiaoheshan", null);
            // 当前节点的数据版本
            int version = exists.getVersion();
            // 当前节点的acl数据版本
            int aversion = exists.getAversion();
            // 当前子节点的数据版本
            int cversion = exists.getCversion();

            System.out.println("aversion = " + aversion);
            System.out.println("version = " + version);
            System.out.println("cversion = " + cversion);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.zooKeeper != null) {
                try {
                    this.zooKeeper.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Test
    public void testWatch() {
        try {
            this.zooKeeper.exists("/xiaoheshan", true);

        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.zooKeeper != null) {
                try {
                    this.zooKeeper.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
