package com.moesome.trade.order.server.test;

import com.moesome.trade.order.common.request.CommodityOrderVo;
import com.moesome.trade.order.server.TradeOrderServerApplication;
import com.moesome.trade.order.server.manager.CacheManager;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeOrderServerApplication.class)
public class Test {

    @Autowired
    private CacheManager cacheManager;

    @org.junit.Test
    public void test(){
    }
}
