package io.shulie.takin.web.biz.pojo.request.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xjz@io.shulie
 * @date 2023/2/15
 * @desc 查询实况,报告链路图请求
 */
@Data
public class ReportLinkDiagramReq2 implements Serializable {

    @ApiModelProperty("业务活动ID")
    private Long activityId;
    
}
