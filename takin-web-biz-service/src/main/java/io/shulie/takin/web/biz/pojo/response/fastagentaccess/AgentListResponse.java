package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/20 10:31 上午
 */
@Data
public class AgentListResponse {

    @ApiModelProperty("应用名")
    private String projectName;

    @ApiModelProperty("ip")
    private String ipAddress;

    @ApiModelProperty("进程号")
    private String progressId;

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("agent版本")
    private String agentVersion;

    @ApiModelProperty("agent状态")
    private String agentStatus;

    @ApiModelProperty("agent错误信息")
    private String agentErrorMsg;

    @ApiModelProperty("探针版本")
    private String probeVersion;

    @ApiModelProperty("探针状态")
    private String probeStatus;

    @ApiModelProperty("探针错误信息")
    private String probeErrorMsg;

    @ApiModelProperty("心跳时间")
    private String agentUpdateTime;

}
