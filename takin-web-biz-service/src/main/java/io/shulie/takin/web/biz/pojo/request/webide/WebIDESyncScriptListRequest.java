package io.shulie.takin.web.biz.pojo.request.webide;

import lombok.Data;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2022/6/20 1:43 下午
 */
@Data
public class WebIDESyncScriptListRequest {

    private List<WebIDESyncScriptRequest> scripts;
}
