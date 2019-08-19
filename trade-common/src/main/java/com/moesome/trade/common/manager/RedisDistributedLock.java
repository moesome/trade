package com.moesome.trade.common.manager;

import com.moesome.trade.common.exception.DistributedLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisDistributedLock implements DistributedLock {

    @Autowired
    private RedisTemplate<String,Object> redisTemplateForDistributedLock;

    private String type;

    // 标识机器
    private final String ID = UUID.randomUUID().toString();

    public static final DistributedLockException DISTRIBUTED_LOCK_EXCEPTION = new DistributedLockException();

    public RedisDistributedLock(String type) {
        this.type = type;
    }

    @Override
    public void lock(Long id) {
        Boolean locked = tryLock(id);
        while (locked == null || !locked){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw DISTRIBUTED_LOCK_EXCEPTION;
            }
            locked = tryLock(id);
        }
        log.debug(Thread.currentThread().getId()+"拿到锁："+type+":"+id);
    }

    public Boolean tryLock(Long id){
        return redisTemplateForDistributedLock.opsForValue().setIfAbsent(getKey(id), getValue(),10, TimeUnit.SECONDS);
    }

    /**
     * 生成键，如 commodity:1 即对一号商品上锁
     * @param id
     * @return
     */
    private String getKey(Long id){
        return "lock-"+type+":"+id;
    }

    /**
     * 生成值，如 fbc122f3-681b-4859-8678-2ba9ff28253c:30
     * 即 UUID 对应机器的 30 号线程对其上锁
     * @return
     */
    private String getValue(){
        return ID+":"+Thread.currentThread().getId();
    }

    @Override
    public Boolean unlock(Long id){
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
        defaultRedisScript.setResultType(Long.class);
        Long result = redisTemplateForDistributedLock.execute(defaultRedisScript, Collections.singletonList(getKey(id)), getValue());
        if (result != null && result == 1){
            log.debug(Thread.currentThread().getId()+"解锁成功："+type+":"+id+" result="+result);
            return true;
        }else{
            log.debug(Thread.currentThread().getId()+"锁已经失效，放弃解锁："+type+":"+id+" result="+result);
            return false;
        }
    }

}
