package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: 南风
 * @Date: 2021/11/23 4:44 下午
 */
@Data
public class ApplicationPluginUpgradeCreateAgentInfoRequest {

    @NotNull
    @ApiModelProperty(value = "应用Id",required = true)
    private Long applicationId;
    @NotNull
    @ApiModelProperty(value = "应用名称",required = true)
    private String applicationName;
    @NotNull
    @ApiModelProperty(value = "agentId",required = true)
    private String agentId;

}
