package io.shulie.takin.cloud.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author vernon
 * @date 2019/10/31 20:57
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTag {
    //名字
    String name();

    //类型
    Class type();

    //是否值转换
    boolean convert() default false;

    //转换规则
    String rule() default "";
}
