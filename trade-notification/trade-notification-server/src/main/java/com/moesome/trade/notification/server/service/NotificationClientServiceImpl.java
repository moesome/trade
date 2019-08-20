package com.moesome.trade.notification.server.service;

import com.moesome.trade.notification.common.request.NotificationType;
import com.moesome.trade.notification.common.request.NotificationVo;
import com.moesome.trade.notification.server.manager.MqSenderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class NotificationClientServiceImpl implements NotificationClientService {

    @Autowired
    private MqSenderManager mqSenderManager;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendNotify(NotificationVo notificationVo) {
        if (notificationVo.getNotificationType() == NotificationType.EMAIL){
            mqSenderManager.sendToMailQueue(notificationVo);
        }else{
            log.error("电话通道暂未开放！");
        }
    }

    @Override
    @Async
    public void doSendEmail(NotificationVo notificationVo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contact@mail.moesome.com");
        message.setTo(notificationVo.getTo());
        message.setSubject(notificationVo.getTitle());
        message.setText(notificationVo.getMsg());
        javaMailSender.send(message);
        log.debug("发送邮件成功，信息为："+message);
    }

}
