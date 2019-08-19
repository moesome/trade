package com.moesome.trade.commodity;


import com.moesome.trade.commodity.server.TradeCommodityServerApplication;
import com.moesome.trade.commodity.server.manager.CacheManager;
import com.moesome.trade.common.manager.DistributedLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeCommodityServerApplication.class)
public class TradeCommodityApplicationTests {
    @Autowired
    DistributedLock distributedLock;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void test() throws InterruptedException {
        cacheManager.getCommodityDetailVo(-6661l);
        cacheManager.getCommodityDetailVo(-6661l);
        cacheManager.getCommodityDetailVo(-6661l);
    }

}
