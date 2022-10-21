package io.shulie.takin.web.biz.service.pressureresource.common;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum PassEnum {
    PASS_YES(0, "是"),
    PASS_NO(1, "否");

    int code;
    String name;

    PassEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static List<String> defaultPass = Arrays.asList("httpclient5", "jdk-http", "okhttpv3");

    public static int defaultPass(String middlewareName) {
        if (StringUtils.isBlank(middlewareName)) {
            return PASS_NO.getCode();
        }
        if (defaultPass.contains(middlewareName.toLowerCase())) {
            return PASS_YES.getCode();
        }
        return PASS_NO.getCode();
    }
}
