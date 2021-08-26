package io.shulie.takin.web.biz.utils;

import com.google.common.collect.Lists;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author caijy
 * 属性复制工具
 * 比beanutil性能好
 */
public class CopyUtils {

    public static <S, T> T copyFields(S s, Class<T> tClass) {
        BeanCopier beanCopier = BeanCopier.create(s.getClass(), tClass, false);
        T o = null;
        try {
            o = tClass.newInstance();
            beanCopier.copy(s, o, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static <S, T> List<T> copyFieldsList(List<S> sList, Class<T> t) {
        List<T> resultList = Lists.newCopyOnWriteArrayList();
        if (CollectionUtils.isEmpty(sList)) {
            return resultList;
        }
        S s = sList.get(0);
        BeanCopier beanCopier = BeanCopier.create(s.getClass(), t, false);
        try {
            for (S member : sList) {
                T resultT = t.newInstance();
                beanCopier.copy(member, resultT, null);
                resultList.add(resultT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
