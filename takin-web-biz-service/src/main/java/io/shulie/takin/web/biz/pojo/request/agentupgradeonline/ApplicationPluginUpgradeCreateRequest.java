package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginInfo;
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

    @ApiModelProperty(value = "升级清单")
    @NotEmpty
    private List<PluginInfo> upgradeInfo;

    @ApiModelProperty(value = "需要升级的应用信息")
    @NotEmpty
    private List<ApplicationPluginUpgradeCreateAgentInfoRequest> appsInfo;

}
