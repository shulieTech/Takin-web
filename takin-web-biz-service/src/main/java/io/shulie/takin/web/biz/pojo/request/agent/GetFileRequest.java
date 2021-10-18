package io.shulie.takin.web.biz.pojo.request.agent;

import javax.validation.constraints.NotBlank;

import io.shulie.takin.cloud.common.constants.ValidConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * agent 获取探针包入参
 *
 * @author liuchuan
 * @date 2021/6/7 2:30 下午
 */
@Data
@ApiModel("入参类-获取探针包入参")
public class GetFileRequest implements ValidConstants {

    @ApiModelProperty(value = "应用名称", required = true)
    @NotBlank(message = "应用名称" + MUST_NOT_BE_NULL)
    private String appName;

    @ApiModelProperty(value = "agentId", required = true)
    @NotBlank(message = "agentId" + MUST_NOT_BE_NULL)
    private String agentId;

    @ApiModelProperty("租户 key")
    private String userAppKey;

}
