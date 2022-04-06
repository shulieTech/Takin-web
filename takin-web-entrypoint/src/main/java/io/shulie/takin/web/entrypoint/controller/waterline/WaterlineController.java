package io.shulie.takin.web.entrypoint.controller.waterline;

import io.shulie.takin.cloud.sdk.model.ScriptNodeSummaryBean;
import io.shulie.takin.cloud.sdk.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportTrendResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.response.waterline.Metrics;
import io.shulie.takin.web.biz.pojo.response.waterline.TendencyChart;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.WaterlineService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/api/waterline")
@Api(tags = "容量水位管理", value = "容量水位管理")
@RestController
public class WaterlineController {

    @Autowired
    private WaterlineService waterlineService;

    @Autowired
    private ApplicationService applicationService;

    @Resource
    private ReportService reportService;

    @Autowired
    private ActivityService activityService;

    @ApiOperation("全部业务活动")
    @GetMapping("/getAllActivityNames")
    @Deprecated
    public ResponseResult<List<String>> getAllActivityNames() {
        List<String> allActivityNames = waterlineService.getAllActivityNames();
        return ResponseResult.success(allActivityNames);
    }

    @ApiOperation("业务活动下的应用")
    @GetMapping("/getAllApplicationsByActivity")
    @Deprecated
    public ResponseResult<List<String>> getAllApplicationsByActivity(@RequestParam(name = "activityName") String activityName) {
        List<String> allActivityNames = waterlineService.getAllApplicationsByActivity(activityName);
        return ResponseResult.success(allActivityNames);
    }

    /**
     * 获取表格指标数据:需要当前节点的名称+上级名称
     *
     * @param applicationNames
     * @param startTime
     * @return
     * @throws ParseException 如果applicationName没有值，就代表要查询所有的应用，否则查询applicationName
     */
    @ApiOperation("指标数据")
    @GetMapping("/getAllApplicationWithMetrics")
    public ResponseResult<List<Metrics>> getAllApplicationWithMetrics(
            @RequestParam(name = "applicationName", required = false) List<String> applicationNames,
            @RequestParam(name = "startTime") String startTime,
            @RequestParam(name = "sceneId") Long sceneId,
            @RequestParam(name = "tagName", required = false) String tagName,
            @RequestParam(name = "sortKey", required = false) String sortKey,
            @RequestParam(name = "sortOrder", required = false) String sortOrder) throws ParseException {
        List<String> names = null;
        if (CollectionUtils.isNotEmpty(applicationNames)) {
            names.addAll(applicationNames);
        } else {
            ResponseResult<List<String>> result = this.getAllApplicationsWithSceneId(sceneId, null, null);
            if (CollectionUtils.isNotEmpty(result.getData())) {
                names = result.getData();
            }
        }
        List<Metrics> metrics = waterlineService.getAllApplicationWithMetrics(names, startTime);//metrics
        waterlineService.getApplicationNodesAmount(metrics);//node amount
        waterlineService.getApplicationTags(metrics, tagName);//application tags
        if (org.springframework.util.StringUtils.hasText(sortKey) && org.springframework.util.StringUtils.hasText(sortOrder)) {
            metrics = doSort(metrics, sortKey, sortOrder);
        }
        return ResponseResult.success(metrics);
    }

    private List<Metrics> doSort(List<Metrics> metrics, String sortKey, String sortOrder) {
        return metrics.stream().sorted((m1, m2) -> {
            switch (sortKey) {
                case "nodesNumber":
                    if (sortOrder.equals("asc")) {
                        return m1.getNodesNumber() - m2.getNodesNumber() > 0 ? 1 : -1;
                    } else {
                        return m1.getNodesNumber() - m2.getNodesNumber() > 0 ? -1 : 1;
                    }
                case "CPU":
                    if (sortOrder.equals("asc")) {
                        return Double.parseDouble(m1.getCpuRate()) - Double.parseDouble(m2.getCpuRate()) > 0 ? 1 : -1;
                    } else {
                        return Double.parseDouble(m1.getCpuRate()) - Double.parseDouble(m2.getCpuRate()) > 0 ? -1 : 1;
                    }
                case "memory":
                    if (sortOrder.equals("asc")) {
                        return Double.parseDouble(m1.getMemory()) - Double.parseDouble(m2.getMemory()) > 0 ? 1 : -1;
                    } else {
                        return Double.parseDouble(m1.getMemory()) - Double.parseDouble(m2.getMemory()) > 0 ? -1 : 1;
                    }
                default:
                    throw new IllegalArgumentException(sortKey + sortOrder);
            }

        }).collect(Collectors.toList());
    }

