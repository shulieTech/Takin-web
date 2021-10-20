package io.shulie.takin.web.data.model.mysql.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库映射共有类
 * 带有租户 key
 *
 * @author liuchuan
 * @date 2021/4/7 5:27 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonWithTenantKeyEntity extends NewBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField(fill = FieldFill.INSERT)
    private String tenantAppKey;

}
