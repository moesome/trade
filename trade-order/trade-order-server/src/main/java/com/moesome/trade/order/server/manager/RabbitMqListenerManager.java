package com.moesome.trade.order.server.manager;

import com.moesome.trade.common.config.QueueConfig;
import com.moesome.trade.common.message.CommodityOrderMessage;
import com.moesome.trade.order.server.config.MQConfig;
import com.moesome.trade.order.server.service.MessageResolverService;
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

    @RabbitListener(queues = QueueConfig.ORDER_FAILED_QUEUE)
    public void receiveOrderFailed(CommodityOrderMessage commodityOrderMessage, Channel channel, Message message){
        log.debug("错误消息队列收到消息: "+commodityOrderMessage);
        try{
            messageResolverService.resolveFailed(commodityOrderMessage);
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
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.debug("发送 Ack 成功");
        } catch (IOException e) {
            log.error("发送 Ack 失败！"+e.toString());
        }
    }

    @RabbitListener(queues = QueueConfig.ORDER_SUCCEED_QUEUE)
    public void receiveOrderSuccess(CommodityOrderMessage commodityOrderMessage, Channel channel, Message message){
        log.debug("成功消息队列收到消息: "+commodityOrderMessage);
        try{
            messageResolverService.resolveSucceed(commodityOrderMessage);
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
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.debug("发送 Ack 成功");
        } catch (IOException e) {
            log.error("发送 Ack 失败！"+e.toString());
        }
    }
}
