package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.common.message.CommodityOrderMessage;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

public interface MqListenerManager {
    /**
     * 监听队列消息
     * @param commodityOrderMessage
     */
    void receiveStockDecrement(CommodityOrderMessage commodityOrderMessage, Channel channel, Message message);
    void receiveStockRollback(CommodityOrderMessage commodityOrderMessage, Channel channel, Message message);
}
