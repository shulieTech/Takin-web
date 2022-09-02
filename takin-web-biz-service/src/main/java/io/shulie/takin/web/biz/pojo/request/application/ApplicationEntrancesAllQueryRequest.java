package io.shulie.takin.web.biz.pojo.request.application;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@Data
@ApiModel("应用入口服务查询对象")
public class ApplicationEntrancesAllQueryRequest {
    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("服务名称")
    private String serviceName;

}
