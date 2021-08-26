package com.pamirs.takin.entity.domain.vo.entracemanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/7/5 03:15
 */
@Data
@ApiModel(value = "ApiCreateVo", description = "新增")
public class ApiCreateVo {
    @ApiModelProperty(name = "applicationName", value = "应用名")
    private String applicationName;
    @ApiModelProperty(name = "api", value = "入口地址")
    private String api;
    @ApiModelProperty(name = "method", value = "方法类型")
    private String method;
}
