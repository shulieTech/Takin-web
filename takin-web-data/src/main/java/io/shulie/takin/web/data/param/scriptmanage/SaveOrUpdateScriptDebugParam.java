package io.shulie.takin.web.data.param.scriptmanage;

import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/11 5:14 下午
 */
@Data
public class SaveOrUpdateScriptDebugParam {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 脚本发布id
     */
    private Long scriptDeployId;

    /**
     * 目标类型, 0 压测场景, 1 业务流程, 2 业务活动
     */
    private Integer targetType;

    /**
     * 目标id, 业务流程类型, 就是业务流程id, 等...
     */
    private Long targetId;

    /**
     * 序列号, 为了防止覆盖, 对应漏数结果表里的 report_id
     */
    private Long targetSn;

    /**
     * 调试记录状态, 0 未启动(默认), 1 启动中, 2 请求中, 3 请求结束, 4 调试成功, 5 调试失败
     */
    private Integer status;

    /**
     * 是否漏数, 1 是, 0 否
     */
    private Integer isLeaked;

    /**
     * 备注, 当调试失败时, 有失败信息
     */
    private String remark;

    /**
     * 对应的 cloud 场景id
     */
    private Long cloudSceneId;

    /**
     * 对应的 cloud 报告id
     */
    private Long cloudReportId;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 租户下的用户id
     */
    private Long userId;

}
