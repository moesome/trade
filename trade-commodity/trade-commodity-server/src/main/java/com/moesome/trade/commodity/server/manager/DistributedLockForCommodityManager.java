package com.moesome.trade.commodity.server.manager;

import com.moesome.trade.common.manager.RedisDistributedLock;
import org.springframework.stereotype.Service;

@Service
public class DistributedLockForCommodityManager extends RedisDistributedLock {
    public DistributedLockForCommodityManager() {
        super("commodity");
    }
}
