package io.shulie.takin.web.data.model.mysql;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_report_bottleneck_interface")
public class ReportBottleneckInterfaceEntity  extends TenantBaseEntity {
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
}
