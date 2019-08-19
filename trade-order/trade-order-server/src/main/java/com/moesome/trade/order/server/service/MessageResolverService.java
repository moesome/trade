package com.moesome.trade.order.server.service;

import com.moesome.trade.common.message.CommodityOrderMessage;

public interface MessageResolverService {
    void resolveFailed(CommodityOrderMessage commodityOrderMessage);

    void resolveSucceed(CommodityOrderMessage commodityOrderMessage);
}
