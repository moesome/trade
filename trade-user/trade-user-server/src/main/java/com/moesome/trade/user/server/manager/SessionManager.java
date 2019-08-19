package com.moesome.trade.user.server.manager;

import java.util.HashMap;
import java.util.Map;

public interface SessionManager {
    String generatorSessionId();

    /**
     * 将信息存入 session
     * @param sessionId
     */
    <T> void saveMessageToSession(String sessionId, String messageKey, T messageValue);

    /**
     * 根据 messageKey 取出 sessionId 关联 session 中的信息
     * @param sessionId
     * @param messageKey
     * @param <T> 返回值类型
     * @return
     */
    <T> T getSessionMessage(String sessionId, String messageKey);

    /**
     * 刷新 session 的缓存存在天数
     * @param sessionId
     * @param day
     */
    void refreshSession(String sessionId, int day);
}
