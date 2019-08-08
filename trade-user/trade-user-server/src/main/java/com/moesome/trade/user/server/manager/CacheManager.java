package com.moesome.trade.user.server.manager;

import com.moesome.trade.user.common.response.vo.UserDetailVo;

public interface CacheManager {
    /**
     * 将用户部分信息存入 redis 并生成 sessionId
     * @param userDetailVo
     * @return
     */
    String saveUserDetailVoAndGenerateSessionId(UserDetailVo userDetailVo);

    /**
     * 将用户使用指定 sessionId存入 redis
     * @param userDetailVo
     * @return
     */
    String saveUserDetailVo(UserDetailVo userDetailVo,String sessionId);

    /**
     * 根据 sessionId 读出 UserDetailVo
     * @param sessionId
     * @return 返回未经反序列化的 UserDetailVo
     */
    UserDetailVo getUserDetailVoBySessionId(String sessionId);

    /**
     * 刷新 redis session 的缓存存在时间
     * @param sessionId
     */
    void refreshUserDetailVo(String sessionId, int time);

}
