package io.shulie.takin.web.biz.service.report;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.report.*;
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

    PageInfo<ReportMockDTO> listReportMock(ReportLocalQueryParam queryParam);

    Long getTraceFailedCount(Long reportId);
}
