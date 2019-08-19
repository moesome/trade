package com.moesome.trade.user.server.manager;

import com.moesome.trade.common.manager.RedisDistributedLock;
import org.springframework.stereotype.Service;

@Service
public class DistributedLockForUserManager extends RedisDistributedLock {
    public DistributedLockForUserManager() {
        super("user");
    }
}
