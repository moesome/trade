package com.moesome.trade.user.client;

import com.moesome.trade.user.common.response.vo.UserDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("trade-user-server")
@RequestMapping("client")
public interface UserClient {
    @GetMapping("getUserDetailVoById")
    UserDetailVo getUserDetailVoById(@RequestParam Long userId);
}
