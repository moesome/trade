package com.moesome.trade.notification.server.manager;

import com.moesome.trade.common.config.QueueConfig;
import com.moesome.trade.notification.common.request.NotificationVo;
import com.moesome.trade.notification.server.service.NotificationClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMqListenerManager implements MqListenerManager{
    @Autowired
    private NotificationClientService notificationClientService;

    @RabbitListener(queues = QueueConfig.EMAIL_QUEUE)
    @Override
    public void receiveMail(NotificationVo notificationVo){
        log.debug("邮件队列接收消息: "+notificationVo);
        notificationClientService.doSendEmail(notificationVo);
    }
}
