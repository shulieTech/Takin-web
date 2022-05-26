package com.pamirs.takin.cloud.entity.domain.vo.report;

import lombok.Data;

/**
 * @author 何仲奇
 * @date 2020/9/30 5:59 下午
 */
@Data
public class ReportFeature {
    private Long reportId;
    private Integer state;
    private String key;
    private String value;

    public ReportFeature(Long reportId, Integer state, String key, String value) {
        this.state = state;
        this.reportId = reportId;
        this.key = key;
        this.value = value;
    }
}
