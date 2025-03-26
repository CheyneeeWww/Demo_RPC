package org.example.client.serviceCenter;

import org.example.client.cache.serviceCache;
import org.example.client.serviceCenter.ZkWatcher.watchZK;
import org.example.client.serviceCenter.balance.LoadBalance;
import org.example.client.serviceCenter.balance.impl.ConsistencyHashBalance;
import org.example.common.Message.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author cnwang
 * @Date created in 12:46 2025/3/14
 */
@Slf4j
public class ZKServiceCenter implements ServiceCenter {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY = "CanRetry";
    private serviceCache cache;

    private final LoadBalance loadBalance = new ConsistencyHashBalance();

    public ZKServiceCenter() throws InterruptedException{
        RetryPolicy policy = new ExponentialBackoffRetry(1000,3);
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        log.info("Zookeeper 连接成功");
        cache = new serviceCache();
        watchZK watcher = new watchZK(client,cache);
        watcher.watchToUpdate(ROOT_PATH);
    }
    @Override
    public InetSocketAddress serviceDiscovery(RpcRequest request){
        String serviceName = request.getInterfaceName();
        try {
            //先从本地缓存中找
            List<String> addressList = cache.getServiceListFromCache(serviceName);
            //如果找不到，再去zookeeper中找
            //这种i情况基本不会发生，或者说只会出现在初始化阶段
            if (addressList == null) {
                addressList = client.getChildren().forPath("/" + serviceName);
                // 如果本地缓存中没有该服务名的地址列表，则添加
                List<String> cachedAddresses = cache.getServiceListFromCache(serviceName);
                if (cachedAddresses == null || cachedAddresses.isEmpty()) {
                    // 假设 addServiceToCache 方法可以处理单个地址
                    for (String address : addressList) {
                        cache.addServiceToCache(serviceName, address);
                    }
                }
            }
            if (addressList.isEmpty()) {
                log.warn("未找到服务：{}", serviceName);
                return null;
            }
            // 负载均衡得到地址
            String address = loadBalance.balance(addressList);
            return parseAddress(address);
        } catch (Exception e) {
            log.error("服务发现失败，服务名：{}", serviceName, e);
        }
        return null;
    }

    private Set<String> retryServiceCache = new CopyOnWriteArraySet<>();

    @Override
    public boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature){
        if (retryServiceCache.isEmpty()) {
            try {
                CuratorFramework rootClient = client.usingNamespace(RETRY);
                List<String> retryableMethods = rootClient.getChildren().forPath("/" + getServiceAddress(serviceAddress));
                retryServiceCache.addAll(retryableMethods);
            } catch (Exception e) {
                log.error("检查重试失败，方法签名：{}", methodSignature, e);
            }
        }
        return retryServiceCache.contains(methodSignature);
    }

    @Override
    public void close() {
        client.close();
    }

    // 将InetSocketAddress解析为格式为ip:port的字符串
    private String getServiceAddress(InetSocketAddress serverAddress){
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    // 字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }

}
