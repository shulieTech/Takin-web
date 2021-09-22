package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import javax.validation.constraints.NotBlank;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 4:27 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参-配置生效列表查询对象")
public class AgentConfigEffectQueryRequest extends PageBaseDTO {

    private static final long serialVersionUID = -2441731235616132272L;

    @ApiModelProperty(value = "配置key", required = true)
    @NotBlank(message = "配置key不能为空")
    private String enKey;

    @ApiModelProperty(value = "应用名")
    private String projectName;

    @ApiModelProperty(value = "是否生效，true为生效，false不生效")
    private Boolean isEffect;

}
