package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.commodity.common.response.vo.CommodityDetailVo;
import com.moesome.trade.commodity.server.model.dao.CommodityMapper;
import com.moesome.trade.commodity.server.model.domain.Commodity;
import com.moesome.trade.commodity.server.util.Transform;
import com.moesome.trade.common.exception.DistributedLockException;
import com.moesome.trade.common.manager.DistributedLock;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RedisCacheManager implements CacheManager{
    // 应对缓存穿透
    private static final CommodityDetailVo TEMP_COMMODITY;
    private static final Integer TEMP_COMMODITY_STOCK = Integer.MIN_VALUE;
    static{
        TEMP_COMMODITY = new CommodityDetailVo();
        TEMP_COMMODITY.setStock(TEMP_COMMODITY_STOCK);
    }
    @Autowired
    private RedisTemplate<String,Object> redisTemplateForCommodityDetailVo;

    @Autowired
    private CommodityMapper commodityMapper;

    @Autowired
    private DistributedLock distributedLock;

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

    @Override
    public CommodityDetailVo getCommodityDetailVo(Long commodityId){
        log.debug("从缓存中取 commodityId: "+commodityId);
        CommodityDetailVo commodityDetailVo = doGet(commodityId);
        // 发现缓存失效
        if (commodityDetailVo == null){
            log.debug("发现缓存失效 commodityId: "+commodityId);
            try{
                // 加锁防止多线程更新缓存
                distributedLock.lock(commodityId);
                // 再次校验是否被其他线程更新缓存
                commodityDetailVo = doGet(commodityId);
                // 如果没有被更新则开始更新
                if (commodityDetailVo == null){
                    log.debug("开始更新缓存 commodityId: "+commodityId);
                    // 查出数据
                    Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);
                    log.debug("查出数据 commodity: "+commodity);
                    // 转化数据，若为空则不会进行转化
                    commodityDetailVo = Transform.transformCommodityToCommodityDetailVo(commodity);
                    // 数据查询为空，表明发生缓存穿透
                    if (commodityDetailVo == null){
                        log.debug("查出数据为空，存入临时值 TEMP_COMMODITY: "+TEMP_COMMODITY);
                        // 存入临时值
                        commodityDetailVo = TEMP_COMMODITY;
                        TEMP_COMMODITY.setId(commodityId);
                        saveCommodityDetailVo(TEMP_COMMODITY,10L);
                    }else{
                        log.debug("查出数据不为空，存入 commodityDetailVo: "+commodityDetailVo);
                        saveCommodityDetailVo(commodityDetailVo);
                    }
                }
            }catch (DistributedLockException e){
                // 抛出错误
                throw e;
            } finally {
                distributedLock.unlock(commodityId);
            }
        }
        // 直接查到临时值或拿锁后查到临时值，或插入临时值都将导致进入该条件
        // commodityDetailVo 为处理后的值，可用 == 直接判断对象是否为同一个
        if (commodityDetailVo == TEMP_COMMODITY){
            log.debug("返回结果：发生缓存穿透");
            // 发生缓存穿透，返回空值表示异常
            return null;
        }
        log.debug("返回结果 commodityDetailVo: "+commodityDetailVo);
        return commodityDetailVo;
    }

    /**
     * 查询 commodity
     * @param commodityId
     * @return null 则缓存失效，TEMP_COMMODITY 则缓存穿透
     */
    private CommodityDetailVo doGet(Long commodityId){
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
        log.debug("查询到的数据为: "+multiGet.toString());
        // 查看 id 字段值
        if(multiGet.get(3) == null){
            log.debug("检测到空值，返回 null");
            return null;
        } else if ((int)multiGet.get(3) == TEMP_COMMODITY_STOCK){
            log.debug("检测到缓存穿透临时值，返回 TEMP_COMMODITY");
            // 缓存穿透
            return TEMP_COMMODITY;
        }else{
            // 检查各个字段是否有空值
            for (Object obj : multiGet){
                if (obj == null){
                    log.debug("检测到空值，返回 null");
                    return null;
                }
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

    @Override
    public boolean decrementStock(Long commodityId) {
        return redisTemplateForCommodityDetailVo.opsForHash().increment(getKey(commodityId),"stock",-1) >= 0;
    }

    @Override
    public void incrementStock(Long commodityId) {
        redisTemplateForCommodityDetailVo.opsForHash().increment(getKey(commodityId),"stock",1);
    }

    private String getKey(Long commodityId){
        return "commodityDetailVo" + commodityId;
    }
}
