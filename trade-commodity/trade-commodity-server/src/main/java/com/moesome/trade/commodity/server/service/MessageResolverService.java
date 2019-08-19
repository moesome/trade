package com.moesome.trade.commodity.server.service;

import com.moesome.trade.common.message.CommodityOrderMessage;

public interface MessageResolverService {
    void decrement(CommodityOrderMessage commodityOrderMessage);

    void increment(CommodityOrderMessage commodityOrderMessage);
}
