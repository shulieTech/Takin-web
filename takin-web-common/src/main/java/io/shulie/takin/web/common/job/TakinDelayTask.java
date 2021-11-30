package io.shulie.takin.web.common.job;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.job
 * @ClassName: TakinDelayJob
 * @Description: 任务引用对象
 * @Date: 2021/11/29 22:35
 */
@Data
@AllArgsConstructor
public class TakinDelayTask implements Serializable {
    /**
     * 延迟任务的唯一标识
     */
    private long jodId;

    /**
     * 任务的执行时间
     */
    private long delayDate;

    /**
     * 任务类型（具体业务类型）
     */
    private String topic;

    public TakinDelayTask(TakinTask job) {
        this.jodId = job.getId();
        this.delayDate = System.currentTimeMillis() + job.getDelayTime();
        this.topic = job.getTopic();
    }

    public TakinDelayTask(Object value, Double score) {
        this.jodId = Long.parseLong(String.valueOf(value));
        this.delayDate = System.currentTimeMillis() + score.longValue();
    }
}
