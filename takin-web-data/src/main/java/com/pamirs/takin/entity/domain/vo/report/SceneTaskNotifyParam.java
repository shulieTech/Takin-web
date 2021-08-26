package com.pamirs.takin.entity.domain.vo.report;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-23
 */
@Data
public class SceneTaskNotifyParam {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 状态
     */
    private String status;

    /**
     * 消息
     */
    private String msg;
}
