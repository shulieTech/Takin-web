package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * agent配置列表查询对象
 *
 * @author ocean_wll
 * @date 2021/8/16 10:41 上午
 */
@Data
@ApiModel("入参-agent配置列表查询对象")
public class AgentConfigQueryRequest {

    /**
     * 应用名
     */
    @ApiModelProperty(value = "应用名")
    private String projectName;

    /**
     * 生效机制0：重启生效，1：立即生效
     *
     * @see io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigEffectMechanismEnum
     */
    @ApiModelProperty(value = "生效机制0：重启生效，1：立即生效")
    private Integer effectMechanism;

    /**
     * 是否生效
     */
    @ApiModelProperty(value = "是否生效，true：生效，false：未生效")
    private Boolean isEffect;

    /**
     * 英文配置key
     */
    @ApiModelProperty(value = "英文配置key")
    private String enKey;

    /**
     * 仅看应用配置
     */
    @ApiModelProperty(value = "仅看应用配置")
    private Boolean readProjectConfig;
}
