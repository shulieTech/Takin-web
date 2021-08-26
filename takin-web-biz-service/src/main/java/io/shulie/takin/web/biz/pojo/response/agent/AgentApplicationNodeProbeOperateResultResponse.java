package io.shulie.takin.web.biz.pojo.response.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/7 9:29 上午
 */
@Data
@ApiModel("出参类-agent 应用节点探针操作结果")
public class AgentApplicationNodeProbeOperateResultResponse {

    @ApiModelProperty("业务码, 1 成功, 0 失败, 99 其他错误, 0 重试, 其他不用重试")
    private Integer businessCode;

    @ApiModelProperty("错误的话, 错误信息")
    private String message = "";

}
