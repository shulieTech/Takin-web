package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_report_application_summary")
public class ReportApplicationSummaryEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "report_id")
    private Long reportId;

    @TableField(value = "application_name")
    private String applicationName;

    /**
     * 总机器数
     */
    @TableField(value = "machine_total_count")
    private Integer machineTotalCount;

    /**
     * 风险机器数
     */
    @TableField(value = "machine_risk_count")
    private Integer machineRiskCount;
}
