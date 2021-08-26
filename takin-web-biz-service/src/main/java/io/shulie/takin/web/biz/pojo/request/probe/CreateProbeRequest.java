package io.shulie.takin.web.biz.pojo.request.probe;

import javax.validation.constraints.NotBlank;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/8 3:05 下午
 */
@Data
@ApiModel("入参类 --> 创建探针入参类")
public class CreateProbeRequest {

    @ApiModelProperty(value = "探针包绝对路径", required = true)
    @NotBlank(message = "探针包绝对路径" + AppConstants.MUST_BE_NOT_NULL)
    private String probePath;

}
