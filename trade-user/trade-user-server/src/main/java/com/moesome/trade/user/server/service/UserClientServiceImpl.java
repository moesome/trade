package com.moesome.trade.user.server.service;

import com.moesome.trade.common.manager.DistributedLock;
import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.manager.CacheManager;
import com.moesome.trade.user.server.model.dao.UserMapper;
import com.moesome.trade.user.server.model.domain.User;
import com.moesome.trade.user.server.util.Transform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserClientServiceImpl implements UserClientService{
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public UserDetailVo getUserDetailVoById(Long userId) {
        return cacheManager.getUserDetailVo(userId);
    }

    @Override
    public List<UserDetailVo> getUserDetailVoByIdList(List<Long> userIdList) {
        ArrayList<UserDetailVo> result = new ArrayList<>();
        for (Long userId : userIdList){
            result.add(getUserDetailVoById(userId));
        }
        return result;
    }

    @Override
    public void incrementCoin(BigDecimal price, Long userId) {
        userMapper.incrementCoin(price,userId);
        // 保证所有查数据库更新缓存过程互斥
        distributedLock.lock(userId);
        User user = userMapper.selectByPrimaryKey(userId);
        cacheManager.saveUserDetailVo(Transform.transformUserToUserDetailVo(user));
        distributedLock.unlock(userId);
    }
}
