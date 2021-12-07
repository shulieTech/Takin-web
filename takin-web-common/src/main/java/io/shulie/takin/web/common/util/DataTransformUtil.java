package io.shulie.takin.web.common.util;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.util.CollectionUtils;

/**
 * 数据转换工具类
 *
 * @author liuchuan
 * @date 2021/11/23 2:04 下午
 */
public class DataTransformUtil {

    /**
     * 一个象的list 转 另一个对象的list
     * 适合两者的字段名称及类型, 都一样
     * 使用 json 方式
     *
     * 要转换的类, 需要有无参构造器
     *
     * 100000 数据, 花费时间 1622712527764
     * 数据越多, 此方法愈快一些,
     * stream 方法加了 try/catch 是一部分原因, newInstance 可能也是一部分原因
     *
     * @param sourceList  源list
     * @param targetClazz 目标对象类对象
     * @param <T>         要转换的类
     * @return 另一个对象的list
     */
    public static <T> List<T> list2list(List<?> sourceList, Class<T> targetClazz) {
        return CollectionUtils.isEmpty(sourceList) ? new ArrayList<>(0)
            : JsonUtil.json2List(JsonUtil.bean2Json(sourceList), targetClazz);
    }

    /**
     * 按照Bean对象属性创建对应的Class对象，并忽略某些属性
     * 如果源头bean为null, 则吐出的也是null
     *
     * @param <T>              对象类型
     * @param source           源Bean对象
     * @param tClass           目标Class
     * @param ignoreProperties 不拷贝的的属性列表
     * @return 目标对象
     */
    public static <T> T copyBeanPropertiesWithNull(Object source, Class<T> tClass, String... ignoreProperties) {
        return source == null ? null : BeanUtil.copyProperties(source, tClass, ignoreProperties);
    }

}
