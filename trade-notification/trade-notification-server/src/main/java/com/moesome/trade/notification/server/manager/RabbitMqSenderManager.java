package com.moesome.trade.notification.server.manager;

import com.moesome.trade.common.config.QueueConfig;
import com.moesome.trade.notification.common.request.NotificationVo;
import com.moesome.trade.notification.server.config.MQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMqSenderManager implements MqSenderManager {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void sendToMailQueue(NotificationVo notificationVo) {
        log.debug("向邮件队列发送消息: "+notificationVo);
        amqpTemplate.convertAndSend(QueueConfig.ORDER_DIRECT_EXCHANGE, QueueConfig.EMAIL_QUEUE_KEY, notificationVo);
    }
}
