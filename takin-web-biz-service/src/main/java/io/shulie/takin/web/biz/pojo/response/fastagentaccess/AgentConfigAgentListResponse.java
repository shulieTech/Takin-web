package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description agent端：配置列表展示对象
 * @Author ocean_wll
 * @Date 2021/8/17 11:28 上午
 */
public class AgentConfigAgentListResponse {

    /**
     * 英文配置key
     */
    @ApiModelProperty("英文配置key")
    private String enKey;


    /**
     * 配置默认值
     */
    @ApiModelProperty("配置值")
    private String defaultValue;

    /**
     * 生效机制0：重启生效，1：立即生效
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigEffectMechanismEnum
     */
    @ApiModelProperty("生效机制0：重启生效，1：立即生效")
    private Integer effectMechanism;
}
