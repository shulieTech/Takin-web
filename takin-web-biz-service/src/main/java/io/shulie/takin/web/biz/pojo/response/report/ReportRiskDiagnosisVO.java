package io.shulie.takin.web.biz.pojo.response.report;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangz
 * Created on 2023/12/1 14:58
 * Email: zz052831@163.com
 */

@Data
public class ReportRiskDiagnosisVO {
    private String id;
    private RiskStausEnum statusEnum;
    private Integer sendMqResult;
    private String desc;
    private String chainCode;
    private Date startTime;
    private Date endTime;
    private String tenantCode;
    private Date gmtCreate;
    private Date gmtUpdate;
    private Double currentAvgRt;
    private Double targetRt;
}
