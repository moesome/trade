package com.moesome.trade.user.server.service;

import com.moesome.trade.user.common.response.vo.UserDetailVo;

public interface UserClientService {
    UserDetailVo getUserDetailVoById(Long userId);
}
