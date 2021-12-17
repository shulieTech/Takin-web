package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 探针心跳数据(AgentReport)controller 入参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:32:59
 */
@ApiModel("入参类-入参")
@Data
public class AgentReportRequest {

    @ApiModelProperty("id")
    private Long id;

}
