/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
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

package io.shulie.takin.web.common.util.whitelist;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author 无涯
 * @date 2021/4/23 1:33 上午
 */
public class WhitelistUtil {

    /**
     * 校验白名单 接口格式
     * 不允许 单独 空格 斜杠 *
     *
     * @param interfaceName 接口名称
     * @return 是否正确
     */
    public static boolean checkWhiteFormatError(String interfaceName, Integer number) {
        return StringUtils.isBlank(interfaceName) || interfaceName.trim().length() < number;
    }



    /**
     * 组装去重唯一白名单id
     * @param type
     * @param interfaceName
     * @return
     */
    public static String buildWhiteId(Object type, String interfaceName) {
        return interfaceName + "@@" + (type == null ? "" :type);
    }

    public static String buildAppNameWhiteId(String appName,Object type, String interfaceName) {
        return  appName + "@@"+  interfaceName + "@@" + (type == null ? "" :type);
    }

    /**
     * 判断是否重名
     * @param list
     * @param buildWhiteId
     * @return
     */
    public static Boolean isDuplicate(List<String> list,String buildWhiteId) {
        return CollectionUtils.isNotEmpty(list) && list.stream().filter(e -> e.equals(buildWhiteId)).count() > 1;
    }
}
