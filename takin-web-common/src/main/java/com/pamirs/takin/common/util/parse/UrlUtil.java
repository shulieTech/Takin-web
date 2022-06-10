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

package com.pamirs.takin.common.util.parse;

import cn.hutool.core.util.URLUtil;
import io.shulie.amdb.common.enums.RpcType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;

/**
 * @author qianshui
 * @date 2020/4/20 下午2:56
 */
@Slf4j
public class UrlUtil {
    public static String convertUrl(ActivityUtil.EntranceJoinEntity entranceJoinEntity) {
        // 虚拟入口
        if (StringUtils.isNotEmpty(entranceJoinEntity.getVirtualEntrance())) {
            return entranceJoinEntity.getVirtualEntrance();
        }
        //如果为http请求
        if (String.valueOf(RpcType.TYPE_WEB_SERVER).equals(entranceJoinEntity.getRpcType())) {
            String url = entranceJoinEntity.getServiceName();
            String[] urls = StringUtils.split(url, "|");
            if (urls == null || urls.length == 0) {
                return null;
            }
            url = urls[urls.length - 1].replace("//", "/");
            return deleteFront(url);
        }
        //如果为rpc请求
        if (String.valueOf(RpcType.TYPE_RPC).equals(entranceJoinEntity.getRpcType())) {
            String methodName = entranceJoinEntity.getMethodName();
            String substring = methodName.substring(0, methodName.indexOf("("));
            if (substring.endsWith("~")) {
                substring = substring.substring(0, substring.length() - 1);
            }
            return entranceJoinEntity.getServiceName().split(":")[0] + "#" + substring;
        }
        //如果为mq
        if (String.valueOf(RpcType.TYPE_MQ).equals(entranceJoinEntity.getRpcType())) {
            return entranceJoinEntity.getServiceName();
        }
        throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "入口URL匹配失败");
    }

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

    public static String parseUrl(String requestUrl) {
        String path = "";
        try {
            path = URLUtil.getPath(requestUrl);
        } catch (Throwable e) {
            // http://{ip}/path
            log.error("格式化URL错误,{}", requestUrl);
            try {
                path = requestUrl.substring(requestUrl.lastIndexOf("/") + 1);
            } catch (Throwable e1) {
                log.error("格式化URL错误,{}", requestUrl);
            }
        }
        return path;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static void main(String[] args) {
        String dubbo = "main-bill-provide-service|queryMainProvideRecord(com.zto.ztrace.domain.BillProvideRequestEntity)|com.zto.ztrace.service.IMainWayBillProvide:1.0|1";
        EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(dubbo);
        System.out.println(UrlUtil.convertUrl(entranceJoinEntity));
        String dubboWithNo = "main-bill-provide-service|queryMainProvideRecord(com.zto.ztrace.domain.BillProvideRequestEntity)|com.zto.ztrace.service.IMainWayBillProvide|1";
        EntranceJoinEntity entranceJoinEntityWithNo = ActivityUtil.covertEntrance(dubboWithNo);
        System.out.println(UrlUtil.convertUrl(entranceJoinEntityWithNo));
    }
}
