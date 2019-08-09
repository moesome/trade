package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheManager implements CacheManager{

    // 应对缓存穿透
    private static final CommodityDetailVo TEMP_COMMODITY = new CommodityDetailVo();


    @Autowired
    private RedisTemplate<String,Object> redisTemplateForCommodityDetailVo;
    /**
     * 保存商品部分信息（stock,price,startAt,endAt）到 redis，用于下单前检测
     * @param commodityDetailVo
     */

    @Override
    public void saveCommodityDetailVo(CommodityDetailVo commodityDetailVo){
        saveCommodityDetailVo(commodityDetailVo,-1L);
    }


    @Override
    public void saveCommodityDetailVo(CommodityDetailVo commodityDetailVo,Long expireSecond){
        HashMap<String, Object> stringObjectHashMap = new HashMap<>(9);
        stringObjectHashMap.put("name",commodityDetailVo.getName());
        stringObjectHashMap.put("userId",commodityDetailVo.getUserId());
        stringObjectHashMap.put("detail",commodityDetailVo.getDetail());
        stringObjectHashMap.put("stock",commodityDetailVo.getStock());
        stringObjectHashMap.put("price",commodityDetailVo.getPrice());
        stringObjectHashMap.put("startAt",commodityDetailVo.getStartAt());
        stringObjectHashMap.put("endAt",commodityDetailVo.getEndAt());
        stringObjectHashMap.put("createdAt",commodityDetailVo.getCreatedAt());
        stringObjectHashMap.put("updatedAt",commodityDetailVo.getUpdatedAt());
        redisTemplateForCommodityDetailVo.opsForHash().putAll(getKey(commodityDetailVo.getId()),stringObjectHashMap);
        if (expireSecond > 0){
            redisTemplateForCommodityDetailVo.expire(getKey(commodityDetailVo.getId()),expireSecond,TimeUnit.SECONDS);
        }
    }

    /**
     * 取出商品部分信息（price,startAt,endAt）
     * 返回 null 表示缓存中没有查到
     * 返回空 CommodityDetailVo 对象表示缓存中查到了防止缓存穿透的临时值
     * @param commodityId
     * @return
     */
    @Override
    public CommodityDetailVo getCommodityDetailVo(Long commodityId){
        // 从缓存取信息用于校验
        ArrayList<Object> list = new ArrayList<>(9);
        list.add("name");
        list.add("userId");
        list.add("detail");
        list.add("stock");
        list.add("price");
        list.add("startAt");
        list.add("endAt");
        list.add("createdAt");
        list.add("updatedAt");
        List<Object> multiGet = redisTemplateForCommodityDetailVo.opsForHash().multiGet(getKey(commodityId),list);
        // 查询到发生缓存穿透时存入的临时值，直接返回，跳过查询数据库阶段
        if (multiGet.get(3) != null && Integer.MIN_VALUE == (int)multiGet.get(3)){
            return TEMP_COMMODITY;
        }
        // 只要有一个为空则不合法
        for (Object obj : multiGet){
            if (obj == null){
                return null;
            }
        }
        CommodityDetailVo commodityDetailVo = new CommodityDetailVo();
        commodityDetailVo.setId(commodityId);
        commodityDetailVo.setName((String)multiGet.get(0));
        commodityDetailVo.setUserId(Long.valueOf(multiGet.get(1).toString()));
        commodityDetailVo.setDetail((String) multiGet.get(2));
        commodityDetailVo.setStock((Integer) multiGet.get(3));
        commodityDetailVo.setPrice((BigDecimal) multiGet.get(4));
        commodityDetailVo.setStartAt((Date)multiGet.get(5));
        commodityDetailVo.setEndAt((Date)multiGet.get(6));
        commodityDetailVo.setCreatedAt((Date)multiGet.get(7));
        commodityDetailVo.setUpdatedAt((Date)multiGet.get(8));
        return commodityDetailVo;
    }

    @Override
    public void removeCommodityDetailVo(Long commodityId) {
        redisTemplateForCommodityDetailVo.delete(getKey(commodityId));
    }

    private String getKey(Long commodityId){
        return "commodityDetailVo" + commodityId;
    }
}
