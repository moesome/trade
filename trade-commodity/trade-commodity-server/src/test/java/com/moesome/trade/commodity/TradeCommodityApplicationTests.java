package com.moesome.trade.commodity;


import com.moesome.trade.commodity.server.TradeCommodityServerApplication;
import com.moesome.trade.commodity.server.manager.DistributedLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeCommodityServerApplication.class)
public class TradeCommodityApplicationTests {
    @Autowired
    DistributedLock distributedLock;

    @Test
    public void lockTest() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,10,1000, TimeUnit.SECONDS, new LinkedBlockingDeque(10));
        executor.execute(new Runnable() {
            @Override
            public void run() {
                distributedLock.lock(1l);
                distributedLock.unlock(1l);
            }
        });
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                distributedLock.lock(1l);
                try {
                    Thread.sleep(13000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                distributedLock.unlock(1l);
            }
        });
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                distributedLock.lock(1l);
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                distributedLock.unlock(1l);
            }
        });
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                distributedLock.lock(1l);
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                distributedLock.unlock(1l);
            }
        });
        Thread.sleep(1000000L);
    }

    @Autowired
    private RedisTemplate<String,Object> redisTemplateForDistributedLock;

    private String getKey(Long id){
        return "commodity"+":"+id;
    }

    private String getValue(){
        return UUID+":"+Thread.currentThread().getId();
    }

    private final String UUID = java.util.UUID.randomUUID().toString();
    @Test
    public void luaTest(){
        List<String> keys = Collections.singletonList(getKey(1l));
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText("redis.call('set',ARGV[1],KEYS[1])");
        defaultRedisScript.setResultType(Long.class);
        Long result = redisTemplateForDistributedLock.execute(defaultRedisScript, keys, getValue());
    }
}
