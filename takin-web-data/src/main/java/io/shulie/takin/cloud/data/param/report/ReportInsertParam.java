package io.shulie.takin.cloud.data.param.report;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class ReportInsertParam {

    private Long id;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 流量消耗
     */
    private BigDecimal amount;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 报表生成状态:0/就绪状态，1/生成中,2/完成生成
     */
    private Integer status;

    /**
     * 报告类型:0:普通场景，1:流量调试
     */
    private Integer type;

    /**
     * 请求总数
     */
    private Long totalRequest;

    /**
     * 施压类型,0:并发,1:tps,2:自定义;不填默认为0
     */
    private Integer pressureType;

    /**
     * 平均并发数
     */
    private BigDecimal avgConcurrent;

    /**
     * 目标TPS
     */
    private Integer tps;

    /**
     * 平均tps
     */
    private BigDecimal avgTps;

    /**
     * 平均响应时间
     */
    private BigDecimal avgRt;

    /**
     * 最大并发
     */
    private Integer concurrent;

    /**
     * 成功率
     */
    private BigDecimal successRate;

    /**
     * sa
     */
    private BigDecimal sa;

    /**
     * 操作用户ID
     */
    private Long operateId;

    /**
     * 扩展字段，JSON数据格式
     */
    private String features;

    /**
     * 是否删除:0/正常，1、已删除
     */
    private Integer isDeleted;

    private Long userId;

    private Long scriptId;

    /**
     * 锁报告
     */
    private Integer lock;

    /**
     * 脚本节点树
     */
    private String scriptNodeTree;

}
