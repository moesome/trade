package com.moesome.trade.common.manager;

public interface DistributedLock {
	/**
	 * 上锁，发生异常则抛出 DistributedLockException
	 * @param id
	 */
	void lock(Long id);


	/**
	 * @param id
	 * @return true：解锁成功 false：锁已经失效
	 */
	Boolean unlock(Long id);
}
