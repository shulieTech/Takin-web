package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description agent配置生效列表
 * @Author ocean_wll
 * @Date 2021/8/19 4:20 下午
 */
@Data
public class AgentConfigEffectListResponse {

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("应用名")
    private String projectName;

    @ApiModelProperty("生效值")
    private String effectVal;

    @ApiModelProperty("是否生效，true生效，false不生效")
    private Boolean isEffect;

    @ApiModelProperty("建议处理方案")
    private String program;
}
