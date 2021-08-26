package com.pamirs.takin.entity.domain.vo.dsmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hengyu
 */
@Data
@ApiModel(value = "DsServerVO", description = "影子配置")
public class DsServerVO {

    @ApiModelProperty(name = "applicationName", value = "应用名称")
    private String applicationName;

    @ApiModelProperty(name = "dsType")
    private Byte dsType;

    @ApiModelProperty(name = "url", value = "数据库url")
    private String url;

    @ApiModelProperty(name = "status", value = "状态")
    private Integer status;

    @ApiModelProperty(name = "config", value = "影子JSON配置")
    private String config;
}
