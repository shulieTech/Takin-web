package com.pamirs.takin.cloud.entity.domain.vo.engine;

import lombok.Data;

/**
 * @author hezhongqi
 */
@Data
public class EngineNotifyParam {

    /**
     * 任务ID
     */
    private Long resultId;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 客户Id 新增
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

    /**
     * 开启压测时间，结束压测时间
     */
    private Long time;

    /**
     * pod序号
     */
    private String podNum;
}
