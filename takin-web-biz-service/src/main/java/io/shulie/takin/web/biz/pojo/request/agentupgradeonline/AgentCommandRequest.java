package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description agent命令请求
 * @Author ocean_wll
 * @Date 2021/11/11 2:29 下午
 */
@Data
@ApiModel("入参类-agent命令请求")
public class AgentCommandRequest {

    @ApiModelProperty(value = "应用名", required = true)
    @NotBlank(message = "应用名不能为空")
    private String projectName;

    @ApiModelProperty(value = "agentId", required = true)
    @NotBlank(message = "agentId不能为空")
    private String agentId;

    @ApiModelProperty(value = "ip地址", required = true)
    @NotBlank(message = "ip地址不能为空")
    private String ipAddress;

    @ApiModelProperty(value = "进程号", required = true)
    @NotBlank(message = "进程号不能为空")
    private String progressId;

    @ApiModelProperty(value = "指令", required = true)
    @NotBlank(message = "指令不能为空")
    private String commandId;

    @ApiModelProperty(value = "指令参数")
    private String commandParam;

}
