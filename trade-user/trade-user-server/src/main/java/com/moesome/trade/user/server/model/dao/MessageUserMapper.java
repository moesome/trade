package com.moesome.trade.user.server.model.dao;

import com.moesome.trade.user.server.model.domain.MessageUser;

public interface MessageUserMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(MessageUser record);

    int insertSelective(MessageUser record);

    MessageUser selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(MessageUser record);

    int updateByPrimaryKey(MessageUser record);
}