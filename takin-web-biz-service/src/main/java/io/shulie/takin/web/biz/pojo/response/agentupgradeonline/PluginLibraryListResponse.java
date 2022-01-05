package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 插件版本库(PluginLibrary)controller 详情响应类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:24:03
 */
@ApiModel("出参类-详情出参")
@Data
public class PluginLibraryListResponse {

    @ApiModelProperty("id")
    private Long id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date gmtUpdate;

    @ApiModelProperty("插件名")
    private String pluginName;

    @ApiModelProperty("插件类型 0:插件 1:主版本 2:agent版本")
    private Integer pluginType;

    @ApiModelProperty("插件版本")
    private String version;

    @ApiModelProperty("是否为定制插件 0:否 1:是")
    private Integer isCustomMode;

    @ApiModelProperty("更新说明")
    private String updateDescription;

    @ApiModelProperty("是否有依赖")
    private Boolean hasDependence;
}
