package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_trace_manage")
public class TraceManageEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 追踪对象
     */
    @TableField(value = "trace_object")
    private String traceObject;

    /**
     * 报告id
     */
    @TableField(value = "report_id")
    private Long reportId;

    @TableField(value = "agent_id")
    private String agentId;

    /**
     * 服务器ip
     */
    @TableField(value = "server_ip")
    private String serverIp;

    /**
     * 进程id
     */
    @TableField(value = "process_id")
    private Integer processId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 拓展字段
     */
    @TableField(value = "feature")
    private String feature;

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
