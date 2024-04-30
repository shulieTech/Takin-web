package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_agent_mock_data")
public class AgentMockDataEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(value = "report_id")
    private Long reportId;
    @TableField(value = "collect_start_time")
    private Date collectStartTime;
    @TableField(value = "collect_end_time")
    private Date collectEndTime;
    @TableField(value = "app_name")
    private String appName;
    @TableField(value = "agent_id")
    private String agentId;
    @TableField(value = "mock_service")
    private String mockService;
    @TableField(value = "mock_method")
    private String mockMethod;
    @TableField(value = "total_cost")
    private Long totalCost;
    @TableField(value = "failure_count")
    private Long failureCount;
    @TableField(value = "success_count")
    private Long successCount;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "tenant_id")
    private Long tenantId;
    @TableField(value = "env_code")
    private String envCode;
}
