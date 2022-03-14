package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 挡板实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_link_guard")
@EnableSign
public class LinkGuardEntity extends UserBaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 项目名称
     */
    @TableField(value = "application_name")
    private String applicationName;

    /**
     * 应用id
     */
    @TableField(value = "application_id")
    private Long applicationId;

    /**
     * 出口信息
     */
    @TableField(value = "method_info")
    private String methodInfo;

    /**
     * GROOVY脚本
     */
    @TableField(value = "groovy")
    private String groovy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;


    /**
     * 0:未启用；1:启用
     */
    @TableField(value = "is_enable")
    private Integer isEnable;

    @TableField(value = "sign",fill = FieldFill.INSERT)
    private String sign;

    private Long customerId;

}
