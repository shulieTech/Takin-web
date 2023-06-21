package io.shulie.takin.cloud.common.utils;

import org.apache.commons.lang3.StringUtils;

public class PtsXmlContentUtils {

    public static String replace2Jmx(String content) {
        if(StringUtils.isBlank(content)) {
            return content;
        }
        String jmxContent = content.replace("<", "&lt;");
        jmxContent = jmxContent.replace( ">", "&gt;");
        return jmxContent;
    }

    public static String parseFromJmx(String content) {
        if(StringUtils.isBlank(content)) {
            return content;
        }
        String jmxContent = content.replace("&lt;", "<");
        jmxContent = jmxContent.replace( "&gt;", ">");
        return jmxContent;
    }

}
