package Server.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author cnwang
 * @Date created in 21:37 2025/3/12
 */
public class ServiceProvider {
    private Map<String,Object> interfaceProvider;
    public ServiceProvider(){
        this.interfaceProvider = new HashMap<>();
    }
    public void provideServiceInterface(Object service){
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for(Class<?> clazz:interfaceName){
            interfaceProvider.put(clazz.getName(),service);
        }
    }
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}
