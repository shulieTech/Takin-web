package io.shulie.takin.web.biz.pojo.request.application;

import io.shulie.takin.ext.content.emus.SamplerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("应用入口服务查询对象-通过取样器类型查询")
public class ApplicationEntrancesSampleTypeQueryRequest {
    @NotEmpty
    @ApiModelProperty("应用名称")
    private String applicationName;

    @NotNull
    @ApiModelProperty("取样器服务类型")
    private SamplerTypeEnum samplerType;
}
