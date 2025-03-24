package Server.provider;

import Server.serviceRegister.ServiceRegister;
import Server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author cnwang
 * @Date created in 21:37 2025/3/12
 */
public class ServiceProvider {
    private Map<String,Object> interfaceProvider;
    private int port;
    private String host;
    private ServiceRegister serviceRegister;

    public ServiceProvider(String host,int port){
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister=new ZKServiceRegister();
    }
    public void provideServiceInterface(Object service,boolean canRetry){
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for(Class<?> clazz:interfaceName){
            interfaceProvider.put(clazz.getName(),service);
            serviceRegister.register(clazz.getName(),new InetSocketAddress(host,port),canRetry);
        }
    }
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}
