package io.shulie.takin.web.data.model.mysql.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.model.mysql.base
 * @ClassName: UserBaseEntity
 * @Description: TODO
 * @Date: 2021/9/28 09:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UserBaseEntity extends TenantBaseEntity {

    /**
     * 用户id
     */
    @TableField(value = "user_id", fill = FieldFill.INSERT)
    private Long userId;
}
