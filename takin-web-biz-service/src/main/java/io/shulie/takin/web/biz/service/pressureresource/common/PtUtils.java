package io.shulie.takin.web.biz.service.pressureresource.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/8 11:43 AM
 */
public class PtUtils {
    public static boolean isShadow(String var) {
        if (StringUtils.isBlank(var)) {
            return false;
        }
        if (var.toUpperCase().startsWith("PT")) {
            return true;
        }
        return false;
    }
}
