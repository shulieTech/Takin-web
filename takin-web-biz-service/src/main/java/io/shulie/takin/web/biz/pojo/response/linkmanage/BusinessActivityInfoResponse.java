package io.shulie.takin.web.biz.pojo.response.linkmanage;

import lombok.Data;

/**
 * @Author: ει£
 * @Date: 2022/7/11 1:53 δΈε
 */
@Data
public class BusinessActivityInfoResponse {

    private Long activityId;

    private String activityName;

    private String applicationName;

    private Long applicationId;

    private Integer type;

    private String methodName;

    private String serviceName;

    private String entrace;
}
