package io.shulie.takin.web.biz.pojo.output.report;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.report.BottleneckInterfaceDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskApplicationCountDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskMacheineDTO;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import lombok.Data;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2022/5/9 9:50 上午
 */
@Data
public class ReportDownLoadOutput {



    private ReportDetailOutput detailOutput;

    private NodeTreeSummaryResp nodeTree;

    private List<BottleneckInterfaceDTO> info;

    private List<RiskMacheineDTO> riskApplicationCountDTO;

    private List<WarnDetailResponse> responses;

    public ReportDownLoadOutput(ReportDetailOutput detailOutput, NodeTreeSummaryResp nodeTree) {
        this.detailOutput = detailOutput;
        this.nodeTree = nodeTree;
    }

    public ReportDownLoadOutput() {
    }
}
