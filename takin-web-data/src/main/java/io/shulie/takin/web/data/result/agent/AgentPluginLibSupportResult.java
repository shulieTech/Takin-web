package io.shulie.takin.web.data.result.agent;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/13 11:01 上午
 */
@Data
public class AgentPluginLibSupportResult {

    private Long id;
    private Long pluginId;
    private String libName;
    private String libVersionRegexp;
    private Boolean isIgnore;

}
