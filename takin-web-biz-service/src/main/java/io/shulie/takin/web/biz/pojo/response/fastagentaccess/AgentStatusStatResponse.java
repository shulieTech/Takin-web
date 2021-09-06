package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 3:25 下午
 */
@Data
public class AgentStatusStatResponse {

    @ApiModelProperty("探针总数")
    private Integer probeCount;

    @ApiModelProperty("探针安装失败数")
    private Integer probeFailCount;

    @ApiModelProperty("agent总数")
    private Integer agentCount;

    @ApiModelProperty("agent安装失败数")
    private Integer agentFailCount;
}
