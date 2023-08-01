package io.shulie.takin.web.biz.pojo.request.activity;

import io.shulie.takin.web.common.enums.activity.info.FlowTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel("业务活动详情查询对象")
public class ReportActivityInfoQueryRequest {
    @ApiModelProperty("报告ID")
    private Long reportId;

    @ApiModelProperty("业务活动ID")
    private Long activityId;

    @ApiModelProperty("流量类型(BLEND混合、PRESSURE_MEASUREMENT压测、BUSINESS业务)")
    private FlowTypeEnum flowTypeEnum;

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private int timeGap;

    @ApiModelProperty("临时 业务活动标志[true 为临时, false 为正常业务活动]")
    private boolean tempActivity;
}
