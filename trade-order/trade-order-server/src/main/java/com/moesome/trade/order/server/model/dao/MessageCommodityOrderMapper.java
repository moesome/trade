package com.moesome.trade.order.server.model.dao;

import com.moesome.trade.order.server.model.domain.MessageCommodityOrder;

public interface MessageCommodityOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(MessageCommodityOrder record);

    int insertSelective(MessageCommodityOrder record);

    MessageCommodityOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(MessageCommodityOrder record);

    int updateByPrimaryKey(MessageCommodityOrder record);
}