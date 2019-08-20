package com.moesome.trade.notification.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    EMAIL("email"),
    PHONE("phone");

    String type;
}
