package org.example.client.serviceCenter.balance.impl;
import org.example.client.serviceCenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
/**
 * @Author cnwang
 * @Date created in 2:22 2025/3/25
 */
@Slf4j
public class ConsistencyHashBalance implements LoadBalance {
    // 虚拟节点个数
    private static final int VIRTUAL_NUM = 5;
    // 保存虚拟节点的hash值和对应的虚拟节点,key为hash值，value为虚拟节点的名称
    private SortedMap<Integer, String> shards = new TreeMap<Integer, String>();
    // 真实节点列表
    private List<String> realNodes = new LinkedList<String>();

    // 获取虚拟节点的个数
    public static int getVirtualNum() {
        return VIRTUAL_NUM;
    }

    //该方法初始化负载均衡器，将真实的服务节点和对应的虚拟节点添加到哈希环中。
    private void init(List<String> serviceList) {
        for (String server : serviceList) {
            realNodes.add(server);
            log.info("真实节点[{}] 被添加", server);
            //遍历 serviceList（真实节点列表），每个真实节点都会生成 VIRTUAL_NUM 个虚拟节点，并计算它们的哈希值
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                //虚拟节点的命名规则是 server&&VN<i>，其中 <i> 是虚拟节点的编号。
                String virtualNode = server + "&&VN" + i;
                //使用 getHash 方法计算每个虚拟节点的哈希值，并将其加入 shards 中。
                int hash = getHash(virtualNode);
                //shards 是一个 SortedMap，会根据哈希值对虚拟节点进行排序。
                shards.put(hash, virtualNode);
                log.info("虚拟节点[{}] hash:{}，被添加", virtualNode, hash);
            }
        }
    }
    //根据请求的 node（比如某个请求的标识符），选择一个服务器节点。
    public String getServer(String node, List<String> serviceList) {
        if (shards.isEmpty()) {
            init(serviceList);  // 初始化，如果shards为空
        }
        //通过 getHash(node) 计算请求的哈希值。
        int hash = getHash(node);
        Integer key = null;
        //使用 shards.tailMap(hash) 获取 hash 值大于等于请求哈希值的所有虚拟节点。
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        //如果没有找到，意味着请求的哈希值大于所有虚拟节点的哈希值，选择哈希值最大的虚拟节点。
        //否则，选择 tailMap 中第一个虚拟节点。
        if (subMap.isEmpty()) {
            key = shards.firstKey();
        } else {
            key = subMap.firstKey();
        }
        //返回真实节点：从选中的虚拟节点 virtualNode 中提取出真实节点的名称（即虚拟节点名称去掉 &&VN<i> 部分）。
        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }
    //添加一个新的真实节点及其虚拟节点到哈希环中。
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            log.info("真实节点[{}] 上线添加", node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                log.info("虚拟节点[{}] hash:{}，被添加", virtualNode, hash);
            }
        }
    }
    //删除一个真实节点及其对应的虚拟节点。
    public void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            log.info("真实节点[{}] 下线移除", node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                log.info("虚拟节点[{}] hash:{}，被移除", virtualNode, hash);
            }
        }
    }
    // FNV1_32_HASH算法
    //该方法用于计算字符串的哈希值，哈希算法基于一个常见的算法，使用了 FNV-1a 哈希和一些额外的位运算，确保哈希值均匀分布
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }
    //模拟负载均衡，通过生成一个随机字符串（random）来模拟请求，最终通过一致性哈希选择一个服务器
    @Override
    public String balance(List<String> addressList) {
        //生成一个随机字符串（UUID），然后将其作为 node 参数传递给 getServer 方法，来获取相应的服务器地址。

        if (addressList == null || addressList.isEmpty()) {
            throw new IllegalArgumentException("Address list cannot be null or empty");
        }

        String random = UUID.randomUUID().toString();
        return getServer(random, addressList);
    }
    public SortedMap<Integer, String> getShards() {
        return shards;
    }

    public List<String> getRealNodes() {
        return realNodes;
    }
    @Override
    public String toString() {
        return "ConsistencyHash";
    }
}