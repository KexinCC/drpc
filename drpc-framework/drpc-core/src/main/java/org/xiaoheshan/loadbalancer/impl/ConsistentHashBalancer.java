package org.xiaoheshan.loadbalancer.impl;

import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.DrpcBootstrap;
import org.xiaoheshan.loadbalancer.AbstractLoadBalancer;
import org.xiaoheshan.loadbalancer.Selector;
import org.xiaoheshan.transport.message.DrpcRequest;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
public class ConsistentHashBalancer extends AbstractLoadBalancer {

    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new ConsistentHashSelector(serviceList, 128);
    }

    /**
     * 一致性哈希的具体算法实现
     */
    private static class ConsistentHashSelector implements Selector {

        // hash环用来存储服务器节点
        private SortedMap<Integer, InetSocketAddress> circle = new TreeMap<>();
        // 虚拟节点的个数
        private int virtualNodes;

        public InetSocketAddress getNext() {
            DrpcRequest drpcRequest = DrpcBootstrap.REQUEST_THREAD_LOCAL.get();

            String requestId = String.valueOf(drpcRequest.getRequestId());

            // 对请求的id做hash
            int hash = hash(requestId);

            if (!circle.containsKey(hash)) {
                SortedMap<Integer, InetSocketAddress> tailMap = circle.tailMap(hash);
                hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
                return tailMap.get(hash);
            }
            return circle.get(hash);
        }


        public ConsistentHashSelector(List<InetSocketAddress> serviceList,int virtualNodes) {
            this.virtualNodes = virtualNodes;
            for (InetSocketAddress inetSocketAddress : serviceList) {
                addNodeToCircle(inetSocketAddress);
            }
        }

        /**
         * 将每个节点挂载在哈希环上
         * @param inetSocketAddress 节点的地址
         */
        private void addNodeToCircle(InetSocketAddress inetSocketAddress) {
            // 为每一个节点生成虚拟的节点进行挂在
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(inetSocketAddress.toString() + "-" + i);
                circle.put(hash, inetSocketAddress);
                if (log.isDebugEnabled()) {
                    log.debug("hash为[{}]的节点已经挂载在了哈希环上", hash);
                }
            }
        }

        private void removeFromCircle(InetSocketAddress inetSocketAddress) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(inetSocketAddress.toString() + "-" + i);
                circle.remove(hash, inetSocketAddress);
            }
        }

        /**
         * 具体的 hash 算法
         * @param s inetSocketAddress.toString()
         * @return hash
         */
        private int hash(String s) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            byte[] digest = md.digest(s.getBytes());

            int res = 0;
            for (int i = 0; i < 4; i++) {
                res = res << 8;
                if (digest[i] < 0 ){
                    res = res | (digest[i] & 255);
                } else {
                    res = res | digest[i];
                }
            }

            return res;
        }

        @Override
        public void reBalance() {

        }
    }
}
