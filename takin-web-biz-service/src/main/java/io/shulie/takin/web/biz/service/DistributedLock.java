package io.shulie.takin.web.biz.service;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 *
 * @author liuchuan
 * @date 2021/6/8 2:42 下午
 */
public interface DistributedLock {

    /**
     * 尝试获得锁, 等待0秒, 开启 watchdog, 释放
     * 单位时间是 秒
     *
     * @param lockKey 锁住的 key
     * @return 是否获得到了锁, true 是, false 否
     */
    boolean tryLockZeroWait(String lockKey);

    /**
     * 尝试获得锁
     * 单位时间是 秒
     *
     * @param lockKey 锁住的 key
     * @param waitTime 等待锁释放的时间
     * @param leaseTime 释放锁的时间
     * @return 是否获得到了锁, true 是, false 否
     */
    boolean tryLockSecondsTimeUnit(String lockKey, Long waitTime, Long leaseTime);

    /**
     * 尝试获得锁
     *
     * @param lockKey 锁住的 key
     * @param waitTime 等待锁释放的时间
     * @param leaseTime 释放锁的时间
     * @param timeUnit 时间单位
     * @return 是否获得到了锁, true 是, false 否
     */
    boolean tryLock(String lockKey, Long waitTime, Long leaseTime, TimeUnit timeUnit);

    /**
     * 安全地释放锁
     *
     * 如果是当前线程持有该锁, 就释放, 否则不做
     *
     * @param lockKey 锁key
     */
    void unLockSafely(String lockKey);

    void unLock(String lockKey);

    /**
     * 是否有锁
     * @param lockKey
     * @return
     */
    Boolean checkLock(String lockKey);
}
