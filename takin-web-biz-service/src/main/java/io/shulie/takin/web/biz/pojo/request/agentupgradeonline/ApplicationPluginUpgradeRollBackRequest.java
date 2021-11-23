package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: 南风
 * @Date: 2021/11/23 3:56 下午
 */
@Data
public class ApplicationPluginUpgradeRollBackRequest {

    @ApiModelProperty(value = "回滚批次号")
    @NotNull
    private String upgradeBatch;

}
