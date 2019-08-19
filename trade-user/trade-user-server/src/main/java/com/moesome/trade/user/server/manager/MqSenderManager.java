package com.moesome.trade.user.server.manager;

import com.moesome.trade.common.message.CommodityOrderMessage;

public interface MqSenderManager {
    void sendToOrderSucceedQueue(CommodityOrderMessage commodityOrderMessage);
    void sendToOrderFailedQueue(CommodityOrderMessage commodityOrderMessage);
}
