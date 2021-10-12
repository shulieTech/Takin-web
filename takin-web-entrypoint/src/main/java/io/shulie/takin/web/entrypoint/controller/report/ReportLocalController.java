package io.shulie.takin.web.entrypoint.controller.report;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.dto.report.ApplicationDTO;
import com.pamirs.takin.entity.domain.dto.report.BottleneckInterfaceDTO;
import com.pamirs.takin.entity.domain.dto.report.MachineDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportCountDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportPradarLinkDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskApplicationCountDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskMacheineDTO;
import com.pamirs.takin.entity.domain.risk.ReportLinkDetail;
import com.pamirs.takin.entity.domain.vo.report.ReportLocalQueryParam;
import io.shulie.takin.web.biz.service.report.ReportLocalService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.risk.ProblemAnalysisService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.domain.WebResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本地压测报告数据
 *
 * @author qianshui
 * @date 2020/7/27 下午5:59
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "场景报告模块", value = "场景报告")
public class ReportLocalController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportLocalService reportLocalService;

    @Autowired
    private ProblemAnalysisService problemAnalysisService;

    @GetMapping("/report/count")
    @ApiOperation("报告汇总数据")
    public Response<ReportCountDTO> getReportCount(Long reportId) {
        return Response.success(reportLocalService.getReportCount(reportId));
    }

    @GetMapping("/report/bottleneckInterface/list")
    @ApiOperation("瓶颈接口")
    public Response<List<BottleneckInterfaceDTO>> getBottleneckInterfaceList(Long reportId, Integer current,
        Integer pageSize) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        initPageParam(queryParam, current, pageSize);
        return Response.success(reportLocalService.listBottleneckInterface(queryParam));
    }

    @GetMapping("/report/risk/application")
    @ApiOperation("风险机器左侧应用")
    public Response<RiskApplicationCountDTO> getRiskApplicationCount(Long reportId) {
        return Response.success(reportLocalService.listRiskApplication(reportId));
    }

    @GetMapping("/report/risk/machine/list")
    @ApiOperation("风险机器右侧列表")
    public Response<List<RiskMacheineDTO>> getRiskMacheine(Long reportId, String applicationName, Integer current,
        Integer pageSize) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        queryParam.setApplicationName(applicationName);
        initPageParam(queryParam, current, pageSize);
        return Response.success(reportLocalService.listRiskMachine(queryParam));
    }

    @GetMapping("/report/businessActivity/summary/list")
    @ApiOperation("压测明细")
    public WebResponse getBusinessActivitySummaryList(Long reportId) {
        return reportService.querySummaryList(reportId);
    }

    @GetMapping("/report/machine/detail")
    @ApiOperation("性能详情")
    public Response<MachineDetailDTO> getMachineDetail(Long reportId, String applicationName, String machineIp) {
        return Response.success(reportLocalService.getMachineDetail(reportId, applicationName, machineIp));
    }

    @GetMapping("/report/application/list")
    @ApiOperation("容量水位应用列表")
    public Response<List<ApplicationDTO>> getApplicationList(Long reportId, String applicationName) {
        if (StringUtils.isBlank(applicationName)) {
            applicationName = null;
        }
        return Response.success(reportLocalService.listApplication(reportId, applicationName));
    }

    @GetMapping("/report/application/trace/failedCount")
    @ApiOperation("请求流量明细失败次数")
    public Response<Long> getTraceFailedCount(Long reportId) {
        if (reportId == null) {
            return Response.fail("报告id为空");
        }
        return Response.success(reportLocalService.getTraceFailedCount(reportId));
    }

    @GetMapping("/report/machine/list")
    @ApiOperation("容量水位应用机器列表")
    public Response<List<MachineDetailDTO>> getMachineList(Long reportId, String applicationName, Integer current,
        Integer pageSize) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        initPageParam(queryParam, current, pageSize);
        queryParam.setReportId(reportId);
        queryParam.setApplicationName(applicationName);
        return Response.success(reportLocalService.listMachineDetail(queryParam));
    }

    @GetMapping("/report/link/list")
    @ApiOperation("链路明细")
    public Response getReportLinkDTOList(Long reportId, Long businessActivityId) {
        ReportPradarLinkDTO pradarLink = new ReportPradarLinkDTO();
        List<ReportLinkDetail> lists = Lists.newArrayList();
        if (reportId != null && reportId == -1) {
            // initReportLink(lists);
            return Response.success(Lists.newArrayList());
        } else {
            WebResponse<HashMap> response = reportService.getReportByReportId(reportId);
            if (response == null || response.getData() == null) {
                return Response.fail("500", "Not Found Report, id=" + reportId);
            }
            ReportDetailDTO reportDetail = JSON.parseObject(JSON.toJSONString(response.getData()),
                ReportDetailDTO.class);
            long startTime = System.currentTimeMillis();
            long endTime = startTime;
            if (reportDetail.getStartTime() != null) {
                startTime = DateUtil.parseSecondFormatter(reportDetail.getStartTime()).getTime();
            }
            if (reportDetail.getEndTime() != null) {
                endTime = reportDetail.getEndTime().getTime();
            }
            ReportLinkDetail detail = problemAnalysisService.queryLinkDetail(businessActivityId, startTime, endTime);
            if (detail != null) {
                lists.add(detail);
            }
        }
        calcOffset(lists, null);
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        queryParam.setCurrentPage(0);
        queryParam.setPageSize(200);
        Set<String> bottleneckSet = Sets.newSet();
        PageInfo<BottleneckInterfaceDTO> pageInfo = reportLocalService.listBottleneckInterface(queryParam);
        if (pageInfo != null && CollectionUtils.isNotEmpty(pageInfo.getList())) {
            bottleneckSet.addAll(
                pageInfo.getList().stream().map(BottleneckInterfaceDTO::getInterfaceName).collect(Collectors.toSet()));
        }
        fillBottleneckFlag(lists, bottleneckSet);

        pradarLink.setDetails(lists);
        if (CollectionUtils.isNotEmpty(lists)) {
            BigDecimal totalRt = lists.stream().map(data -> BigDecimal.valueOf(data.getAvgRt())).reduce(BigDecimal.ZERO,
                BigDecimal::add);
            pradarLink.setTotalRT(totalRt.intValue());
        }
        return Response.success(pradarLink);
    }

    private void initPageParam(ReportLocalQueryParam queryParam, int current, int pageSize) {
        if (current < 0) {
            current = 0;
        }
        if (pageSize < 0) {
            pageSize = 20;
        }
        queryParam.setCurrent(current);
        queryParam.setPageSize(pageSize);
    }

    /**
     * 计算偏移量
     *
     * @param lists
     */
    private void calcOffset(List<ReportLinkDetail> lists, Integer parentOffset) {
        if (CollectionUtils.isEmpty(lists)) {
            return;
        }
        if (parentOffset == null) {
            parentOffset = 0;
        }
        for (int i = 0; i < lists.size(); i++) {
            ReportLinkDetail temp = lists.get(i);
            if (i == 0) {
                temp.setOffset(parentOffset);
            } else {
                ReportLinkDetail preTemp = lists.get(i - 1);
                temp.setOffset(preTemp.getOffset() + preTemp.getAvgRt().intValue());
            }
            calcOffset(temp.getChildren(), temp.getOffset());
        }
    }

    private void fillBottleneckFlag(List<ReportLinkDetail> lists, Set<String> bottleneckSet) {
        if (CollectionUtils.isEmpty(lists)) {
            return;
        }
        for (ReportLinkDetail dto : lists) {
            //默认非瓶颈接口
            dto.setBottleneckFlag(0);
            if (CollectionUtils.isEmpty(bottleneckSet)) {
                continue;
            }
            bottleneckSet.forEach(bottleneck -> {
                if (StringUtils.isBlank(bottleneck)) {
                    return;
                }
                if (StringUtils.isNotBlank(dto.getServiceName())) {
                    if (dto.getServiceName().endsWith(bottleneck)) {
                        dto.setBottleneckFlag(1);
                    }
                }
            });
            fillBottleneckFlag(dto.getChildren(), bottleneckSet);
        }
    }

}
