package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: 南风
 * @Date: 2021/11/23 4:44 下午
 */
@Data
public class ApplicationPluginUpgradeCreateAgentInfoRequest {

    @NotNull
    private Long applicationId;
    @NotNull
    private String applicationName;
    @NotNull
    private String agentId;

}
