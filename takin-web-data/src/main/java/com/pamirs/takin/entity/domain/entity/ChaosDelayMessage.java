package com.pamirs.takin.entity.domain.entity;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shulie
 * @package: com.pamirs.takin.entity.domain.entity
 * @date 2019-05-27 15:03
 */
@Getter
@Setter
public class ChaosDelayMessage implements Delayed {

    private String id;
    private String body;
    private long excuteTime;
    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
    public ChaosDelayMessage(String id, String body, long excuteTime) {
        this.id = id;
        this.body = body;
        this.excuteTime = excuteTime + System.currentTimeMillis();
    }

    /**
     * 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期
     *
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.excuteTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        return (int)(this.getDelay(TimeUnit.MILLISECONDS) - delayed.getDelay(TimeUnit.MILLISECONDS));
    }
}
