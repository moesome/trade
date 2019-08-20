package com.moesome.trade.user.server.controller;

import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.service.UserClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("client")
public class UserClientController {
    @Autowired
    private UserClientService userClientService;

    @GetMapping("getUserDetailVoById")
    public UserDetailVo getUserDetailVoById(Long userId){
        return userClientService.getUserDetailVoById(userId);
    }

    @PostMapping("getUserDetailVoByIdList")
    public List<UserDetailVo> getUserDetailVoByIdList(@RequestBody List<Long> userIdList){
        return userClientService.getUserDetailVoByIdList(userIdList);
    }

    @PostMapping("incrementCoin")
    public void incrementCoin(BigDecimal price,  Long userId){
        userClientService.incrementCoin(price,userId);
    }
}
