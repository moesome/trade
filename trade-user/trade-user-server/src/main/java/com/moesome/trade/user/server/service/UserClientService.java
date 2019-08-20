package com.moesome.trade.user.server.service;

import com.moesome.trade.user.common.response.vo.UserDetailVo;

import java.math.BigDecimal;
import java.util.List;

public interface UserClientService {
    UserDetailVo getUserDetailVoById(Long userId);

    List<UserDetailVo> getUserDetailVoByIdList(List<Long> userIdList);

    void incrementCoin(BigDecimal price, Long userId);
}
