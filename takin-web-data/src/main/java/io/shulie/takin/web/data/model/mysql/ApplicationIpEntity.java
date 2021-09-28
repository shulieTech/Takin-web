package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 应用ip配置表
 */
@Data
@TableName(value = "t_application_ip")
public class ApplicationIpEntity {
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    @TableField(value = "APPLICATION_NAME")
    private String applicationName;

    @TableField(value = "TYPE")
    private String type;

    @TableField(value = "IP")
    private String ip;

    @TableField(value = "SYSTEM_NAME")
    private String systemName;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
