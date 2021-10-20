package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description agent状态统计对象
 * @Author ocean_wll
 * @Date 2021/8/19 3:43 下午
 */
@Data
public class AgentConfigStatusCountResponse {

    @ApiModelProperty("配置总数")
    private Integer configCount;

    @ApiModelProperty("生效总数")
    private Integer effectiveCount;

    @ApiModelProperty("未生效")
    private Integer invalidCount;
}
