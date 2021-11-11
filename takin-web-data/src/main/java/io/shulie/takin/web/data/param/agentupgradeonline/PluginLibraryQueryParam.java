package io.shulie.takin.web.data.param.agentupgradeonline;

import lombok.Data;

/**
 * @Description 插件库查询参数
 * @Author ocean_wll
 * @Date 2021/11/11 3:40 下午
 */
@Data
public class PluginLibraryQueryParam {

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 插件版本
     */
    private String version;

}
