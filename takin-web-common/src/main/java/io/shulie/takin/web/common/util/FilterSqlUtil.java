package io.shulie.takin.web.common.util;

import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * @author qianshui
 * @date 2020/11/16 下午4:59
 */
public class FilterSqlUtil {

    public static String buildFilterSql(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        ids.stream().forEach(uid -> {
            sb.append(uid);
            sb.append(",");
        });
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(")");
        return sb.toString();
    }
}
