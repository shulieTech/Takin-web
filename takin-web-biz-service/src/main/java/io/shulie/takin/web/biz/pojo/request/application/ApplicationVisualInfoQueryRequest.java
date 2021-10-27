package io.shulie.takin.web.biz.pojo.request.application;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@ApiModel("应用监控查询对象")
public class ApplicationVisualInfoQueryRequest extends PagingDevice {
    /**
     * 应用名称
     */
    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("流量类型(BLEND混合-1、PRESSURE_MEASUREMENT压测1、BUSINESS业务0)")
    private int clusterTest;

    @ApiModelProperty("开始时间")
    private LocalDateTime startTimeDate = LocalDateTime.now().minusMinutes(5);

    @ApiModelProperty("结束时间")
    private LocalDateTime endTimeDate = LocalDateTime.now();

    @ApiModelProperty("开始时间")
    private String startTime = startTimeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @ApiModelProperty("结束时间")
    private String endTime = endTimeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    /**
     * 服务名称
     * 如果服务名称为空则查询全部服务，否则查询指定服务名称服务
     */
    @ApiModelProperty("服务名称")
    private String serviceName;

    /**
     * 业务活动
     */
    @ApiModelProperty("业务活动名称")
    private String activityName;

    /**
     * 排序字段
     * eg:TPS desc
     */
    @ApiModelProperty("排序字段")
    private String orderBy = "TPS desc";

    /**
     * 关注列表
     */
    @ApiModelProperty("关注列表")
    private List<String> attentionList;

    /**
     * 是否关注
     */
    @ApiModelProperty("是否关注")
    private Boolean attend;
}
