package io.shulie.takin.web.biz.pojo.response.application;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MiddlewareImportResponse {
    private long total;
    private long success;
    private long fail;
    private String url;
}
