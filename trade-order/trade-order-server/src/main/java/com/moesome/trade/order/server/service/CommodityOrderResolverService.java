package com.moesome.trade.order.server.service;

import com.moesome.trade.common.response.Result;

public interface CommodityOrderResolverService {

    Result remindToSendProduction(Long userId, Long commodityOrderId);

    Result receivedProduction(Long userId, Long commodityOrderId);

    Result sendProduction(Long userId, Long commodityOrderId);

    Result index(int page, String order, Long userId);
}
