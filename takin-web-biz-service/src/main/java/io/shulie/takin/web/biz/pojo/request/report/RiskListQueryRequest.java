package io.shulie.takin.web.biz.pojo.request.report;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zhangz
 * Created on 2023/11/29 17:01
 * Email: zz052831@163.com
 */

@Data
@ApiModel(value = "RiskListQueryRequest", description = "报告风险列表")
public class RiskListQueryRequest {
    private List<Long> taskIds;
    private String appName;
    private String serviceName;
    private String riskName;
    private String tenantCode;
    private String startTime;
    private String endTime;
    private Integer page;
    private Integer size;
}
