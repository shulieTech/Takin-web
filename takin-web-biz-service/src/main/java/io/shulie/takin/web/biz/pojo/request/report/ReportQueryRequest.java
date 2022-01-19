package io.shulie.takin.web.biz.pojo.request.report;

import java.io.Serializable;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-09-20
 */

@Data
@ApiModel(value = "ReportQueryRequest", description = "报告查询节点")
public class ReportQueryRequest implements Serializable {

    @ApiModelProperty(name = "sceneId", value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(name = "reportId", value = "报告ID")
    private Long reportId;
}
