package io.shulie.takin.web.ext.entity.traffic;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrafficRecorderQueryExt extends PagingDevice {
    @ApiModelProperty(name = "startTime", value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(name = "endTime", value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(name = "appName", value = "应用名")
    private String appName;

    @ApiModelProperty(name = "endTime", value = "接口名")
    private String serviceName;

    @ApiModelProperty(name = "endTime", value = "方法名")
    private String methodName;

    @ApiModelProperty(name = "count", value = "数据条数")
    private Long count;

}
