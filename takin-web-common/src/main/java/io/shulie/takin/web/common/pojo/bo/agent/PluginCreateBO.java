package io.shulie.takin.web.common.pojo.bo.agent;

import lombok.Data;

/**
 * @Description 新增插件BO
 * @Author ocean_wll
 * @Date 2021/11/15 11:14 上午
 */
@Data
public class PluginCreateBO {

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 插件类型
     *
     * @see io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum
     */
    private Integer pluginType;

    /**
     * 依赖包
     */
    private String dependenciesInfo;

    /**
     * 插件版本
     */
    private String pluginVersion;

    /**
     * 是否定制 false:非定制 true:定制
     */
    private Boolean isCustomMode;

    /**
     * 是否为公共模块 false:非公共 true:公共
     */
    private Boolean isCommonModule;

    /**
     * 下载路径
     */
    private String downloadPath;
}
