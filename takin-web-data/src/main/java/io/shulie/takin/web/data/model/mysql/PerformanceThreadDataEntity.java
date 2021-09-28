package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
 * 性能线程数据表
 *
 * @author qianshui
 * @date 2020/11/4 下午1:53
 */
@Data
@TableName(value = "t_performance_thread_data")
public class PerformanceThreadDataEntity extends TenantBaseEntity {

    @TableField(value = "base_id")
    private Long baseId;

    @TableField(value = "agent_id")
    private String agentId;

    @TableField(value = "app_name")
    private String appName;

    @TableField(value = "app_ip")
    private String appIP;

    @TableField(value = "timestamp")
    private String timestamp;

    @TableField(value = "thread_data")
    private String threadData;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

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
