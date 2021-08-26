package io.shulie.takin.web.biz.pojo.response.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/13 11:27 上午
 */
@Data
public class ApplicationLibSupportResponse {
    private String pluginType;
    private String pluginName;
    private String jarName;
    private Boolean isSupport;
}
