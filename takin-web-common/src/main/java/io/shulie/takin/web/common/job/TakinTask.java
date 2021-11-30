package io.shulie.takin.web.common.job;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.shulie.takin.web.common.enums.job.JobStatus;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.biz.task
 * @ClassName: TakinJob
 * @Description: 任务对象
 * @Date: 2021/11/29 22:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TakinTask extends TenantCommonExt implements Serializable {
    /**
     * 延迟任务的唯一标识，用于检索任务
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 任务类型（具体业务类型）
     */
    private String topic;
    // /**
    //  * 任务的延迟时间
    //  */
    // private long delayTime;
    // /**
    //  * 任务的执行超时时间
    //  */
    // private long ttrTime;
    /**
     * 任务具体的消息内容，用于处理具体业务逻辑用
     */
    private String message;
    /**
     * 任务状态
     */
    private JobStatus status;
}
