package io.shulie.takin.web.biz.service.interfaceperformance.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: vernon
 * @Date: 2022/5/24 14:42
 * @Description:
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    ActionEnum action();

    public enum ActionEnum {
        create, delete, select, detail, update;
    }

}
