package io.shulie.takin.web.biz.pojo.output.application;

import io.shulie.takin.web.biz.pojo.response.application.SingleServerConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fanxx
 * @date 2020/12/1 2:07 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShadowServerConfigurationOutput {
    private SingleServerConfiguration dataSourceBusiness;
    private SingleServerConfiguration dataSourceBusinessPerformanceTest;
}
