package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName t_report_base_line_problem
 */
@TableName(value ="t_report_base_line_problem")
@Data
public class TReportBaseLinkProblem implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long sceneId;

    /**
     * 
     */
    private Long reportId;

    /**
     * 
     */
    private Integer lineType;

    private String rpcId;

    private String rpcType;

    private Integer logType;

    /**
     * 
     */
    private String binRef;

    /**
     * 
     */
    private Long activityId;

    private String activityName;

    /**
     * 节点服务名
     */
    private String serviceName;

    /**
     * 节点appName
     */
    private String appName;
    /**
     * 节点方法
     */
    private String methodName;
    /**
     * 节点问题原因
     */
    private String reason;

    /**
     * 基线rt
     */
    private BigDecimal baseRt;

    /**
     * 基线成功率
     */
    private BigDecimal baseSuccessRate;

    /**
     * 当前报告的rt
     */
    private BigDecimal rt;

    /**
     * 当前报告的成功率
     */
    private BigDecimal successRate;

    private BigDecimal totalRequest;

    /**
     * trace快照
     */
    private String traceSnapshot;

    /**
     * 
     */
    private Integer isDelete;

    /**
     * 
     */
    private String envCode;

    /**
     * 
     */
    private Long tenantId;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private String createName;

    /**
     * 
     */
    private Integer columnName;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private String updateName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}