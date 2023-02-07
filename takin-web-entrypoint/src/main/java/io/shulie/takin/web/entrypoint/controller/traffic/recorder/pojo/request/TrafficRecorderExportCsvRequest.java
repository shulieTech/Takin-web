package io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.request;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author chenxingxing
 * @date 2023/2/7 1:47 下午
 */
@Data
@ApiModel(value = "TrafficRecorderResponse", description = "任务配置入参")
public class TrafficRecorderExportCsvRequest extends PagingDevice {

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

    @ApiModelProperty("总大小")
    private Integer size;
}
