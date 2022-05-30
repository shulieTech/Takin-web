package io.shulie.takin.cloud.data.model.mysql;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author -
 */
@Data
@TableName(value = "t_report")
public class ReportEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务Id
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 资源Id
     */
    @TableField("resource_id")
    private String resourceId;

    /**
     * 压测引擎任务Id
     */
    @TableField("job_id")
    private Long jobId;

    /**
     * 数据校准状态
     * amdb	    cloud
     * 00		00      未校准
     * 01		01      校准中
     * 10		10      校准失败
     * 11		11      校准成功
     */
    @TableField("calibration_status")
    private Integer calibrationStatus;

    /**
     * 流量消耗
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 场景名称
     */
    @TableField(value = "scene_name")
    private String sceneName;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 报表生成状态:0/就绪状态，1/生成中,2/完成生成
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 报告类型；0普通场景，1流量调试
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 压测结论: 0/不通过，1/通过
     */
    @TableField(value = "conclusion")
    private Integer conclusion;

    /**
     * 请求总数
     */
    @TableField(value = "total_request")
    private Long totalRequest;

    /**
     * 施压类型,0:并发,1:tps,2:自定义;不填默认为0
     */
    @TableField(value = "pressure_type")
    private Integer pressureType;

    /**
     * 平均并发数
     */
    @TableField(value = "avg_concurrent")
    private BigDecimal avgConcurrent;

    /**
     * 目标TPS
     */
    @TableField(value = "tps")
    private Integer tps;

    /**
     * 平均tps
     */
    @TableField(value = "avg_tps")
    private BigDecimal avgTps;

    /**
     * 平均响应时间
     */
    @TableField(value = "avg_rt")
    private BigDecimal avgRt;

    /**
     * 最大并发
     */
    @TableField(value = "concurrent")
    private Integer concurrent;

    /**
     * 成功率
     */
    @TableField(value = "success_rate")
    private BigDecimal successRate;

    /**
     * sa
     */
    @TableField(value = "sa")
    private BigDecimal sa;

    /**
     * 操作用户ID
     */
    @TableField(value = "operate_id")
    private Long operateId;

    /**
     * 扩展字段，JSON数据格式
     */
    @TableField(value = "features")
    private String features;

    /**
     * 是否删除:0/正常，1、已删除
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;

    @TableField(value = "dept_id")
    private Long deptId;

    @TableField(value = "script_id")
    private Long scriptId;

    /**
     * 锁报告
     */
    @TableField(value = "`lock`")
    private Integer lock;

    /**
     * 脚本节点树
     */
    @TableField(value = "script_node_tree")
    private String scriptNodeTree;

    /**
     * 用户主键
     */
    @TableField(value = "user_id")
    private Long userId;
    /**
     * 租户主键
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
    /**
     * 用户id
     */
    @TableField(value = "env_code")
    private String envCode;

    @TableField(value = "calibration_message")
    private String calibrationMessage;

//    @TableField(value = "sign" , fill = FieldFill.INSERT)
//    private String sign;
//    @TableField(value = "custom_id")
//    private Long customId;
//    @TableField(value = "create_uid")
//    private Long createUid;
//    @TableField(value = "customer_id")
//    private Long customerId;




}
