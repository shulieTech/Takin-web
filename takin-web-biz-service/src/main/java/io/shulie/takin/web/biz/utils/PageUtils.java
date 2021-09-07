package io.shulie.takin.web.biz.utils;

import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.PageHelper;

/**
 * @author hengyu
 * @date 2020/1/8 17:22
 */
@Deprecated
public class PageUtils {

    /**
     * 清除PageHelper
     */
    public static void clearPageHelper() {
        PageHelper.clearPage();
    }

    public static <T> List<T> getPage(Boolean needPage, Integer current, Integer pageSize, List<T> filteredSources) {
        if (filteredSources == null || filteredSources.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> pagedTargets = new ArrayList<>();
        if (needPage) {
            Integer page = current;
            if (page < 0) {
                page = 0;
            }
            int offset = page * pageSize;
            if (offset <= filteredSources.size() - 1) {
                for (int index = offset, count = 0; index < filteredSources.size() && count < pageSize;
                     index++, count++) {
                    pagedTargets.add(filteredSources.get(index));
                }
            }
        } else {
            pagedTargets.addAll(filteredSources);
        }
        return pagedTargets;
    }
}
