package io.shulie.takin.web.biz.pojo.output.report;

import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import lombok.Data;

/**
 * @Author: 南风
 * @Date: 2022/5/9 9:50 上午
 */
@Data
public class ReportDownLoadOutput {



    private ReportDetailOutput detailOutput;

    private NodeTreeSummaryResp nodeTree;

    public ReportDownLoadOutput(ReportDetailOutput detailOutput, NodeTreeSummaryResp nodeTree) {
        this.detailOutput = detailOutput;
        this.nodeTree = nodeTree;
    }

    public ReportDownLoadOutput() {
    }
}
