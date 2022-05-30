package io.shulie.takin.adapter.api.model.response.cloud.resources;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class Resource {
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "压力机数")
    private int resourcesAmount;
    @ApiModelProperty(value = "初始化压力机数")
    private int initializedAmount = 0;
    @ApiModelProperty(value = "运行中压力机数")
    private int aliveAmount;//压测运行中的数量
    @ApiModelProperty(value = "异常压力机数")
    private int unusualAmount;
    @ApiModelProperty(value = "停止压力机数")
    private int inactiveAmount;
    @ApiModelProperty(value = "压力机数据")
    private List<CloudResource> resources;
    @ApiModelProperty(value = "异常信息")
    private String errorMessage;
    @ApiModelProperty(value = "任务Id")
    private Integer taskId;
    @ApiModelProperty(value = "资源Id")
    private String resourceId;
    @ApiModelProperty(value = "当前页")
    private int currentPage;
    @ApiModelProperty(value = "页数")
    private int pageSize;
}
