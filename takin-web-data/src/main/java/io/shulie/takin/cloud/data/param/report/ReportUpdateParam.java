package io.shulie.takin.cloud.data.param.report;

import java.math.BigDecimal;
import java.util.Date;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2021/2/3 10:49 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportUpdateParam extends ContextExt {
    private Long id;

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
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 报表生成状态:0/就绪状态，1/生成中,2/完成生成
     */
    private Integer status;

    /**
     * 压测结论: 0/不通过，1/通过
     */
    private Integer conclusion;

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
     * 扩展字段，JSON数据格式
     */
    private String features;

    /**
     * 是否删除:0/正常，1、已删除
     */
    private Integer isDeleted;

    private Date gmtUpdate;

    private String resourceId;

    private Long jobId;

    private Integer calibrationStatus;
    private String calibrationMessage;

    public ReportUpdateParam() {
        gmtUpdate = new Date();
    }
}
