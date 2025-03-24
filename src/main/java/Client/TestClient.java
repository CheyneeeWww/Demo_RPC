package Client;

import Client.proxy.ClientProxy;
import common.pojo.User;
import common.service.UserService;

/**
 * @Author cnwang
 * @Date created in 21:14 2025/3/12
 */
public class TestClient {
    public static void main(String[] args) throws InterruptedException{
        ClientProxy clientProxy = new ClientProxy();
        //ClientProxy clientProxy = new ClientProxy("127.0.0.1",9999,1);
        UserService proxy = clientProxy.getProxy(UserService.class);
        User user = proxy.getUserByUserId(2);
        System.out.println("从服务端得到的User"+user.toString());
        User u = User.builder().id(100).userName("wcn").sex(true).build();
        Integer id = proxy.insertUserId(u);
        System.out.println("向服务端插入user的id"+id);
    }
}
