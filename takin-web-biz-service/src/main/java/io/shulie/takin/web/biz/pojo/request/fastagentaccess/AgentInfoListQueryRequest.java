package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description  agent版本管理(AgentVersion)controller 应用探针基础信息
 * @Author iaoyz
 * @Date 2021/8/12 3:39 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentInfoListQueryRequest extends PageBaseDTO {

    private static final long serialVersionUID = 7908819806350087243L;

    @ApiModelProperty("应用名称-支持全模糊")
    private String applicationName;

    @ApiModelProperty("标签id")
    private Long tagId;

    @ApiModelProperty("插件id")
    private Long pluginId;

    @ApiModelProperty("应用接入状态 1:正常 2:异常 0:升级中")
    private Integer accessStatus;

    @ApiModelProperty("业务活动id")
    private Long businessActivityId;

}
