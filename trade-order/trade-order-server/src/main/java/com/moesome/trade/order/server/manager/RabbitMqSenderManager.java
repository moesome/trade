package com.moesome.trade.order.server.manager;

import com.moesome.trade.common.config.QueueConfig;
import com.moesome.trade.common.message.CommodityOrderMessage;
import com.moesome.trade.order.server.config.MQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMqSenderManager implements MqSenderManager,RabbitTemplate.ReturnCallback{
    @Autowired
    RabbitTemplate rabbitTemplate;

    public RabbitMqSenderManager(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.setReturnCallback(this);
    }

    public void sendToStockDecrementQueue(CommodityOrderMessage commodityOrderMessage){
        log.debug("发送到减库存队列: "+commodityOrderMessage);
        rabbitTemplate.invoke(operations -> {
            operations.convertAndSend(QueueConfig.ORDER_DIRECT_EXCHANGE, QueueConfig.STOCK_DECREMENT_QUEUE_KEY, commodityOrderMessage);
            if (!operations.waitForConfirms(3000)){
                throw new RuntimeException("队列发送信息失败");
            }
            log.debug("发送成功！");
            return null;
        });
    }

    @Override
    public void sendToStockRollbackQueue(CommodityOrderMessage commodityOrderMessage) {
        log.debug("发送到库存回滚队列: "+commodityOrderMessage);
        rabbitTemplate.invoke(operations -> {
            operations.convertAndSend(QueueConfig.ORDER_DIRECT_EXCHANGE, QueueConfig.STOCK_ROLLBACK_QUEUE_KEY, commodityOrderMessage);
            if (!operations.waitForConfirms(3000)){
                throw new RuntimeException("队列发送信息失败");
            }
            log.debug("发送成功！");
            return null;
        });
    }

    /**
     * 处理交换机到队列时发生的错误
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("由交换机转发到队列时出现错误，message:"+message);
    }
}
