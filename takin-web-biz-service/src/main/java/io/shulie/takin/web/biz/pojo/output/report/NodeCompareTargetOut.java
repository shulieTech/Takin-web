package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("节点比对")
public class NodeCompareTargetOut implements Serializable {

    private String requestMethod;

    private String serviceName;

    private String appName;

    private BigDecimal report1Rt;

    private BigDecimal report2Rt;

    private BigDecimal report3Rt;

    private BigDecimal report4Rt;

}
