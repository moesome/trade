package com.moesome.trade.notification.client;

import com.moesome.trade.notification.common.request.NotificationVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient("trade-notification-server")
@RequestMapping("client")
public interface NotificationClient {
    @PostMapping("notification")
    void notification(@RequestBody NotificationVo notificationVo);
}
