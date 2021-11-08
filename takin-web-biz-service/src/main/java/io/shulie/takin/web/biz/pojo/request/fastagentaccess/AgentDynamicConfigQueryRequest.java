package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * agent端：动态参数查询对象
 *
 * @author ocean_wll
 * @date 2021/8/16 3:39 下午
 */
@Data
public class AgentDynamicConfigQueryRequest {

    /**
     * 应用名
     */
    @ApiModelProperty(value = "应用名")
    @NotBlank(message = "projectName不允许为空")
    private String projectName;

    /**
     * agent版本
     */
    @ApiModelProperty(value = "agent版本")
    @NotBlank(message = "agent版本不允许为空")
    private String version;

    /**
     * 生效机制0：重启生效，1：立即生效
     *
     * @see io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigEffectMechanismEnum
     */
    @ApiModelProperty("生效机制0：重启生效，1：立即生效")
    private Integer effectMechanism;
}
