package com.pamirs.takin.entity.domain.vo.entracemanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/7/4 00:24
 */
@Data
public class ApiUpdateVo {
    @ApiModelProperty(name = "id", value = "主键")
    private String id;
    @ApiModelProperty(name = "applicationName", value = "应用名")
    private String applicationName;
    @ApiModelProperty(name = "api", value = "地址")
    private String api;
    @ApiModelProperty(name = "method", value = "方法类型")
    private String method;
}
