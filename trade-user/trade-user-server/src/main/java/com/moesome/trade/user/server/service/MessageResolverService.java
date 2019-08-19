package com.moesome.trade.user.server.service;

import com.moesome.trade.common.message.CommodityOrderMessage;

public interface MessageResolverService {
    void decrement(CommodityOrderMessage commodityOrderMessage);
}
