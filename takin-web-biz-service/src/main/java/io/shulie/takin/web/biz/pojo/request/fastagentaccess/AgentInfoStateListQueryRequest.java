package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description  agent版本管理(AgentVersion)controller 应用探针状态信息
 * @Author iaoyz
 * @Date 2021/8/12 3:39 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentInfoStateListQueryRequest extends PageBaseDTO {

    private static final long serialVersionUID = 7908819806350087243L;

    @ApiModelProperty("应用名称-支持全模糊")
    private String applicationName;

}
