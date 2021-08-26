package io.shulie.takin.web.biz.pojo.response.application;

import java.util.List;
import java.util.regex.Pattern;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/13 7:22 下午
 */
@Data
public class AgentPluginSupportResponse {
    private Long pluginId;
    private String pluginType;
    private String pluginName;
    private String libName;
    private Boolean isIgnore;
    private List<Pattern> regexpList;
}
