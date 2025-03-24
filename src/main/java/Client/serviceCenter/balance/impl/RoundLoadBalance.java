package Client.serviceCenter.balance.impl;

import Client.serviceCenter.balance.LoadBalance;

import java.util.List;

/**
 * @Author cnwang
 * @Date created in 2:17 2025/3/25
 */
public class RoundLoadBalance implements LoadBalance {
    private int choose = -1;
    @Override
    public String balance(List<String> addressList){
        choose++;
        choose =choose % addressList.size();
        System.out.println("负载均衡选择了"+"服务器");
        return addressList.get(choose);
    }
    @Override
    public void addNode(String node){

    }
    @Override
    public void delNode(String node){

    }
}
