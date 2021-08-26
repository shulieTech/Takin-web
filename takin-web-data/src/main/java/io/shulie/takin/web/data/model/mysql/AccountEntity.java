package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 账户表
 */
@Data
@TableName(value = "t_ac_account")
public class AccountEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "uid")
    private Long uid;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 标签
     */
    @TableField(value = "tags")
    private Long tags;

    /**
     * 扩展字段
     */
    @TableField(value = "features")
    private String features;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value = "gmt_update")
    private LocalDateTime gmtUpdate;
}
