package io.shulie.takin.cloud.ext.content.enginecall;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-08-10
 */
@Data
public class ScheduleInitParamExt {

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 客户id 新增
     */
    private Long tenantId;

    /**
     * POD总数
     */
    private int total;
}
