/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: opensource@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.cloud.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author qianshui
 * @date 2020/4/20 下午2:56
 */
public class UrlUtil {

    public static Boolean checkEqual(String pradarPath, String jmeterPath) {
        if (StringUtils.isBlank(pradarPath) || StringUtils.isBlank(jmeterPath)) {
            return false;
        }
        int pos = pradarPath.indexOf("?");
        if (pos > 0) {
            pradarPath = pradarPath.substring(0, pos);
        }
        pos = jmeterPath.indexOf("?");
        if (pos > 0) {
            jmeterPath = jmeterPath.substring(0, pos);
        }
        pradarPath = pradarPath.replace("/", "");
        jmeterPath = jmeterPath.replace("/", "");
        //匹配 /api/employee/{id} 的情况
        jmeterPath = jmeterPath.replace("$", "");
        //endWith 匹配 网关转发时，加前缀的情况 /partner-open-api  网关转发，去前缀的情况 /api
        return pradarPath.equals(jmeterPath) || pradarPath.endsWith(jmeterPath) || jmeterPath.endsWith(pradarPath);
    }

    /**
     * 删除字符串中，冒号及之前的字符
     *
     * @param str -
     * @return -
     */
    private static String deleteFront(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        int index = str.lastIndexOf(":");
        if (index == -1) {
            return str;
        }
        str = str.substring(index + 1);
        if (StringUtils.startsWith(str, "/")) {
            str = str.substring(1);
        }
        index = str.indexOf("/");
        return str.substring(index + 1);
    }
}
