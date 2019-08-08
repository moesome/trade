package com.moesome.trade.user.server.manager;

import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheManager implements CacheManager{
    @Autowired
    private RedisTemplate<String, UserDetailVo> redisTemplateForUser;

    @Override
    public String saveUserDetailVoAndGenerateSessionId(UserDetailVo userDetailVo){
        String sessionId = UUID.randomUUID().toString().replace("-","");
        redisTemplateForUser.opsForValue().set(sessionId, userDetailVo, RedisConfig.EXPIRE_SECOND, TimeUnit.SECONDS);
        return sessionId;
    }

    @Override
    public String saveUserDetailVo(UserDetailVo userDetailVo,String sessionId){
        redisTemplateForUser.opsForValue().set(sessionId, userDetailVo, RedisConfig.EXPIRE_SECOND,TimeUnit.SECONDS);
        return sessionId;
    }

    @Override
    public UserDetailVo getUserDetailVoBySessionId(String sessionId){
        return redisTemplateForUser.opsForValue().get(sessionId);
    }

    @Override
    public void refreshUserDetailVo(String sessionId, int time){
        redisTemplateForUser.expire(sessionId,time, TimeUnit.SECONDS);
    }
}
