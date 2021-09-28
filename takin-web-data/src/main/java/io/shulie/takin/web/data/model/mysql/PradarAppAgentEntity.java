package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 应用状态表
 */
@Data
@TableName(value = "pradar_app_agent")
public class PradarAppAgentEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主机IP
     */
    @TableField(value = "app_group_id")
    private Long appGroupId;

    /**
     * 主机IP PORT
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 机器名
     */
    @TableField(value = "machine_room")
    private String machineRoom;

    /**
     * 主机IP
     */
    @TableField(value = "host_port")
    private String hostPort;

    /**
     * 主机名称
     */
    @TableField(value = "hostname")
    private String hostname;

    /**
     * agent状态，1：已上线，2：暂停中，3：已下线
     */
    @TableField(value = "agent_status")
    private Integer agentStatus;

    /**
     * agent版本
     */
    @TableField(value = "agent_version")
    private String agentVersion;

    /**
     * 插入时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @TableField(value = "modify_time")
    private LocalDateTime modifyTime;

    @TableField(value = "deleted")
    private Integer deleted;

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
