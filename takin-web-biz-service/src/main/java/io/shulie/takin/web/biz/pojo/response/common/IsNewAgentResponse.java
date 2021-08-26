package io.shulie.takin.web.biz.pojo.response.common;

import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/17 5:56 下午
 */
@Data
@ApiModel("出参类-应用agent是否是新版本")
public class IsNewAgentResponse {

    @ApiModelProperty("是否是新的agent, 1 是, 0 否")
    private Integer isNew;

    @ApiModelProperty("agent 版本列表")
    private Set<String> agentVersions;

}
