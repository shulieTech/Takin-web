package io.shulie.takin.web.biz.pojo.input.application;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 南风
 * @date 2021/10/19 4:01 下午
 */
@Data
@Valid
public class ApplicationDsDeleteInputV2 {

    @NotNull
    @ApiModelProperty(name = "记录id", value = "id")
    private Long id;

    @NotNull
    @ApiModelProperty(name = "中间件类型", value = "middlewareType")
    private String middlewareType;

    @NotNull
    @ApiModelProperty(name = "数据版本", value = "isNewData")
    private  Boolean isNewData;

    @NotNull
    @ApiModelProperty(name = "应用id", value = "applicationId")
    private Long applicationId;
}
