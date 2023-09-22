package org.xiaoheshan;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ZookeeperTest {

    ZooKeeper zooKeeper = null;

    // 定义连接参数
    String connectString = "127.0.0.1:21810";
    // 定义超时时间
    int sessionTimeout = 10000;


    @Before
    public void creatZk() {
        try {
            this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreatPNode() {
        try {
            String result = zooKeeper.create("/xiaoheshan", "hello".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("result = " + result);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
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
            zooKeeper.delete("/xiaoheshan", -1);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testExsitNode() {
        try {
            zooKeeper.setData("/xiaoheshan", "hello".getBytes(), -1);

            Stat exists = zooKeeper.exists("/xiaoheshan", null);
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
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
