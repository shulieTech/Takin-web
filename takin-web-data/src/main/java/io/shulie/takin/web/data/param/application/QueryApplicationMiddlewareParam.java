package io.shulie.takin.web.data.param.application;

import lombok.Data;

/**
 * 应用中间件(ApplicationMiddleware)查询入参类
 *
 * @author liuchuan
 * @date 2021-06-30 16:09:38
 */
@Data
public class QueryApplicationMiddlewareParam {

    private Long applicationId;
    private Long tenantId;
    private String envCode;

}
