package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.common.vo.script.RequestAssertDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/11 1:54 下午
 */
@Data
@ApiModel("出参类-脚本调试的请求流量明细列表出参")
public class ScriptDebugRequestListResponse {

    @ApiModelProperty("traceId")
    private String traceId;

    @ApiModelProperty("入口")
    private String entry;

    @ApiModelProperty("请求体")
    private String requestBody;

    @ApiModelProperty("响应体/异常")
    private String responseBody;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("请求时间")
    private LocalDateTime requestAt;

    @ApiModelProperty("结果")
    private Integer responseStatus;

    @ApiModelProperty("结果文字描述")
    private String responseStatusDesc;

    @ApiModelProperty("断言详情列表")
    private List<RequestAssertDetailVO> assertDetailList = Collections.emptyList();

    @ApiModelProperty("总耗时")
    private Long cost;

}
