package com.moesome.trade.zuul.manager;

public interface SessionManager {

    /**
     * 根据 messageKey 取出 sessionId 关联 session 中的信息
     * @param sessionId
     * @param messageKey
     * @param <T> 返回值类型
     * @return
     */
    <T> T getSessionMessage(String sessionId, String messageKey);
}
