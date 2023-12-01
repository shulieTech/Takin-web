package io.shulie.takin.web.biz.pojo.request.report;

import lombok.Data;

/**
 * @author zhangz
 * Created on 2023/12/1 17:03
 * Email: zz052831@163.com
 */

@Data
public class ReportRiskDeleteRequest {
    private String chainCode;
    private String tenantCode;
    private String startTime;
    private String endTime;
}
