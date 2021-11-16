package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigCreateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/11/15 10:32 上午
 */
@ApiModel("入参类-发布新探针")
@Data
public class AgentLibraryCreateRequest {

    @ApiModelProperty("文件路径")
    @NotBlank(message = "上传文件不能为空")
    private String filePath;

    @ApiModelProperty("插件类型,0:中间件包，1:simulator包，2:agent包")
    @NotNull(message = "插件类型不能为空")
    private Integer pluginType;

    @Valid
    @ApiModelProperty("插件信息")
    @Size(min = 1, message = "插件信息不能为空")
    private List<PluginCreateRequest> pluginList;

    @Valid
    @ApiModelProperty("agent配置信息")
    private List<AgentConfigCreateRequest> configList;
}
