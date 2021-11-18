package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 应用标签表(ApplicationTagRef)controller 入参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:09:42
 */
@ApiModel("入参类-入参")
@Data
public class ApplicationTagRefRequest {

    @ApiModelProperty("id")
    private Long id;

}
