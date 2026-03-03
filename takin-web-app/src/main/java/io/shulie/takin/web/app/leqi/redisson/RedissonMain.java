package io.shulie.takin.web.app.leqi.redisson;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import java.util.concurrent.TimeUnit;

public class RedissonMain {

    public static void main(String[] args) {
        // 1. 配置并创建 RedissonClient
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        // 可选：设置看门狗超时时间（默认30000毫秒）
        // config.setLockWatchdogTimeout(30000L);
        RedissonClient redisson = Redisson.create(config);

        // 2. 获取分布式锁对象
        RLock lock = redisson.getLock("gwsk:20250917:0100");

        boolean isLocked = false;
        try {
            // 3. 尝试获取锁：最多等待2秒，获取成功后锁持有10秒后自动释放
            isLocked = lock.tryLock(2, 60, TimeUnit.SECONDS);
            if (isLocked) {
                System.out.println(Thread.currentThread().getName() + " 成功获取到锁！");
                // 4. 模拟业务处理
                Thread.sleep(45000); // 假设业务执行了5秒
                System.out.println(Thread.currentThread().getName() + " 业务执行完毕。");
            } else {
                System.out.println(Thread.currentThread().getName() + " 在指定时间内未获取到锁，可能其他线程正持有锁。");
                // 获取锁失败后的处理逻辑，如快速失败、重试或降级
            }
        } catch (InterruptedException e) {
            System.out.println("线程在等待锁时被中断。");
            Thread.currentThread().interrupt(); // 恢复中断状态
        } finally {
            // 5. 释放锁 - 只有在成功获取锁且当前线程仍持有锁时才释放
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + " 已释放锁。");
            }
            // 6. 关闭 Redisson 客户端（通常在应用关闭时进行）
            redisson.shutdown();
        }
    }
}
