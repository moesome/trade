package com.moesome.trade.notification.server.manager;

import com.moesome.trade.notification.common.request.NotificationVo;

public interface MqListenerManager {
    void receiveMail(NotificationVo notificationVo);
}
