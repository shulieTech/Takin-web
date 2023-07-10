package io.shulie.takin.web.biz.service.risk;

import com.pamirs.takin.entity.domain.risk.ReportLinkDetail;
import io.shulie.takin.web.data.result.risk.BaseRiskResult;

import java.util.List;

/**
 * @author xingchen
 * @Package: io.shulie.takin.report.service
 * @date 2020/7/2717:03
 */
public interface ProblemAnalysisService {
    /**
     * 同步机器数据到表
     */
    void syncMachineData(Long reportId,Long endTime);

    /**
     * 检查风险机器，并保存
     */
    void checkRisk(Long reportId);

    /**
     * 通过压测报告ID,获取所有的风险列表
     *
     * @return
     */
    List<BaseRiskResult> processRisk(Long reportId);

    /**
     * 通过压测报告ID,获取瓶颈列表
     *
     * @return
     */
    void processBottleneck(Long reportId);

    /**
     * 根据业务活动ID、时间获取压测链路明细信息
     *
     * @param startTime 毫秒数
     * @return
     */
    ReportLinkDetail queryLinkDetail(Long businessActivityId, Long startTime, Long endTime);
}
