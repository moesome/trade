package com.moesome.trade.order.server.manager;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.order.common.request.CommodityOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheManager implements CacheManager {

    public static final int SUCCESS = 1;
    public static final int FAILED = -1;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate<String, String> redisTemplateForCommodityOrderVo;

    @Override
    public boolean saveCommodityOrderVo(CommodityOrderVo commodityOrderVo) {
        return Objects.equals(redisTemplateForCommodityOrderVo.opsForSet().add("commodity_order_vo", generateCommodityOrderVoValue(commodityOrderVo)), 1L);
    }

    @Override
    public void removeCommodityOrderVo(CommodityOrderVo commodityOrderVo) {
        redisTemplateForCommodityOrderVo.opsForSet().remove("commodity_order_vo",generateCommodityOrderVoValue(commodityOrderVo));
    }

    @Override
    public void saveCommodityOrderResult(Long orderId,int result) {
        redisTemplateForCommodityOrderVo.opsForValue().set("commodity_order_result:"+orderId,String.valueOf(result),1, TimeUnit.MINUTES);
    }

    @Override
    public int getCommodityOrderResult(Long orderId) {
        String s = redisTemplateForCommodityOrderVo.opsForValue().get("commodity_order_result:" + orderId);
        if (s == null){
            return 0;
        }
        return Integer.parseInt(s);
    }

    private String generateCommodityOrderVoValue(CommodityOrderVo commodityOrderVo){
        return "userId:"+commodityOrderVo.getUserId()+"commodityId:"+commodityOrderVo.getCommodityId();
    }
}
