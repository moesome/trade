package com.moesome.trade.common.exception;

public class DistributedLockException extends RuntimeException{
    public DistributedLockException() {
        super("获取分布式锁出现异常！");
    }
}
