package io.shulie.takin.web.common.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 无涯
 * @date 2021/5/18 11:13 上午
 * @description 去除空格
 */
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trimmed {
    public static enum TrimmerType {
        SIMPLE,
        ALL_WHITESPACES,
        EXCEPT_LINE_BREAK;
    }

    TrimmerType value() default TrimmerType.ALL_WHITESPACES;
}
