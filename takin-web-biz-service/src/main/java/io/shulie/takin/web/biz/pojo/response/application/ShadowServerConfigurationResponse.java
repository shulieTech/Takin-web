package io.shulie.takin.web.biz.pojo.response.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/1 2:07 下午
 */
@Data
public class ShadowServerConfigurationResponse {
    private SingleServerConfiguration dataSourceBusiness;
    private SingleServerConfiguration dataSourceBusinessPerformanceTest;
}
