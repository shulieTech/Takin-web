package io.shulie.takin.web.data.result.risk;

import lombok.Data;

/**
 * @author xingchen
 * @ClassName: BaseRiskResult
 * @Package: io.shulie.takin.report.vo
 * @date 2020/7/2717:13
 */
@Data
public class BaseRiskResult {
    private Long reportId;

    private String appName;

    private String appIp;

    private String content;
}
