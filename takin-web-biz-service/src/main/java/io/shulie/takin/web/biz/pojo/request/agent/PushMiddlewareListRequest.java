package io.shulie.takin.web.biz.pojo.request.agent;

import javax.validation.constraints.NotBlank;

import io.shulie.takin.cloud.common.constants.ValidConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/30 4:46 下午
 */
@Data
@ApiModel("入参类-推送应用的中间件列表")
public class PushMiddlewareListRequest implements ValidConstants {

    @ApiModelProperty(value = "中间件项目名称", required = true)
    @NotBlank(message = "中间件项目名称" + MUST_NOT_BE_NULL)
    private String artifactId;

    @ApiModelProperty(value = "中间件组织名称", required = true)
    @NotBlank(message = "中间件组织名称" + MUST_NOT_BE_NULL)
    private String groupId;

    @ApiModelProperty(value = "版本号", required = true)
    @NotBlank(message = "版本号" + MUST_NOT_BE_NULL)
    private String version;

}
