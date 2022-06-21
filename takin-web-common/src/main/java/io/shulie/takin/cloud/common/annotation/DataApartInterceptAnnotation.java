package io.shulie.takin.cloud.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 客户端数据隔离注解
 *
 * @author qianshui
 * @date 2020/7/22 下午11:24
 *
 * mybatis plus 数据隔离, 使用下面的类
 * @see io.shulie.takin.cloud.data.util.MPUtil
 * 下的 getTenantLQW(), getTenantQW()
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataApartInterceptAnnotation {

}
