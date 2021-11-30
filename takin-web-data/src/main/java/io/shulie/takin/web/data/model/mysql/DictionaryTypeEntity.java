package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
 * 数据字典分类表
 */
@Data
@TableName(value = "t_dictionary_type")
public class DictionaryTypeEntity extends TenantBaseEntity {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 分类名称
     */
    @TableField(value = "TYPE_NAME")
    private String typeName;

    /**
     * 是否启用
     */
    @TableField(value = "ACTIVE")
    private String active;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDate createTime;

    /**
     * 更新时间
     */
    @TableField(value = "MODIFY_TIME")
    private LocalDate modifyTime;

    /**
     * 创建人
     */
    @TableField(value = "CREATE_USER_CODE")
    private String createUserCode;

    /**
     * 更新人
     */
    @TableField(value = "MODIFY_USER_CODE")
    private String modifyUserCode;

    /**
     * 上级分类编码
     */
    @TableField(value = "PARENT_CODE")
    private String parentCode;

    /**
     * 分类别名
     */
    @TableField(value = "TYPE_ALIAS")
    private String typeAlias;

    /**
     * 是否为子分类
     */
    @TableField(value = "IS_LEAF")
    private String isLeaf;
}
