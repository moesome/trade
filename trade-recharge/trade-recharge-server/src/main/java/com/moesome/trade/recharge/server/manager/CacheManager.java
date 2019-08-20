package com.moesome.trade.recharge.server.manager;

public interface CacheManager {
    boolean saveRechargeId(Long id);
    void removeRechargeId(Long id);
}
