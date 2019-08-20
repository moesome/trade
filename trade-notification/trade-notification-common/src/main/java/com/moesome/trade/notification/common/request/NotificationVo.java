package com.moesome.trade.notification.common.request;

import lombok.Data;

@Data
public class NotificationVo {
    /**
     * 通知标题
     */
    private String title;
    /**
     * 通知内容
     */
    private String msg;
    /**
     * 通知类型
     */
    private NotificationType notificationType;
    /**
     * 通知目标地址
     */
    private String to;
}
