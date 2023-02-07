package io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenxingxing
 * @date 2023/2/3 3:56 下午
 */
@Data
@ApiModel(value = "TrafficRecorderResponse", description = "流量录制出参")
public class TrafficRecorderResponse {

    @ApiModelProperty("业务主键")
    private Long id;

    @ApiModelProperty("租户编码")
    private String tenantCode;

    @ApiModelProperty("应用名编码")
    private String appName;

    @ApiModelProperty("请求服务名")
    private String requestServiceName;

    @ApiModelProperty("请求服务地址")
    private String requestAddress;

    @ApiModelProperty("机房：亦庄、西咸")
    private String machineRoom;

    @ApiModelProperty("请求服务名")
    private Integer serviceType;

    @ApiModelProperty("请求服务地址")
    private Integer kind;

    @ApiModelProperty("请求头")
    private Integer requestHeader;

    @ApiModelProperty("请求体")
    private String requestBody;

    @ApiModelProperty("响应头")
    private String responseHeader;

    @ApiModelProperty("响应体")
    private String responseBody;

    @ApiModelProperty("清洗请求服务名")
    private String cleanRequestServiceName;

    @ApiModelProperty("清洗请求地址")
    private String cleanRequestAddress;

    @ApiModelProperty("清洗后的请求头")
    private String cleanRequestHeader;

    @ApiModelProperty("清洗后的body体")
    private String cleanRequestBody;

    @ApiModelProperty("清洗后的响应头")
    private String cleanResponseHeader;

    @ApiModelProperty("清洗后的响应体")
    private String cleanResponseBody;

    @ApiModelProperty("记录时间")
    private String recordTime;

    @ApiModelProperty("创建时间")
    private String gmtCreate;

}
