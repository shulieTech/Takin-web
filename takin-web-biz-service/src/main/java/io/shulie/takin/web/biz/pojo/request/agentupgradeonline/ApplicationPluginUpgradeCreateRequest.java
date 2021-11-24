package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/11/23 3:56 下午
 */
@Data
public class ApplicationPluginUpgradeCreateRequest {


    @ApiModelProperty(value = "升级清单",required = true)
    @NotEmpty
    private List<ApplicationPluginUpgradeCreatePluginInfoRequest> upgradeInfo;

    @ApiModelProperty(value = "需要升级的应用信息",required = true)
    @NotEmpty
    private List<ApplicationPluginUpgradeCreateAgentInfoRequest> appsInfo;

}
