package com.moesome.trade.commodity.server.model.dao;

import com.moesome.trade.commodity.server.model.domain.MessageCommodity;

public interface MessageCommodityMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(MessageCommodity record);

    int insertSelective(MessageCommodity record);

    MessageCommodity selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(MessageCommodity record);

    int updateByPrimaryKey(MessageCommodity record);
}