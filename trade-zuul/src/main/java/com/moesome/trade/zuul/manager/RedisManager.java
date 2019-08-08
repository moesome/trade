package com.moesome.trade.zuul.manager;

import com.moesome.trade.user.common.response.vo.UserDetailVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisManager {

    @Autowired
    private RedisTemplate<String, UserDetailVo> redisTemplateForUserDetailVo;

    /**
     * 根据 sessionId 读出 UserDetailVo
     * @param sessionId
     * @return
     */
    public UserDetailVo getUserDetailVoBySessionId(String sessionId){
        return redisTemplateForUserDetailVo.opsForValue().get(sessionId);
    }
}
