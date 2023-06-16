package io.shulie.takin.web.biz.service.report;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.report.ApplicationDTO;
import com.pamirs.takin.entity.domain.dto.report.BottleneckInterfaceDTO;
import com.pamirs.takin.entity.domain.dto.report.MachineDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportCountDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskApplicationCountDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskMacheineDTO;
import io.shulie.takin.web.biz.pojo.input.report.NodeCompareTargetInput;
import io.shulie.takin.web.biz.pojo.output.report.*;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;

/**
 * @author qianshui
 * @date 2020/7/27 下午8:25
 */
public interface ReportLocalService {

    ReportCountDTO getReportCount(Long reportId);

    PageInfo<BottleneckInterfaceDTO> listBottleneckInterface(ReportLocalQueryParam queryParam);

    RiskApplicationCountDTO listRiskApplication(Long reportId);

    PageInfo<RiskMacheineDTO> listRiskMachine(ReportLocalQueryParam queryParam);

    MachineDetailDTO getMachineDetail(Long reportId, String applicationName, String machineIp);

    List<ApplicationDTO> listApplication(Long reportId, String applicationName);

    PageInfo<MachineDetailDTO> listMachineDetail(ReportLocalQueryParam queryParam);

    Long getTraceFailedCount(Long reportId);

    ReportCompareOutput getReportCompare(List<Long> reportIds, Long businessActivityId);

    void cacheLTReportData2Redis(Long reportId);

    /**
     * 获取节点对比目标
     *
     * @param nodeCompareTargetInput
     * @return
     */
    Response<NodeCompareTargetOut> getLtNodeCompare(NodeCompareTargetInput nodeCompareTargetInput);

    /**
     * 获取报告应用性能列表
     *
     * @return
     */
    Response<List<ReportAppPerformanceOut>> getReortAppPerformanceList(Long reportId);


    /**
     * 获取报告应用实例性能列表
     *
     * @return
     */
    Response<List<ReportAppInstancePerformanceOut>> getReortAppInstancePerformanceList(Long reportId);


    /**
     * 获取报告应用性能趋势图
     *
     * @return
     */
    Response<List<ReportAppMapOut>> getReportAppTrendMap(Long reportId);

    List<ReportAppMapOut> getReportAppTrendMapToReportApplication(Long reportId);

    /**
     * 获取报告应用性能趋势图
     *
     * @return
     */
    Response<List<MachineDetailDTO>> getReportAppInstanceTrendMap(Long reportId);

}
