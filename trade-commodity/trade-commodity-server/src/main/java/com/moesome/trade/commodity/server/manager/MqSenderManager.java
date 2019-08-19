package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.common.message.CommodityOrderMessage;

public interface MqSenderManager {
    /**
     * 发送消息到减金币队列
     * @param commodityOrderMessage
     */
    void sendToCoinDecrementQueue(CommodityOrderMessage commodityOrderMessage);
    void sendToOrderFailedQueue(CommodityOrderMessage commodityOrderMessage);
}
