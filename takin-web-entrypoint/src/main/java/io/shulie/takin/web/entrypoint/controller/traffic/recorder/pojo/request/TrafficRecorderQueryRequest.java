package io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.request;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.plugin.app.pojo.request.trace
 * @ClassName: TraceLogQueryRequest
 * @Description: TODO
 * @Date: 2021/10/25 17:13
 */
@Data
@ApiModel(value = "TrafficRecorderResponse", description = "流量录制入参")
public class TrafficRecorderQueryRequest extends PagingDevice {

    @ApiModelProperty("应用名")
    private String appName;

    @ApiModelProperty("请求服务名称")
    private String ServiceName;

    @ApiModelProperty("中间件类型")
    private Integer serviceType;

    @ApiModelProperty("请求类型")
    private Integer kind;

    @ApiModelProperty("租户标识")
    private String tenantCode;


}
