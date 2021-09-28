package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * prada获取http接口表
 */
@Data
@TableName(value = "t_prada_http_data")
public class PradaHttpDataEntity {
    /**
     * prada获取http接口表id
     */
    @TableId(value = "TPHD_ID", type = IdType.INPUT)
    private Long tphdId;

    /**
     * 应用名称
     */
    @TableField(value = "APP_NAME")
    private String appName;

    /**
     * 接口名称
     */
    @TableField(value = "INTERFACE_NAME")
    private String interfaceName;

    /**
     * 接口类型(1.http 2.dubbo 3.禁止压测 4.job)
     */
    @TableField(value = "INTERFACE_TYPE")
    private Integer interfaceType;

    /**
     * 同步数据时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

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
