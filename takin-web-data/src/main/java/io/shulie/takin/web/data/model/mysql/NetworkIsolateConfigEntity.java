package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 网络隔离配置表
 */
@Data
@TableName(value = "t_network_isolate_config")
public class NetworkIsolateConfigEntity {
    /**
     * 主键id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 应用名称
     */
    @TableField(value = "APP_NAME")
    private String appName;
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
    /**
     * VIP地址
     */
    @TableField(value = "VIP_ADDR")
    private String vipAddr;

    /**
     * 负载均衡类型
     */
    @TableField(value = "BALANCE_TYPE")
    private String balanceType;

    /**
     * 地址池名称，多个用逗号分隔
     */
    @TableField(value = "POOL_NAME")
    private String poolName;

    /**
     * 隔离状态(0:未隔离，1:预隔离，2:隔离)
     */
    @TableField(value = "ISOLATED_STATUS")
    private Integer isolatedStatus;

    /**
     * 是否有效: Y:有效 N:无效
     */
    @TableField(value = "ACTIVE")
    private String active;

    /**
     * 隔离出错信息
     */
    @TableField(value = "FAILED_DETAIL")
    private String failedDetail;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 变更时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;

    /**
     * 隔离成功IP，多个用逗号分隔
     */
    @TableField(value = "ISOLATED_IP")
    private String isolatedIp;
}
