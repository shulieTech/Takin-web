package io.shulie.takin.web.data.model.mysql.dashboard;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 快捷入口表
 *
 * @author 张天赐
 */
@Data
@TableName(value = "t_quick_access")
public class QuickAccessEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 客户id
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;
    /**
     * 快捷键名称
     */
    @TableField(value = "quick_name")
    private String quickName;
    /**
     * 快捷键logo
     */
    @TableField(value = "quick_logo")
    private String quickLogo;
    /**
     * 实际地址
     */
    @TableField(value = "url_address")
    private String urlAddress;
    /**
     * 顺序
     */
    @TableField(value = "`order`")
    private Integer order;
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
     * 是否删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;
    /**
     * 是否启用
     */
    @TableField(value = "is_enable")
    private Boolean isEnable;
}
