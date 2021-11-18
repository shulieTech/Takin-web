package io.shulie.takin.web.ext.entity.e2e;

import java.util.Date;

import lombok.Data;
import lombok.NonNull;

/**
 * @Author: fanxx
 * @Date: 2021/9/16 1:41 下午
 * @Description:
 */
@Data
public class E2eBaseStorageParam {
    /**
     * 业务活动ID
     */
    private Long activityId;
    /**
     * 服务唯一标识
     */
    private String edgeId;
    /**
     * 异常类型
     */
    private int type;
    /**
     * 异常等级
     */
    private int level;
    /**
     * 响应时间
     */
    private Double rt;
    /**
     * 成功率
     */
    private Double successRate;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 业务RPC类型
     */
    private String rpcType;

    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 环境编码
     */
    private String envCode;
    /**
     * 用户ID
     */
    private Long userId;

}
