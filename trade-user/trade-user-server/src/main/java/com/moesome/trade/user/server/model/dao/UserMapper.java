package com.moesome.trade.user.server.model.dao;

import com.moesome.trade.user.server.model.domain.User;

import java.math.BigDecimal;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUsername(String username);

    void incrementCoin(BigDecimal price, Long userId);
}