package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.common.vo.script.RequestAssertDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 压测报告；统计返回
 *
 * @author qianshui
 * @date 2020/7/22 下午2:19
 */
@ApiModel
@Data
public class ReportTraceDTO implements Serializable {

    private static final long serialVersionUID = 8928035842416997931L;

    @ApiModelProperty("请求体")
    public String requestBody;

    @ApiModelProperty("响应体")
    public String responseBody;

    @ApiModelProperty("入口名称")
    private String interfaceName;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("状态")
    private Boolean succeeded;

    @ApiModelProperty("总耗时")
    private Long totalRt;

    @ApiModelProperty("开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("traceId")
    private String traceId;

    @ApiModelProperty("结果")
    private Integer responseStatus;

    @ApiModelProperty("结果文字描述")
    private String responseStatusDesc;

    @ApiModelProperty("断言详情列表")
    private List<RequestAssertDetailVO> assertDetailList = Collections.emptyList();

}
