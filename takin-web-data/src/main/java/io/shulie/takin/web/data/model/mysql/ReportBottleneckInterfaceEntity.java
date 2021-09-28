package io.shulie.takin.web.data.model.mysql;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_report_bottleneck_interface")
public class ReportBottleneckInterfaceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "report_id")
    private Long reportId;

    @TableField(value = "application_name")
    private String applicationName;

    @TableField(value = "sort_no")
    private Integer sortNo;

    /**
     * 接口类型
     */
    @TableField(value = "interface_type")
    private String interfaceType;

    @TableField(value = "interface_name")
    private String interfaceName;

    @TableField(value = "tps")
    private BigDecimal tps;

    @TableField(value = "rt")
    private BigDecimal rt;

    @TableField(value = "node_count")
    private Integer nodeCount;

    @TableField(value = "error_reqs")
    private Integer errorReqs;

    @TableField(value = "bottleneck_weight")
    private BigDecimal bottleneckWeight;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
