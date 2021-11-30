package io.shulie.takin.web.common.enums.job;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.enums.job
 * @ClassName: JobStatus
 * @Description: TODO
 * @Date: 2021/11/29 22:31
 */
@Getter
@AllArgsConstructor
public enum JobStatus {
    READY("ready","可执行状态"),
    DELAY("delay","不可执行状态，等待时钟周期。"),
    RESERVED("reserved","已被消费者读取，但没有完成消费。"),
    DELETED("deleted","已被消费完成或者已被删除")
    ;
    private String status;
    private String desc;

}
