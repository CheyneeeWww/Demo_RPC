package Client.circuitBreaker;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author cnwang
 * @Date created in 3:58 2025/3/25
 */
public class CircuitBreakerProvider {

    // 用一个 Map 存储每个服务的熔断器实例，key 为服务名，value 为该服务的熔断器
    private Map<String, CircuitBreaker> circuitBreakerMap = new HashMap<>();

    // 根据服务名获取对应的熔断器
    public synchronized CircuitBreaker getCircuitBreaker(String serviceName) {
        CircuitBreaker circuitBreaker;

        // 检查是否已经存在该服务的熔断器
        if (circuitBreakerMap.containsKey(serviceName)) {
            // 如果存在，则直接返回对应的熔断器
            circuitBreaker = circuitBreakerMap.get(serviceName);
        } else {
            // 如果不存在，则创建一个新的熔断器
            System.out.println("serviceName=" + serviceName + " 创建一个新的熔断器");
            circuitBreaker = new CircuitBreaker(1, 0.5, 10000); // 这里传入熔断器的参数：失败阈值1，成功率50%，重置时间周期10000毫秒
            circuitBreakerMap.put(serviceName, circuitBreaker); // 将新创建的熔断器存入 Map
        }

        return circuitBreaker;
    }
}
