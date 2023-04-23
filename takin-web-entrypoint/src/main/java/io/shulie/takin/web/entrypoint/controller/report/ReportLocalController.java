package io.shulie.takin.web.entrypoint.controller.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.dto.report.*;
import com.pamirs.takin.entity.domain.risk.ReportLinkDetail;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.report.NodeCompareTargetInput;
import io.shulie.takin.web.biz.pojo.output.report.NodeCompareTargetOut;
import io.shulie.takin.web.biz.pojo.output.report.ReportCompareOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.service.report.ReportLocalService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.risk.ProblemAnalysisService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 本地压测报告数据
 *
 * @author qianshui
 * @date 2020/7/27 下午5:59
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "场景报告模块", value = "场景报告")
@Slf4j
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

    @GetMapping("vlt/report/bottleneckInterface/list")
    @ApiOperation("LT版-瓶颈接口")
    public Response<List<BottleneckInterfaceLtDTO>> getLtBottleneckInterfaceList(Long reportId, Integer current,
                                                                                 Integer pageSize) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        initPageParam(queryParam, current, pageSize);
        PageInfo<BottleneckInterfaceDTO> pageInfo = reportLocalService.listBottleneckInterface(queryParam);
        List<BottleneckInterfaceLtDTO> dataList = new ArrayList<>();
        if(pageInfo != null && CollectionUtils.isNotEmpty(pageInfo.getList())) {
            pageInfo.getList().stream().forEach(data -> {
                BottleneckInterfaceLtDTO lt = new BottleneckInterfaceLtDTO();
                lt.setRank(data.getRank());
                lt.setApplicationName(data.getApplicationName());
                lt.setInterfaceName(data.getInterfaceName());
                lt.setAvgTps(data.getTps());
                lt.setAvgRt(data.getRt());
                lt.setSuccessRate(data.getSuccessRate());
                dataList.add(lt);
            });
        }
        return Response.success(dataList);
    }

    @GetMapping("/report/risk/application")
    @ApiOperation("风险机器左侧应用")
    public Response<RiskApplicationCountDTO> getRiskApplicationCount(Long reportId) {
        return Response.success(reportLocalService.listRiskApplication(reportId));
    }

    @GetMapping("/report/risk/machine/list")
    @ApiOperation("风险机器右侧列表")
    public Response<List<RiskMacheineDTO>> getRiskMachine(Long reportId, String applicationName, Integer current,
        Integer pageSize) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        queryParam.setApplicationName(applicationName);
        initPageParam(queryParam, current, pageSize);
        return Response.success(reportLocalService.listRiskMachine(queryParam));
    }

    @GetMapping("vlt/report/risk/machine/list")
    @ApiOperation("LT版-风险容器")
    public Response<List<RiskMachineLtDTO>> getRiskMachineList(Long reportId, Integer current,
                                                               Integer pageSize) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        initPageParam(queryParam, current, pageSize);
        PageInfo<RiskMacheineDTO> pageInfo = reportLocalService.listRiskMachine(queryParam);
        List<RiskMachineLtDTO> dataList = new ArrayList<>();
        if(pageInfo != null && CollectionUtils.isNotEmpty(pageInfo.getList())) {
            pageInfo.getList().stream().forEach(data -> {
                RiskMachineLtDTO lt = new RiskMachineLtDTO();
                lt.setId(data.getId());
                lt.setAppName(data.getAppName());
                lt.setAgentId(data.getAgentId());
                lt.setRiskContent(data.getRiskContent());
                dataList.add(lt);
            });
            //根据应用名称，实例排序
            dataList.stream().sorted((o1, o2) -> {
                int value1 = o1.getAppName().compareTo(o2.getAppName());
                if(value1 < 0) {
                    return -1;
                } else if(value1 > 0) {
                    return 1;
                } else {
                    return o1.getAgentId().compareTo(o2.getAgentId());
                }
            });
        }
        return Response.success(dataList);
    }

    @GetMapping("vlt/report/test/build/data")
    @ApiOperation("LT版-主动生成报告数据")
    public Response buildTestReportData(Long jobId, Long sceneId, Long reportId) {
        reportService.buildReportTestData(jobId, sceneId, reportId, WebPluginUtils.traceTenantId());
        return Response.success();
    }

    //@GetMapping("/report/businessActivity/summary/list")
    //@ApiOperation("压测明细")
    //public ResponseResult<NodeTreeSummaryResp> getBusinessActivitySummaryList(Long reportId) {
    //    return ResponseResult.success(reportService.querySummaryList(reportId));
    //}

    @GetMapping("/report/machine/detail")
    @ApiOperation("性能详情")
    public Response<MachineDetailDTO> getMachineDetail(Long reportId, String applicationName, String machineIp) {
        return Response.success(reportLocalService.getMachineDetail(reportId, applicationName, machineIp));
    }

    @GetMapping("vlt/report/machine/list")
    @ApiOperation("LT版-应用性能")
    public Response<List<ReportApplicationTargetDTO>> getLtMachineList(Long reportId) {
        return Response.success(new ArrayList<>());
    }

    @GetMapping("vlt/report/machine/agent/trend")
    @ApiOperation("LT版-应用实例趋势图")
    public Response<List<MachineDetailDTO>> getLtMachineDetail(Long reportId, String applicationName) {
        return Response.success(new ArrayList<>());
    }

    @GetMapping("vlt/report/compare")
    @ApiOperation("LT版-压测报告比对")
    public Response<ReportCompareOutput> getLtReportCompare(@RequestParam List<Long> reportIds, @RequestParam Long businessActivityId) {
        if(CollectionUtils.isEmpty(reportIds) || businessActivityId == null || businessActivityId == -1) {
            log.warn("压测报告比对告警，传入参数长度不正确");
            return Response.success();
        }
        return Response.success(reportLocalService.getReportCompare(reportIds, businessActivityId));
    }

    @GetMapping("/vlt/report/compare/activityInfo/{activityId")
    @ApiOperation("LT版-业务活动trace节点信息")
    public Response<String> getActivityInfoForLtNodeCompare(@PathVariable("activityId") Long activityId) {
        return Response.success();
    }

    @GetMapping("/vlt/report/node/compare")
    @ApiOperation("LT版-节点比对")
    public Response<NodeCompareTargetOut> getLtNodeCompare(NodeCompareTargetInput nodeCompareTargetInput) {
        return this.reportLocalService.getLtNodeCompare(nodeCompareTargetInput);
    }

    @GetMapping("/vlt/report/application/performanceList")
    @ApiOperation("LT版-应用性能列表")
    public Response<Object> getReortAppPerformanceList(long reportId) {
        //TODO 应用维度的性能
        return Response.success(this.reportLocalService.getReortAppPerformanceList(reportId));
    }

    @GetMapping("/vlt/report/application/trendMap")
    @ApiOperation("LT版-应用趋势图")
    public Response<Object> getReportAppTrendMap(ReportTrendQueryReq reportTrendQuery) {
        ResponseResult.success(reportService.queryReportTrend(reportTrendQuery));

        //TODO 趋势图应用维度的性能
        return Response.success();
    }

    @GetMapping("/vlt/report/application/instance/trendMap")
    @ApiOperation("LT版-应用实例趋势图")
    public Response<Object> getReportAppInstanceTrendMap() {
        //TODO
        return Response.success();
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
            ReportDetailOutput response = reportService.getReportByReportId(reportId);
            ReportDetailDTO reportDetail = BeanUtil.copyProperties(response, ReportDetailDTO.class);
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
