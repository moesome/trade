package com.moesome.trade.commodity.server.manager;

import org.springframework.stereotype.Service;

@Service
public class DistributedLockManager extends RedisDistributedLock {
    public DistributedLockManager() {
        super("commodity");
    }
}
