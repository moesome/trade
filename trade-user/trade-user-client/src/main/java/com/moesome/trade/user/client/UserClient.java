package com.moesome.trade.user.client;

import com.moesome.trade.user.common.response.vo.UserDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("trade-user-server")
@RequestMapping("client")
public interface UserClient {
    @GetMapping("getUserDetailVoById")
    UserDetailVo getUserDetailVoById(@RequestParam Long userId);

    @PostMapping("getUserDetailVoByIdList")
    List<UserDetailVo> getUserDetailVoByIdList(@RequestBody List<Long> userIdList);

    @PostMapping("incrementCoin")
    void incrementCoin(@RequestParam BigDecimal price, @RequestParam Long userId);
}
