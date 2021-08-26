package io.shulie.takin.web.common.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限
 *
 * @author qianshui
 * @date 2020/11/5 下午4:46
 */
@Documented
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataAuth {
    String tableAlias() default "";
}
