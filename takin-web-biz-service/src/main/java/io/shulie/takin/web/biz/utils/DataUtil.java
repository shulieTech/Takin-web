package io.shulie.takin.web.biz.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 数据处理函数
 * @Author: liyuanba
 * @Date: 2021/10/27 4:18 下午
 */
public class DataUtil {
    /**
     * 从数据列表中获取经过过滤和排序后的第一个元素
     * @param list      原始列表
     * @param comparator 排序函数
     * @param filters 过滤函数列表
     * @param <T>       对象类型
     * @return  返回过和排序后的第一个对象
     */
    public static <T> T getFirst(List<T> list, Comparator<? super T> comparator, Predicate<? super T>... filters) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Stream<T> stream = list.stream().filter(Objects::nonNull);
        if (null != filters) {
            for (Predicate<? super T> filter : filters) {
                stream = stream.filter(filter);
            }
        }
        if (null != comparator) {
            stream = stream.sorted(comparator);
        }
        return stream.findFirst().orElse(null);
    }
}
