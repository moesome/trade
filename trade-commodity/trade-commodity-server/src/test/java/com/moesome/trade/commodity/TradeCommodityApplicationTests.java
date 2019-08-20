package com.moesome.trade.commodity;


import com.moesome.trade.commodity.server.TradeCommodityServerApplication;
import com.moesome.trade.commodity.server.manager.CacheManager;
import com.moesome.trade.commodity.server.model.dao.CommodityMapper;
import com.moesome.trade.commodity.server.model.domain.Commodity;
import com.moesome.trade.common.manager.DistributedLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeCommodityServerApplication.class)
public class TradeCommodityApplicationTests {
    @Autowired
    CommodityMapper commodityMapper;
    @Test
    public void test() throws InterruptedException {
        List<Commodity> commodityList = commodityMapper.selectByCommodityIdList(Collections.singletonList(314L));
        System.out.println(commodityList);
    }

}
