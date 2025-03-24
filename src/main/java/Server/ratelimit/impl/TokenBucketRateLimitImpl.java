package Server.ratelimit.impl;

import Server.ratelimit.RateLimit;

/**
 * @Author cnwang
 * @Date created in 3:22 2025/3/25
 */
public class TokenBucketRateLimitImpl implements RateLimit {
    private static int RATE;
    private static int CAPACITY;
    private volatile int curCapcity;
    private volatile long timeStamp=System.currentTimeMillis();
    public TokenBucketRateLimitImpl(int rate,int capacity){
        RATE =rate;
        CAPACITY=capacity;
    }
    @Override
    public synchronized boolean getToken(){
        if(curCapcity>0){
            curCapcity--;
            return true;
        }
        long current=System.currentTimeMillis();
        if(current-timeStamp>=RATE){
            if((current-timeStamp)/RATE>=2){
                curCapcity+=(int)(current-timeStamp)/RATE-1;
            }
            //保持桶内令牌容量<=10
            if(curCapcity>CAPACITY) curCapcity=CAPACITY;
            //刷新时间戳为本次请求
            timeStamp=current;
            return true;
        }
        return false;
    }
}
