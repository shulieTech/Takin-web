package io.shulie.takin.web.biz.service.impl;

import java.util.concurrent.TimeUnit;

import io.shulie.takin.web.biz.service.DistributedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author liuchuan
 * @date 2021/6/8 2:44 下午
 */
@Service
public class RedissonDistributedLock implements DistributedLock {

    @Qualifier("redisson")
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean tryLockZeroWait(String lockKey) {
        return this.tryLockSecondsTimeUnit(lockKey, 0L, -1L);
    }

    @Override
    public boolean tryLockSecondsTimeUnit(String lockKey, Long waitTime, Long leaseTime) {
        return this.tryLock(lockKey, waitTime, leaseTime, TimeUnit.SECONDS);
    }

    @Override
    public boolean tryLock(String lockKey, Long waitTime, Long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException("尝试获得锁失败!");
        }
    }

    @Override
    public void unLockSafely(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Override
    public void unLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isLocked()) {
            lock.unlock();
        }
    }

    @Override
    public Boolean checkLock(String lockKey) {
       return redissonClient.getLock(lockKey).isLocked();
    }

}
