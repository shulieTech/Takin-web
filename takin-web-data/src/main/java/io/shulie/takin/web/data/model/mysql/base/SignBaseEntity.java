package io.shulie.takin.web.data.model.mysql.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: 南风
 * @Date: 2022/2/24 3:37 下午
 * 需要启用数据签名的表映射对象 继承
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SignBaseEntity extends UserBaseEntity{

    @TableField(value = "sign",fill = FieldFill.INSERT)
    private String sign;
}
