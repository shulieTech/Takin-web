package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 压测引擎配置
 */
@Data
@TableName(value = "t_pressure_test_engine_config")
public class PressureTestEngineConfigEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 压测引擎名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 压测引擎类型 PTS，SPT，JMETER
     */
    @TableField(value = "type")
    private String type;

    /**
     * access_key
     */
    @TableField(value = "access_key")
    private String accessKey;

    /**
     * secret_key
     */
    @TableField(value = "secret_key")
    private String secretKey;

    /**
     * 地域ID
     */
    @TableField(value = "region_id")
    private String regionId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 0-可用 1-不可用
     */
    @TableField(value = "status")
    private Boolean status;

    /**
     * 是否删除 0-未删除、1-已删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;

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
