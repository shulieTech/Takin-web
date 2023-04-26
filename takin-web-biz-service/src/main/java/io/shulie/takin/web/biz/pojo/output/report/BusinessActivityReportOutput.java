package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("业务活动报告信息")
public class BusinessActivityReportOutput implements Serializable {

    @ApiModelProperty("业务活动ID")
    private Long businessActivityId;

    @ApiModelProperty("业务活动名称")
    private String businessActivityName;

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("请求方式")
    private String requestMethod;

    @ApiModelProperty("xpath的MD5值")
    private String xpathMd5;

}
