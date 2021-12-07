package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 应用标签表(ApplicationTagRef)controller 详情响应类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:09:26
 */
@ApiModel("出参类-详情出参")
@Data
public class ApplicationTagRefDetailResponse {

    @ApiModelProperty("id")
    private Long id;

}
