package com.moesome.trade.user.server.controller;

import com.moesome.trade.user.common.request.LoginVo;
import com.moesome.trade.user.common.request.UserStoreVo;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.user.common.response.result.UserResult;
import com.moesome.trade.user.server.service.UserService;
import com.moesome.trade.user.server.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    // 用户授权相关
    @PostMapping("login")
    public Result login(@Validated @RequestBody LoginVo loginVo, HttpServletResponse httpServletResponse){
        return userService.login(loginVo,httpServletResponse);
    }

    @PostMapping("logout")
    public Result logout(@CookieValue String sessionId, HttpServletResponse httpServletResponse){
        return userService.logout(sessionId,httpServletResponse);
    }

    @GetMapping("check")
    public Result check(@CookieValue String sessionId, HttpServletResponse httpServletResponse){
        return userService.check(sessionId,httpServletResponse);
    }

    // 用户操作相关
    @PostMapping
    public Result store(@RequestBody @Validated UserStoreVo userStoreVo){
        return userService.store(userStoreVo);
    }

    @GetMapping("{id}")
    public Result show(@CookieValue String sessionId, @PathVariable Long id){
        return userService.show(sessionId,id);
    }

    @PatchMapping("{id}")
    public Result update(@RequestHeader("user-id") Long userId,@CookieValue String sessionId,@RequestBody UserStoreVo userStoreVo, @PathVariable Long id){
        if (!userId.equals(id)){
            return UserResult.AUTHORIZED_ERR;
        }
        return userService.update(userId,userStoreVo,sessionId);
    }

    // 暂不开启入口
    public Result delete(String sessionId,Long id){
        return userService.delete(sessionId,id);
    }

}
