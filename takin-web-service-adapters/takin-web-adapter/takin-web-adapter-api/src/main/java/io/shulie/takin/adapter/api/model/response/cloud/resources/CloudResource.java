package io.shulie.takin.adapter.api.model.response.cloud.resources;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudResource {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "重启次数")
    private Integer restart;
    @ApiModelProperty(value = "启动时间")
    private String startTime;
    @ApiModelProperty(value = "停止时间")
    private String stopTime;
    @ApiModelProperty(value = "压力机ip")
    private String ip;
    @ApiModelProperty(value = "宿主机ip")
    private String hostIp;
    @ApiModelProperty(value = "状态时间")
    private String statusTime;
    @ApiModelProperty(value = "异常信息")
    private String statusMessage;
}
