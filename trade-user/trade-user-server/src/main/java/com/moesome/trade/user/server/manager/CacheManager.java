package com.moesome.trade.user.server.manager;

import com.moesome.trade.user.common.response.vo.UserDetailVo;

import java.util.HashMap;

public interface CacheManager {
    /**
     * 将用户部分信息存入 redis
     * @param userDetailVo
     * @return
     */
    void saveUserDetailVo(UserDetailVo userDetailVo);

    void saveUserDetailVo(UserDetailVo userDetailVo,Long second);

    /**
     * 根据 userId 读出 UserDetailVo
     * @param userId
     * @return 返回未经反序列化的 UserDetailVo
     */
    UserDetailVo getUserDetailVo(Long userId);

}
