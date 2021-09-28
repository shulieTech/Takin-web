package io.shulie.takin.web.data.model.mysql.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.ToString;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.model.mysql.base
 * @ClassName: TenantBaseEntity
 * @Description:最顶层
 * @Date: 2021/9/27 18:06
 */
@Data
@ToString(callSuper = true)
public class TenantBaseEntity {

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

}
