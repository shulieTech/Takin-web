package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 数据字典基础数据表
 */
@Data
@TableName(value = "t_dictionary_data")
public class DictionaryDataEntity {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 数据字典分类
     */
    @TableField(value = "DICT_TYPE")
    private String dictType;

    /**
     * 序号
     */
    @TableField(value = "VALUE_ORDER")
    private Integer valueOrder;

    /**
     * 值名称
     */
    @TableField(value = "VALUE_NAME")
    private String valueName;

    /**
     * 值代码
     */
    @TableField(value = "VALUE_CODE")
    private String valueCode;

    /**
     * 语言
     */
    @TableField(value = "LANGUAGE")
    private String language;

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
     * 备注信息
     */
    @TableField(value = "NOTE_INFO")
    private String noteInfo;

    /**
     * 版本号
     */
    @TableField(value = "VERSION_NO")
    private Long versionNo;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
