package com.moesome.trade.user.server.service;

import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.manager.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserClientServiceImpl implements UserClientService{
    @Autowired
    private CacheManager cacheManager;

    @Override
    public UserDetailVo getUserDetailVoById(Long userId) {
        return cacheManager.getUserDetailVo(userId);
    }
}
