package Client.serviceCenter;

import Client.cache.serviceCache;
import Client.serviceCenter.ZkWatcher.watchZK;
import Client.serviceCenter.balance.impl.ConsistencyHashBalance;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author cnwang
 * @Date created in 12:46 2025/3/14
 */
public class ZKServiceCenter implements ServiceCenter {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRPC";
    private serviceCache cache;

    public ZKServiceCenter() throws InterruptedException{
        RetryPolicy policy = new ExponentialBackoffRetry(1000,3);
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
        this.cache = new serviceCache();
        watchZK watcher = new watchZK(client,cache);
        watcher.watchToUpdate(ROOT_PATH);
    }
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName){
        try{
            List<String> serviceList = cache.getServiceFromCache(serviceName);
            //List<String> strings = client.getChildren().forPath("/"+serviceName);
            if(serviceList == null){
                serviceList = client.getChildren().forPath("/"+serviceName);
            }
            String address = new ConsistencyHashBalance().balance(serviceList);
            return parseAddress(address);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private String getServiceAddress(InetSocketAddress serverAddress){
        return serverAddress.getHostName()+
                "+"+
                serverAddress.getPort();
    }
    private InetSocketAddress parseAddress(String address){
    String[] result = address.split(":");
    return new InetSocketAddress(result[0],Integer.parseInt(result[1]));
    }

}
