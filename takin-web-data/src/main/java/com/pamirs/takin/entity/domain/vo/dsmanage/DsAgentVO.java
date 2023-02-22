package com.pamirs.takin.entity.domain.vo.dsmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Create by xuyh at 2020/3/15 18:17.
 */
@Data
@ApiModel(value = "DsAgentVO", description = "影子库表配置入参")
public class DsAgentVO {
    @ApiModelProperty(name = "applicationName", value = "应用名称")
    private String applicationName;

    @ApiModelProperty(name = "dsType", value = "库表类型，0：影子库 1：影子表")
    private Byte dsType;

    @ApiModelProperty(name = "url", value = "数据库url")
    private String url;

    @ApiModelProperty(name = "username", value = "数据库username")
    private String username;

    @ApiModelProperty(name = "status", value = "状态")
    private Byte status;

    @ApiModelProperty(name = "config", value = "影子表配置")
    private String shadowTableConfig;

    @ApiModelProperty(name = "config", value = "影子库配置")
    private Configurations shadowDbConfig;
}
