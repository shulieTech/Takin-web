package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:43 下午
 */
@Data
public class PerformanceConfigQueryRequest extends PageBaseDTO {
    // 名称
    private String queryName;

    // 配置Id
    private Long id;

    // 请求URL
    private String requestUrl;

    // 请求方式
    private String httpMethod;
}
