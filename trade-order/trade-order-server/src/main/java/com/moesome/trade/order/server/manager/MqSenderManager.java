package com.moesome.trade.order.server.manager;

import com.moesome.trade.common.message.CommodityOrderMessage;

public interface MqSenderManager {
    /**
     * 发送消息到减库存队列，队列未接收到则抛出异常
     * @param commodityOrderMessage
     */
    void sendToStockDecrementQueue(CommodityOrderMessage commodityOrderMessage);

    void sendToStockRollbackQueue(CommodityOrderMessage commodityOrderMessage);
}
