package io.shulie.takin.web.biz.pojo.request.report;

import lombok.Data;

import java.util.List;

/**
 * @author zhangz
 * Created on 2023/12/1 16:50
 * Email: zz052831@163.com
 */

@Data
public class ReportRiskRequest {
    private List<String> chainCodeList;
    private String tenantCode;
    private String startTime;
    private String endTime;
}
