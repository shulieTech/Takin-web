package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_report_mock")
public class ReportMockEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(value = "report_id")
    private Long reportId;
    @TableField(value = "start_time")
    private Date startTime;
    @TableField(value = "end_time")
    private Date endTime;
    @TableField(value = "app_name")
    private String appName;
    @TableField(value = "mock_name")
    private String mockName;
    @TableField(value = "mock_type")
    private String mockType;
    @TableField(value = "mock_script")
    private String mockScript;
    @TableField(value = "mock_status")
    private String mockStatus;
    @TableField(value = "failure_count")
    private Long failureCount;
    @TableField(value = "success_count")
    private Long successCount;
    @TableField(value = "avg_rt")
    private Double avgRt;
    @TableField(value = "tenant_id")
    private Long tenantId;
    @TableField(value = "env_code")
    private String envCode;
}
