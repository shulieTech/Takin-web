package io.shulie.takin.web.biz.service.pressureresource.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 5:13 PM
 */
public class DbNameUtil {
    /**
     * 获取解析库名
     *
     * @param url
     * @return
     */
    public static String getDbName(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }
        if (url.lastIndexOf("/") > 0) {
            url = url.substring(url.lastIndexOf("/") + 1);
        }
        if (url.indexOf("?") > 0) {
            url = url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    public static void main(String[] args) {
        System.out.println(getDbName("jdbc:mysql://127.0.0.1/trodb?useUnicode=true"));
        System.out.println(getDbName("jdbc:mysql://127.0.0.1/trodb"));
    }
}
