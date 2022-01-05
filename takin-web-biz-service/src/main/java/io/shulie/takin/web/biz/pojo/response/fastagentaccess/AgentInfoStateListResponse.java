package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * agent版本管理(AgentVersion)controller 应用探针状态信息
 *
 * @author 南风
 * @date 2021-11-15 10:43:22
 */
@ApiModel("出参类-基础信息出参")
@Data
public class AgentInfoStateListResponse {

    @ApiModelProperty("应用id")
    private Long applicationId;

    @ApiModelProperty(name = "accessStatus", value = "接入状态； 0：正常 ； 1；异常 ；2：升级中")
    private Integer accessStatus;

    @ApiModelProperty(name = "nodeNum", value = "总节点数量")
    private Integer nodeNum;

    @ApiModelProperty(name = "errorNum", value = "异常节点数量")
    private Integer errorNum;

    @ApiModelProperty(name = "waitRestartNum", value = "待重启节点数量")
    private Integer waitRestartNum;

    @ApiModelProperty(name = "waitRestartNum", value = "休眠节点数量")
    private Integer sleepNum;

    @ApiModelProperty(name = "runningNum", value = "运行中节点数量")
    private Integer runningNum;

    @ApiModelProperty(name = "version", value = "探针主版本")
    private String version;

}
