package org.xiaoheshan;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class MyWatch implements Watcher {
    @Override
    public void process(WatchedEvent event) {
        // 判断事件类型，是不是连接类型的事件
        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected  -> System.out.println("zookeeper连接成功");
                case AuthFailed     -> System.out.println("zookeeper认证失败");
                case Disconnected   -> System.out.println("zookeeper断开连接");
            }
        } else if (event.getType() == Event.EventType.NodeCreated) {
            System.out.println(event.getPath() + " zookeeper节点被创建");
        } else if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println(event.getPath() + " zookeeper节点被删除");
        }

    }
}