    /**
     * 获取压测场景下所有的应用
     */
    @ApiOperation("获取压测场景下所有的应用")
    @GetMapping("/getAllApplicationsWithSceneId")
    public ResponseResult<List<String>> getAllApplicationsWithSceneId(
            @RequestParam(name = "sceneId") Long sceneId,
            @RequestParam(name = "reportId") Long reportId,
            @RequestParam(name = "xpathMd5", required = false) String xpathMd5) {
        if (org.springframework.util.StringUtils.isEmpty(xpathMd5)) {
            List<String> ids = waterlineService.getAllApplicationsWithSceneId(sceneId);
            List<String> names = null;
            if (CollectionUtils.isNotEmpty(ids)) {
                names = waterlineService.getApplicationNamesWithIds(ids);
            }
            return ResponseResult.success(names);
        } else {
            NodeTreeSummaryResp nodeTreeSummaryResp = reportService.querySummaryList(reportId);
            if (null != nodeTreeSummaryResp) {
                List<ScriptNodeSummaryBean> beans = nodeTreeSummaryResp.getScriptNodeSummaryBeans();
                if (CollectionUtils.isNotEmpty(beans)) {
                    List<Long> activityIds = new ArrayList<>();
                    for (ScriptNodeSummaryBean bean : beans) {
                        boolean hit = doGetActivityIds(activityIds, bean, xpathMd5);
                        if (hit) {
                            break;
                        }
                    }
                    Set<String> applicationName = activityService.getApplicationName(activityIds);
                    List<String> names = new ArrayList<>(applicationName);
                    return ResponseResult.success(names);
                }
            }
        }
        return null;
    }

    private boolean doGetActivityIds(List<Long> activityIds, ScriptNodeSummaryBean bean, String xpathMd5) {
        if (StringUtils.equals(bean.getXpathMd5(), xpathMd5)) {
            if (bean.getActivityId() == -1) {
                if (CollectionUtils.isNotEmpty(bean.getChildren())) {
                    bean.getChildren().forEach(b -> doGetActivityIds(activityIds, b));
                }
            } else {
                activityIds.add(bean.getActivityId());
            }
            return true;
        } else if (CollectionUtils.isNotEmpty(bean.getChildren())) {
            for (ScriptNodeSummaryBean b : bean.getChildren()) {
                boolean hit = doGetActivityIds(activityIds, b, xpathMd5);
                if (hit) {
                    return true;
                }
            }
        }
        return false;
    }

    private void doGetActivityIds(List<Long> activityIds, ScriptNodeSummaryBean bean) {
        if (bean.getActivityId() == -1) {
            if (CollectionUtils.isNotEmpty(bean.getChildren())) {
                bean.getChildren().forEach(b -> doGetActivityIds(activityIds, b));
            }
        } else {
            activityIds.add(bean.getActivityId());
        }
    }

    @ApiOperation("获取标签")
    @GetMapping("/getApplicationTags")
    public ResponseResult<List<String>> getApplicationTags(@RequestParam(name = "sceneId") Long sceneId) {
        ResponseResult<List<String>> result = this.getAllApplicationsWithSceneId(sceneId, null, null);
        if (CollectionUtils.isNotEmpty(result.getData())) {
            List<Metrics> metricsList = result.getData().stream().map(name -> {
                Metrics metrics = new Metrics();
                metrics.setApplicationName(name);
                return metrics;
            }).collect(Collectors.toList());
            waterlineService.getApplicationTags(metricsList, null);
            List<String> tags = new ArrayList<>();
            metricsList.forEach(metrics -> tags.addAll(metrics.getTags()));
            return ResponseResult.success(tags);
        }
        return ResponseResult.success();
    }

    @ApiOperation("获取趋势图")
    @GetMapping("/getTendencyChart")
    public ResponseResult getTendencyChart(
            @RequestParam(name = "applicationName") String applicationName,
            @RequestParam(name = "startTime") String startTime,
            @RequestParam(name = "endTime") String endTime,
            @RequestParam(name = "nodes") List<String> nodes,
            @RequestParam(name = "xpathMd5") String xpathMd5,
            @RequestParam(name = "reportId") Long reportId) throws ParseException {
        List<TendencyChart> charts = waterlineService.getTendencyChart(applicationName, startTime, endTime, nodes);
        Map response = new HashMap();
        if (CollectionUtils.isNotEmpty(charts)) {
            TendencyChart tendencyChart = charts.get(0);
            String hostIp = tendencyChart.getAgentId();
            List<String> times = new ArrayList<>();
            charts.forEach(chart -> {
                if (StringUtils.equals(chart.getAgentId(), hostIp)) times.add(chart.getTime());
            });
            response.put("time", times);
            response.put("list", charts);
            ReportTrendQueryReq reportTrendQueryReq = new ReportTrendQueryReq();
            reportTrendQueryReq.setReportId(reportId);
            reportTrendQueryReq.setXpathMd5(xpathMd5);
            ReportTrendResp reportTrendResp = reportService.queryReportTrend(reportTrendQueryReq);
            if (null != reportTrendResp) {
                response.put("tps", reportTrendResp.getTps());
            }
        }
        return ResponseResult.success(response);
    }
}
