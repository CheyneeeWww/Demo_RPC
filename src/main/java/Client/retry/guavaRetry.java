package Client.retry;

import Client.rpcClient.RpcClient;
import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import common.Message.RpcRequest;
import common.Message.RpcResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author cnwang
 * @Date created in 2:41 2025/3/25
 */
public class guavaRetry {
    //用来发送RPC请求
    private RpcClient rpcClient;

    public RpcResponse sendServiceWithRetry(RpcRequest request, RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfException()
                //重试会在请求发生异常或返回状态码为500时进行
                .retryIfResult(response -> Objects.equals(response.getCode(), 500))
                //每次重试之间固定等待2秒
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                //最多重试3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                //用于为 Retryer 配置一个重试监听器
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("RetryListener: 第" + attempt.getAttemptNumber() + "次调用");
                    }
                })
                .build();
        try {
            // retryer.call执行RPC请求，进行重试。
            //传入的 Lambda 表达式是要执行的操作，即 rpcClient.sendRequest(request)，这会发送请求并返回一个 RpcResponse 对象。
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RpcResponse.fail();
    }
}
