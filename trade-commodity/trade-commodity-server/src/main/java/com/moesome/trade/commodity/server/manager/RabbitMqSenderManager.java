package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.commodity.server.config.MQConfig;
import com.moesome.trade.common.config.QueueConfig;
import com.moesome.trade.common.message.CommodityOrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMqSenderManager implements MqSenderManager, RabbitTemplate.ReturnCallback{

    @Autowired
    RabbitTemplate rabbitTemplate;

    public RabbitMqSenderManager(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void sendToCoinDecrementQueue(CommodityOrderMessage commodityOrderMessage) {
        log.debug("sendToCoinDecrementQueue 发送 commodityOrderMessage: "+commodityOrderMessage);
        rabbitTemplate.invoke(operations -> {
            operations.convertAndSend(QueueConfig.ORDER_DIRECT_EXCHANGE, QueueConfig.COIN_DECREMENT_QUEUE_KEY, commodityOrderMessage);
            if (!operations.waitForConfirms(3000)){
                throw new RuntimeException("队列发送信息失败");
            }
            log.debug("发送成功！");
            return null;
        });
    }

    @Override
    public void sendToOrderFailedQueue(CommodityOrderMessage commodityOrderMessage) {
        log.debug("sendToOrderFailedQueue 发送 commodityOrderMessage: "+commodityOrderMessage);
        rabbitTemplate.invoke(operations -> {
            operations.convertAndSend(QueueConfig.ORDER_DIRECT_EXCHANGE, QueueConfig.ORDER_FAILED_QUEUE_KEY, commodityOrderMessage);
            if (!operations.waitForConfirms(3000)){
                throw new RuntimeException("队列发送信息失败");
            }
            log.debug("发送成功！");
            return null;
        });
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("由交换机转发到队列时出现错误，message:"+message);
    }

}
