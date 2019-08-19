package com.moesome.trade.user.server.controller;

import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.service.UserClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("client")
public class UserClientController {
    @Autowired
    private UserClientService userClientService;

    @GetMapping("getUserDetailVoById")
    public UserDetailVo getUserDetailVoById(Long userId){
        return userClientService.getUserDetailVoById(userId);
    }
}
