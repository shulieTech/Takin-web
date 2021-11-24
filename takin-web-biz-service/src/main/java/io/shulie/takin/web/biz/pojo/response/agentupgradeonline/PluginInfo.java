package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Description agent插件基本信息
 * @Author ocean_wll
 * @Date 2021/11/10 4:34 下午
 */
@Data
public class PluginInfo {

    @ApiModelProperty(value = "插件id")
    private Long id;

    /**
     * 插件名称
     */
    @ApiModelProperty(value = "插件名称")
    private String pluginName;

    /**
     * 插件类型
     *
     * @see io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum
     */
    @ApiModelProperty(value = "插件类型")
    private Integer pluginType;

    /**
     * 依赖包
     */
    private String dependenciesInfo;

    /**
     * 插件版本
     */
    @ApiModelProperty(value = "插件版本")
    private String version;

    /**
     * 是否定制 false:非定制 true:定制
     */
    @ApiModelProperty(value = "是否定制 false:非定制 true:定制")
    private Boolean isCustomMode;

    /**
     * 依赖模块
     */
    @ApiModelProperty(value = "依赖模块")
    private List<PluginInfo> dependenciesInfos;

    /**
     * 更新内容
     */
    @ApiModelProperty(value = "更新内容")
    private String updateDescription;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;
}
