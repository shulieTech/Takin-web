package io.shulie.takin.web.biz.pojo.output.application;

import lombok.Data;

@Data
public class ApplicationHbaseDetailOutput extends ApplicationDsDetailOutput {

    /**
     * hbase 业务节点内容
     */
    private String dataSourceBusinessQuorum;

    private String dataSourceBusinessPort;

    private String dataSourceBusinessZNode;

    private String dataSourceBusinessParams;

    /**
     * hbase 影子节点内容
     */

    private String dataSourcePerformanceTestQuorum;

    private String dataSourcePerformanceTestPort;

    private String dataSourcePerformanceTestZNode;

    private String dataSourcePerformanceTestParams;
}
