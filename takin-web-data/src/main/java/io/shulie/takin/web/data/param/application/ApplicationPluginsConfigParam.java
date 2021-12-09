package io.shulie.takin.web.data.param.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * (ApplicationPluginsConfig)实体类
 *
 * @author caijy
 * @date 2021-05-18 16:48:12
 */
@Data
@Valid
public class ApplicationPluginsConfigParam extends UserCommonExt {
    private Long id;
    @ApiModelProperty(value = "应用ID")
    @NotNull
    private Long applicationId;
    @ApiModelProperty(value = "应用名称")
    private String applicationName;
    @ApiModelProperty(value = "配置项")
    private String configItem;
    @ApiModelProperty(value = "配置项key")
    private String configKey;
    @ApiModelProperty(value = "配置值 表示具体时间 -1=与业务key一致")
    private String configValue;
    @ApiModelProperty(value = "配置说明")
    private String configDesc;
}
