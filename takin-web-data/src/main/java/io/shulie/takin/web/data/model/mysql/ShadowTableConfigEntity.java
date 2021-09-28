package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 影子表配置表
 */
@Data
@TableName(value = "t_shadow_table_config")
public class ShadowTableConfigEntity {
    /**
     * 影子表配置id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 影子表所属数据源
     */
    @TableField(value = "SHADOW_DATASOURCE_ID")
    private Long shadowDatasourceId;

    /**
     * 需要使用影子表的表名
     */
    @TableField(value = "SHADOW_TABLE_NAME")
    private String shadowTableName;

    /**
     * 该表 有哪些操作
     */
    @TableField(value = "SHADOW_TABLE_OPERATION")
    private String shadowTableOperation;

    /**
     * 是否使用 影子表 1 使用 0 不使用
     */
    @TableField(value = "ENABLE_STATUS")
    private Integer enableStatus;

    /**
     * 插入时间
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
