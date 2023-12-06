package io.shulie.takin.web.biz.pojo.input.sresla;

import lombok.Data;

import java.util.Date;

/**
 * @author zhaoyong
 */
@Data
public class CollectorSlaRequest {

    private String startDate;

    private String endDate;

    private String appName;

    private String rpc;

    private Long refId;

    private String tenantCode;
}
