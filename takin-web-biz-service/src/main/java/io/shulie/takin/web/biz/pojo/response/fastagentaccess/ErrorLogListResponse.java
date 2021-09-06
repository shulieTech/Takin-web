package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 2:27 下午
 */
@Data
public class ErrorLogListResponse {

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("应用名")
    private String projectName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("发生时间")
    private Date agentTimestamp;

    @ApiModelProperty("异常日志")
    private String agentInfo;
}
