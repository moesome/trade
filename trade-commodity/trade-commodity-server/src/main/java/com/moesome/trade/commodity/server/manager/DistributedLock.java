package com.moesome.trade.commodity.server.manager;

public interface DistributedLock {
	/**
	 * 轮询，直到拿到锁位置
	 * @param id
	 * @return true：拿到锁，false：出现异常
	 */
	Boolean lock(Long id);


	/**
	 * @param id
	 * @return true：解锁成功 false：锁已经失效
	 */
	Boolean unlock(Long id);
}
