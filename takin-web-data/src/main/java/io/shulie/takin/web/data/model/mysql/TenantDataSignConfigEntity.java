package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 租户数据存储签名配置(TenantDataSignConfig)实体类
 *
 * @author 南风
 * @date 2022-05-23 15:45:48
 */
@Data
@TableName(value = "t_tenant_data_sign_config")
@ToString(callSuper = true)
public class TenantDataSignConfigEntity  implements Serializable {
    private static final long serialVersionUID = 219396217467596875L;
    
   /**
   * 是否开启数据签名 0:否;1:是
   */  
    private Integer status;

    /**
     * 租户id
     */
    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private Long tenantId;

    /**
     * 用户id
     */
    @TableField(value = "env_code", fill = FieldFill.INSERT)
    private String envCode;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO,value = "id")
    private Long id;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    @TableLogic
    private Integer isDeleted;

    private String sign;
    
}
