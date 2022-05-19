package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:57 下午
 */
public class PerformanceConvert {
    /**
     * 转为Entity
     *
     * @param input
     * @return
     */
    public static InterfacePerformanceConfigEntity convertConfigEntity(PerformanceConfigCreateInput input) {
        InterfacePerformanceConfigEntity entity = new InterfacePerformanceConfigEntity();
        // 基础信息
        entity.setName(input.getName());
        entity.setHttpMethod(input.getHttpMethod());
        entity.setRequestUrl(input.getRequestUrl());
        entity.setEntranceAppName(input.getEntranceAppName());
        entity.setTimeout(input.getTimeout());
        entity.setIsRedirect(input.getIsRedirect());

        // header头,body
        entity.setContentType(JsonHelper.bean2Json(input.getContentTypeVo()));
        entity.setHeaders(input.getHeaders());
        entity.setBody(input.getBody());
        entity.setCookies(input.getCookies());

        return entity;
    }
}
