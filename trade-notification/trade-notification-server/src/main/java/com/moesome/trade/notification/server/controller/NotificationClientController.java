package com.moesome.trade.notification.server.controller;

import com.moesome.trade.notification.common.request.NotificationVo;
import com.moesome.trade.notification.server.service.NotificationClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("client")
public class NotificationClientController {
    @Autowired
    private NotificationClientService notificationClientService;

    @PostMapping("notification")
    public void notification(@RequestBody NotificationVo notificationVo){
        notificationClientService.sendNotify(notificationVo);
    }
}
