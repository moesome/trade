package com.moesome.trade.notification.server.test;

import com.moesome.trade.notification.server.TradeNotificationServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeNotificationServerApplication.class)
public class MyTest {
    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    public void test(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contact@mail.moesome.com");
        message.setTo("1053770594@qq.com");
        message.setSubject("title");
        message.setText("text");
        javaMailSender.send(message);
    }
}
