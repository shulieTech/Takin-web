package io.shulie.takin.web.data.param.application;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-09-20
 */
@Data
public class ApplicationQueryParam {
    private Long tenantId;
    private Long userId;
    private String envCode;
}
