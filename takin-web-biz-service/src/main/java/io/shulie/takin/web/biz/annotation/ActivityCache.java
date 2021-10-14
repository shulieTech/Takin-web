package io.shulie.takin.web.biz.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ActivityCache {
    int expireTime() default 10;
}
