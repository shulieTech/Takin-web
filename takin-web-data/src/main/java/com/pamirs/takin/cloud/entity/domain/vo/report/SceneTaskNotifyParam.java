package com.pamirs.takin.cloud.entity.domain.vo.report;

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
     * 客户Id 新版
     */
    private Long tenantId;
    /**
     * 客户Id 旧版
     */
    private Long customerId;

    /**
     * 状态
     */
    private String status;

    /**
     * 消息
     */
    private String msg;

}
