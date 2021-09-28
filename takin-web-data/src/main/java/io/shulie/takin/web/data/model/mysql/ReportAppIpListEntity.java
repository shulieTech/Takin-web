package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_report_app_ip_list")
public class ReportAppIpListEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField(value = "report_id")
    private String reportId;

    @TableField(value = "link_id")
    private String linkId;

    @TableField(value = "application_name")
    private String applicationName;

    @TableField(value = "type")
    private String type;

    @TableField(value = "system_name")
    private String systemName;

    @TableField(value = "ip")
    private String ip;

    @TableField(value = "cpu")
    private String cpu;

    @TableField(value = "memory")
    private String memory;

    @TableField(value = "io_read")
    private String ioRead;

    @TableField(value = "io_write")
    private String ioWrite;

    @TableField(value = "io_all")
    private String ioAll;

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
