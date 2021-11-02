package io.shulie.takin.web.biz.pojo.request.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 根据条件查询业务活动
 * @author zhaoyong
 */
@Data
public class ActivityResultQueryRequest implements Serializable {

    @ApiModelProperty("应用名")
    private String applicationName;

    @ApiModelProperty("请求方式：GET,POST")
    private String method;

    @ApiModelProperty("远程调用类型")
    private String rpcType;

    @ApiModelProperty("请求路径")
    private String serviceName;
}
