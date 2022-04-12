package io.shulie.takin.web.biz.service.report;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.report.ApplicationDTO;
import com.pamirs.takin.entity.domain.dto.report.BottleneckInterfaceDTO;
import com.pamirs.takin.entity.domain.dto.report.MachineDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportCountDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportPerformanceCostTrendDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportPerformanceInterfaceDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskApplicationCountDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskMacheineDTO;
import io.shulie.takin.web.biz.pojo.request.report.ReportPerformanceCostTrendRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportPerformanceInterfaceRequest;
import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import org.apache.commons.lang3.tuple.Pair;

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

    Pair<List<ReportPerformanceInterfaceDTO>, Long> listPerformanceInterface(ReportPerformanceInterfaceRequest request);

    ReportPerformanceCostTrendDTO queryCostTrend(ReportPerformanceCostTrendRequest request);
}
