package com.moesome.trade.notification.server.service;

import com.moesome.trade.notification.common.request.NotificationVo;

public interface NotificationClientService {
    void sendNotify(NotificationVo notificationVo);

    void doSendEmail(NotificationVo notificationVo);
}
