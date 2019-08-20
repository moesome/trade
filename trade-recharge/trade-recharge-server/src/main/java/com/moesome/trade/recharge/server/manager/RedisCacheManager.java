package com.moesome.trade.recharge.server.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheManager implements CacheManager {
    @Autowired
    private RedisTemplate<String,Long> redisTemplateForRechargeId;

    @Override
    public boolean saveRechargeId(Long id){
        return redisTemplateForRechargeId.opsForSet().add("rechargeId",id) == 1;
    }

    @Override
    public void removeRechargeId(Long id){
        redisTemplateForRechargeId.opsForSet().remove("rechargeId",id);
    }
}
