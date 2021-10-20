package io.shulie.takin.web.ext.entity.e2e;

import java.util.Date;

import lombok.Data;

/**
 * 巡检异常-数据落库
 *
 * @author 张天赐
 */
@Data
public class E2eStorageRequest extends E2eBaseStorageRequest {
    /**
     * 链路ID
     */
    private Long chainId;
    /**
     * 巡检类型 0.业务活动巡检 1.业务流程巡检
     */
    private int patrolType;

    /**
     * 业务主键
     */
    private Long activityId;

    /**
     * 业务名称
     */
    private String activityName;
    /**
     * 业务RPC类型
     */
    private String rpcType;

    /**
     * 场景
     */
    private long sceneId;
    /**
     * 场景名称
     */
    private String sceneName;
    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * amdb查询开始时间
     */
    private Long queryStartTime;
    /**
     * amdb查询结束时间
     */
    private Long queryEndTime;
    /**
     * 边ID
     */
    private String edgeId;
}
