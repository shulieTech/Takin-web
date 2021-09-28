package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * agent配置管理(AgentConfig)controller 入参类
 *
 * @author ocean_wll
 * @date 2021-08-12 18:54:53
 */
@ApiModel("入参类-agent配置修改类")
@Data
public class AgentConfigUpdateRequest {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id", required = true)
    @NotNull(message = "主键id不能为空")
    private Long id;

    /**
     * 配置默认值
     */
    @ApiModelProperty(value = "默认值", required = true)
    @NotBlank(message = "默认值不能为空")
    private String defaultValue;

    /**
     * 应用名称（应用配置时才生效）
     */
    @ApiModelProperty("应用名称：将全局配置改成应用配置时需要传，其他单纯修改全局配置或应用配置时不需要传")
    private String projectName;

}
