package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.commodity.server.config.MQConfig;
import com.moesome.trade.commodity.server.service.MessageResolverService;
import com.moesome.trade.common.config.QueueConfig;
import com.moesome.trade.common.message.CommodityOrderMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class RabbitMqListenerManager implements MqListenerManager{
    @Autowired
    private MessageResolverService messageResolverService;

    @RabbitListener(queues = QueueConfig.STOCK_DECREMENT_QUEUE)
    @Override
    public void receiveStockDecrement(CommodityOrderMessage commodityOrderMessage, Channel channel, Message message){
        log.debug("减库存队列收到消息: "+commodityOrderMessage);
        try{
            messageResolverService.decrement(commodityOrderMessage);
        // 若捕获了未经处理的异常则拒绝消费这条消息
        } catch (Exception e){
            log.error(e.toString());
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            } catch (IOException ex) {
                log.error("发送 Nack 失败！"+e.toString());
            }
        }
        // 尝试确认该消息已被消费，确认未到达也不影响，业务中有防止重复消费的逻辑
        try {
            log.debug("发送 Ack 成功");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            log.error("发送 Ack 失败！"+e.toString());
        }
    }

    @RabbitListener(queues = QueueConfig.STOCK_ROLLBACK_QUEUE)
    @Override
    public void receiveStockRollback(CommodityOrderMessage commodityOrderMessage, Channel channel, Message message){
        log.debug("回滚库存队列收到消息: "+commodityOrderMessage);
        try{
            messageResolverService.increment(commodityOrderMessage);
            // 若捕获了未经处理的异常则拒绝消费这条消息
        } catch (Exception e){
            log.error(e.toString());
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            } catch (IOException ex) {
                log.error("发送 Nack 失败！"+e.toString());
            }
        }
        // 尝试确认该消息已被消费，确认未到达也不影响，业务中有防止重复消费的逻辑
        try {
            log.debug("发送 Ack 成功");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            log.error("发送 Ack 失败！"+e.toString());
        }
    }
}
