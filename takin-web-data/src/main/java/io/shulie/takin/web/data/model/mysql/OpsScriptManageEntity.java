package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 运维脚本主表(OpsScriptManage)实体类
 *
 * @author caijy
 * @date 2021-06-16 10:35:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_ops_script_manage")
public class OpsScriptManageEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 815261567033798687L;

    /**
     * 脚本名称
     */
    private String name;

    /**
     * 来源于字典。脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本
     */
    private Integer scriptType;

    /**
     * 租户ID
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id", fill = FieldFill.INSERT)
    private Long userId;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

    /**
     * 执行状态 0=待执行,1=执行中,2=已执行
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtUpdate;

}
