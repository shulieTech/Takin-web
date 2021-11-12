package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import java.util.Date;

import lombok.Data;

/**
 * @Description agent插件基本信息
 * @Author ocean_wll
 * @Date 2021/11/10 4:34 下午
 */
@Data
public class PluginInfo {

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
     * 更新内容
     */
    private String updateInfo;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;
}
