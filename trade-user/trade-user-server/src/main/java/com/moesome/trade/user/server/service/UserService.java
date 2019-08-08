package com.moesome.trade.user.server.service;

import com.moesome.trade.common.response.Result;
import com.moesome.trade.user.common.request.LoginVo;
import com.moesome.trade.user.common.request.UserStoreVo;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    // 授权操作
    Result login(LoginVo loginVo, HttpServletResponse httpServletResponse);
    Result logout(String sessionId, HttpServletResponse httpServletResponse);
    Result check(String sessionId, HttpServletResponse httpServletResponse);

    // 增删改查
    Result store(UserStoreVo userStoreVo);
    Result delete(String sessionId, Long id);
    Result show(String sessionId,Long id);
    Result update(Long userId, UserStoreVo userStoreVo,String sessionId);
}
