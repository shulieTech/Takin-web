package io.shulie.takin.web.biz.pojo.response.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MiddlewareCompareResponse {
    private long total;
    private long unknown;
    private long notSupported;
    private long supported;
    private long fail;
    private String url;
}
