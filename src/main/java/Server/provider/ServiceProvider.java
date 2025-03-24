package Server.provider;

import Server.ratelimit.provider.RateLimitProvider;
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

    //限流器
    private RateLimitProvider rateLimitProvider;
    public ServiceProvider(String host,int port){
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister=new ZKServiceRegister();
        this.rateLimitProvider=new RateLimitProvider();
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
    public RateLimitProvider getRateLimitProvider(){
        return rateLimitProvider;
    }
}
