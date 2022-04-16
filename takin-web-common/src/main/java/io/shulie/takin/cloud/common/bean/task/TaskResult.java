package io.shulie.takin.cloud.common.bean.task;

import java.util.Map;

import io.shulie.takin.cloud.common.enums.scenemanage.TaskStatusEnum;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/4/20 下午2:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskResult extends ContextExt {
    /**
     * 场景ID
     */
    private Long sceneId;
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 压力节点 调度 任务状态
     */
    private TaskStatusEnum status;
    /**
     * 状态描述
     */
    private String msg;
    /**
     * 是否主动停止
     */
    private Boolean forceStop;
    /**
     * 是否主动启动
     */
    private Boolean forceStart;
    /**
     * 拓展配置
     */
    private Map<String, Object> extendMap;
    private String resourceId;

    public TaskResult(Long sceneId, Long taskId, Long tenantId) {
        this.sceneId = sceneId;
        this.taskId = taskId;
        this.setTenantId(tenantId);
    }

    public TaskResult() {

    }
}
