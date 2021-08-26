package io.shulie.takin.web.biz.utils;

import com.pamirs.takin.entity.domain.entity.linkmanage.structure.Category;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mubai
 * @date 2020-07-02 14:42
 */
public class CategoryUtils {

    /**
     * 组装前端需要的展示数据
     */
    public static void assembleVo(Category tree) {
        if (tree == null) {
            return;
        }
        String serviceName = tree.getServiceName();
        if (StringUtils.isNotBlank(serviceName) && serviceName.contains("|")) {
            String[] split = serviceName.split("\\|");
            if (split.length < 3) {
                return;
            }
            tree.setServiceName(split[0]);
            tree.setServiceType(split[1]);
            tree.setServiceDetail(split[2]);
            if (CollectionUtils.isEmpty(tree.getChildren())) {
                tree.setChildren(null);
            }
        }
        if (CollectionUtils.isEmpty(tree.getChildren())) {
            return;
        }
        for (Category child : tree.getChildren()) {
            assembleVo(child);
        }
    }

}
