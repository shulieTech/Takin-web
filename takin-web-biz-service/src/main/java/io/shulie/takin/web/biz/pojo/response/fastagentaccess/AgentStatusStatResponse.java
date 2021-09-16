package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 3:25 下午
 */
public class AgentStatusStatResponse {

    @ApiModelProperty("探针总数")
    private Integer probeCount;

    @ApiModelProperty("探针安装失败数")
    private Integer probeFailCount;

    @ApiModelProperty("agent总数")
    private Integer agentCount;

    @ApiModelProperty("agent安装失败数")
    private Integer agentFailCount;

    public Integer getProbeCount() {
        return probeCount == null ? 0 : probeCount;
    }

    public Integer getProbeFailCount() {
        return probeFailCount == null ? 0 : probeFailCount;
    }

    public Integer getAgentCount() {
        return agentCount == null ? 0 : agentCount;
    }

    public Integer getAgentFailCount() {
        return agentFailCount == null ? 0 : agentFailCount;
    }

    public void setProbeCount(Integer probeCount) {
        this.probeCount = probeCount;
    }

    public void setProbeFailCount(Integer probeFailCount) {
        this.probeFailCount = probeFailCount;
    }

    public void setAgentCount(Integer agentCount) {
        this.agentCount = agentCount;
    }

    public void setAgentFailCount(Integer agentFailCount) {
        this.agentFailCount = agentFailCount;
    }
}
