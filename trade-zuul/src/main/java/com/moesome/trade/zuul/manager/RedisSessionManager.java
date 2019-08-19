package com.moesome.trade.zuul.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisSessionManager implements SessionManager{
    public static final int SESSION_EXPIRE_DAY = 15;

    @Autowired
    private RedisTemplate<String, Map<String, Object>> redisTemplateForSession;


    @Override
    @SuppressWarnings("unchecked")
    public <T> T getSessionMessage(String sessionId, String messageKey) {
       return (T)redisTemplateForSession.opsForHash().get(sessionId, messageKey);
    }
}
