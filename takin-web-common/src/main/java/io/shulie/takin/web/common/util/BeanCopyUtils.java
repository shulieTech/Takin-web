package io.shulie.takin.web.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.shulie.takin.common.beans.page.PagingList;
import org.springframework.beans.BeanUtils;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
public class BeanCopyUtils {

    public static <T> T copyObject(Object source, Class<T> targetClass) {
        return getInstance(targetClass);
    }

    public static <T> List<T> copyList(Object source, Class<T> targetClass) {
        if (source == null) {
            return new ArrayList<>();
        }
        if (source instanceof List) {
            return (List<T>)(((List)source).stream().map(item -> {
                T t = getInstance(targetClass);
                BeanUtils.copyProperties(item, t);
                return t;
            }).collect(Collectors.toList()));
        }
        return new ArrayList<>();
    }

    public static <T> PagingList<T> copyPagingList(PagingList<?> source, Class<T> tClass) {
        if (source == null) {
            return PagingList.empty();
        }
        List<T> collect = (List<T>)((PagingList)source).getList().stream().map(item -> {
            T t = getInstance(tClass);
            BeanUtils.copyProperties(item, t);
            return t;
        }).collect(Collectors.toList());
        return PagingList.of(collect, source.getTotal());
    }

    private static <T> T getInstance(Class<T> targetClass) {
        try {
            return targetClass.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
