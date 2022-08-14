package io.shulie.takin.web.entrypoint.controller.shift;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pamirs.takin.entity.domain.dto.report.ReportCountDTO;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.shift.BaseResult;
import com.pamirs.takin.entity.domain.vo.shift.SceneManagerResult;
import com.pamirs.takin.entity.domain.vo.shift.ShiftCloudVO;
import io.shulie.takin.cloud.sdk.model.common.DataBean;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.TagService;
import io.shulie.takin.web.biz.service.UserService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.utils.PDFUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.mapper.mysql.YReleationTaskMapper;
import io.shulie.takin.web.data.mapper.mysql.YVersionMapper;
import io.shulie.takin.web.data.model.mysql.YReleationTaskEntity;
import io.shulie.takin.web.data.model.mysql.YVersionEntity;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.entrypoint.controller.report.ReportController;
import io.shulie.takin.web.entrypoint.controller.report.ReportLocalController;
import io.shulie.takin.web.entrypoint.controller.scenemanage.SceneTaskController;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Api(tags = "移动云接口", value = "移动云接口")
@RestController
@Slf4j
public class ShiftCloudController {

    private static final String WEB = "web-";
    private static final String BENCH = "bench-";

    @Autowired
    private SceneManageService sceneManageService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private SceneTaskController sceneTaskController;

    @Autowired
    private ReportController reportController;

    @Autowired
    private ReportLocalController reportLocalController;

    private static final Map<Integer, String> TASK_CACHE = new ConcurrentHashMap();

    @Autowired
    private YVersionMapper yVersionMapper;

    @Autowired
    private YReleationTaskMapper mapper;

    @Value("${benchmark.path}")
    private String path;

    @Value("${yidongyun.path:http://devops.testcloud.com}")
    private String yPath;

    @Autowired
    private PDFUtil pdfUtil;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private SceneManageApi sceneManageApi;

    @Resource
    private ReportService reportService;

    //2.1
    @Deprecated
    public void getProjects() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://域名/ms/testcloudplatform/api/service/project/list");
        CloseableHttpResponse response = null;
        try {
            Map data = new HashMap();
            data.put("token", "");
            StringEntity stringEntity = new StringEntity(JSON.toJSONString(data), "UTF-8");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                String result = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            //Ignore
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                //Ignore
            }
        }
    }

    //2.2
    @PostMapping("/api/c/self-service-api/Performance/task_api/task_list")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.YI_DONG_YUN_TASK_LIST,
            needAuth = ActionTypeEnum.QUERY
    )
    public BaseResult getSceneManagers(@RequestBody ShiftCloudVO shiftCloudVO, HttpServletRequest request) {
        String token = request.getAttribute("token").toString();
        String pid = request.getAttribute("pid").toString();

        BaseResult result = new BaseResult<>();
        try {
            SceneManageQueryVO queryVO = new SceneManageQueryVO();
            queryVO.setTenantId(4l);
            queryVO.setPageNumber(shiftCloudVO.getPage_index());
            queryVO.setPageSize(shiftCloudVO.getPage_size());
            queryVO.setTenantCode("yidongyun");
            if (StringUtils.isNoneBlank(shiftCloudVO.getTask_name())) {
                queryVO.setSceneName(shiftCloudVO.getTask_name());
            }
            if (StringUtils.isNotBlank(shiftCloudVO.getProject_id())) {
                queryVO.setEnvCode(shiftCloudVO.getProject_id());
                Long id = tagService.getIdByName(shiftCloudVO.getProject_id());
                if (null != id) {
                    queryVO.setTagId(id);
                }
            }
            queryVO.setIsDeleted(0);
            String userName = shiftCloudVO.getAccount();
            Long id = null;
//            if (StringUtils.isNotBlank(userName)) {
//                id = userService.getIdByName(userName);
//                if (null != id) {
//                    queryVO.setFilterSql("(" + id + ")");
//                    queryVO.setUserId(id);
//                }
//            }
            ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
            Map<String, List> map = new HashMap<>(1);
            List<SceneManagerResult> list = new ArrayList<>();
            map.put("task_list", list);
            long total = 0;
            if (null != responseResult && CollectionUtils.isNotEmpty(responseResult.getData())) {
                responseResult.getData().forEach(r -> list.add(new SceneManagerResult(WEB + r.getId(), r.getSceneName(), r.getUserId(), r.getUserName())));
                total = responseResult.getTotalNum();
            }
            if (list.size() < shiftCloudVO.getPage_size()) {
                int current = 0;
                int pageSize = shiftCloudVO.getPage_size();
                if (total != 0) {
                    int n = shiftCloudVO.getPage_index() * shiftCloudVO.getPage_size();
                    if (n - total > 10) {
                        current = shiftCloudVO.getPage_index() - (int) (total / 10) - 1 < 0 ? 0 : shiftCloudVO.getPage_index() - (int) (total / 10) - 1;
                    }
                }
                //TODO 数据不足是拿基准测试补齐
                Map data = new HashMap();
                String responseJson = HttpUtil.get(path + "/api/benchmark/scene/query?tenantCode=yidongyun&token="+token+"&envCode="+pid+"&current=" + current + "&pageSize=" + pageSize + "&status=", data, 10000);
                if (StringUtils.isNotBlank(responseJson)) {
                    Long finalId = id;
                    JSONArray ja = JSON.parseObject(responseJson).getJSONObject("data").getJSONArray("records");
                    int size = ja.size() > shiftCloudVO.getPage_size() - list.size() ? shiftCloudVO.getPage_size() - list.size() : ja.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject j = JSON.parseObject(ja.get(i).toString());
                        list.add(new SceneManagerResult(BENCH + j.getString("id"), j.getString("sceneName"), finalId, null));
                    }
                }
            }
            result.setData(map);
            return result;
        } catch (Exception e) {
            result.fail(e.getMessage());
            return result;
        }
    }

    //2.3
    @PostMapping("/api/c/self-service-api/Performance/task_api/relate_task")
    @Deprecated
    public BaseResult relateTask(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult baseResult = new BaseResult();
        TASK_CACHE.put(shiftCloudVO.getTask_id(), shiftCloudVO.getTool_task_id());
        return baseResult;
    }

    //2.4
    @PostMapping("/api/c/self-service-api/Performance/task_api/execute_task")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.YI_DONG_YUN_EXECUTE_TASK,
            needAuth = ActionTypeEnum.START_STOP
    )
    public BaseResult executeTask(@RequestBody ShiftCloudVO shiftCloudVO, HttpServletRequest request) {

        String token = request.getAttribute("token").toString();
        String pid = request.getAttribute("pid").toString();

        BaseResult baseResult = new BaseResult();
        try {
            String taskId = shiftCloudVO.getTool_task_id();
            if (isWeb(taskId)) {
                SceneActionParam param = new SceneActionParam();
                param.setSceneId(Long.parseLong(taskId.replaceFirst(WEB, "")));
                param.setLeakSqlEnable(false);
                param.setContinueRead("0");
                WebResponse<SceneActionResp> response = sceneTaskController.start(param);
                if (null != response && response.getSuccess()) {
                    SceneActionResp sceneActionResp = response.getData();
                    if (null != sceneActionResp) {
                        Long toolExecuteId = sceneActionResp.getData();
                        if (null != toolExecuteId) {
                            Map data = new HashMap(1);
                            data.put("tool_execute_id", WEB + toolExecuteId);
                            ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);
                            pool.scheduleWithFixedDelay(() -> {
                                boolean status = this.pushStatus(WEB + toolExecuteId,token,pid);
                                if (status) {
                                    pool.shutdown();
                                }
                            }, 30, 30, TimeUnit.SECONDS);
                            baseResult.setData(data);
                        }
                    }
                }
            } else {
                //TODO 基准测试
                final boolean[] flag = {false};
                AtomicLong id = new AtomicLong();
                int type;
                String suites = null;

                String rj = HttpUtil.get(path + "/api/benchmark/scene/detail?tenantCode=yidongyun&token="+token+"&envCode="+pid+"&id=" + taskId.replaceFirst(BENCH, ""), 10000);
                if (StringUtils.isNotBlank(rj)) {
                    BenchmarkSceneDetailVO b = JSON.parseObject(rj).getObject("data", BenchmarkSceneDetailVO.class);
                    suites = b.getSuites();
                }
                getId(flag, id, type = 1, suites,token,pid);
                if (!flag[0]) getId(flag, id, type = 2, suites, token, pid);
                if (!flag[0]) getId(flag, id, type = 1, null, token, pid);
                if (!flag[0]) getId(flag, id, type = 2, null, token, pid);
                if (!flag[0]) baseResult.fail("压力机繁忙");
                else {
                    Map data = new HashMap();
                    data.put("machineId", id.get());
                    data.put("machineType", type);
                    data.put("sceneId", taskId.replaceFirst(BENCH, ""));
                    data.put("type", "BENCHMARK");


                    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                    HttpPost httpPost = new HttpPost(path + "api/pressure/start?tenantCode=yidongyun&token="+token+"&envCode="+pid);
                    CloseableHttpResponse response = null;
                    String responseJson = null;
                    try {
                        StringEntity stringEntity = new StringEntity(JSON.toJSONString(data), "UTF-8");
                        httpPost.setEntity(stringEntity);
                        httpPost.setHeader("Content-Type", "application/json");
                        response = httpClient.execute(httpPost);
                        HttpEntity entity = response.getEntity();
                        if (null != entity) {
                            responseJson = EntityUtils.toString(response.getEntity());
                        }
                    } catch (Exception e) {
                        //Ignore
                    } finally {
                        try {
                            if (null != response) {
                                response.close();
                            }
                            httpClient.close();
                        } catch (IOException e) {
                            //Ignore
                        }
                    }


                    if (StringUtils.isNotBlank(responseJson)) {
                        Integer code = JSON.parseObject(responseJson).getInteger("code");
                        if (null == code || 200 != code)
                            baseResult.fail(JSON.parseObject(responseJson).getString("message"));
                        else {
                            Map result = new HashMap(1);
                            result.put("tool_execute_id", BENCH + JSON.parseObject(responseJson).getLong("data"));
                            ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);
                            String finalResponseJson = responseJson;
                            pool.scheduleWithFixedDelay(() -> {
                                boolean status = this.pushStatus(BENCH + JSON.parseObject(finalResponseJson).getLong("data"),token,pid);
                                if (status) {
                                    pool.shutdown();
                                }
                            }, 30, 30, TimeUnit.SECONDS);
                            baseResult.setData(result);
                        }
                    }
                }
            }
            return baseResult;
        } catch (Exception e) {
            baseResult.fail(e.getMessage());
            return baseResult;
        } finally {
            Object data = baseResult.getData();
            if (null != data) {
                String taskId = JSON.parseObject(JSON.toJSONString(data)).getString("tool_execute_id");
                YReleationTaskEntity entity = new YReleationTaskEntity();
                entity.setTaskId(taskId);
                entity.setEnvCode(pid);
                entity.setUserId(WebPluginUtils.traceUserId());
                entity.setName(WebPluginUtils.traceUser().getName());
                mapper.insert(entity);
            }
        }
    }

    private void getId(final boolean[] flag, final AtomicLong id, int i, String suite, String token, String pid) {
        Map data = new HashMap();
        String responseJson = HttpUtil.get(path + "/api/machine/list?tenantCode=yidongyun&token="+token+"&envCode="+pid+"&type=" + i, data, 10000);
        if (StringUtils.isNotBlank(responseJson)) {
            JSON.parseObject(responseJson).getJSONArray("data").forEach(o -> {
                if (!flag[0]) {
                    JSONObject j = JSON.parseObject(o.toString());
                    if (StringUtils.equals(j.getString("status"), "0")) {
                        if (StringUtils.isBlank(suite)) {
                            flag[0] = true;
                            id.set(j.getInteger("id"));
                        } else {
                            if (StringUtils.isNotBlank(j.getString("typeMachine"))) {
                                String typeMachine = j.getString("typeMachine");
                                String[] split = typeMachine.split(",");
                                List<String> list = Arrays.asList(split);
                                if (list.contains(suite.split(",")[0])) {
                                    flag[0] = true;
                                    id.set(j.getInteger("id"));
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private boolean isWeb(String taskId) {
        return StringUtils.isNotBlank(taskId) && taskId.startsWith(WEB);
    }

    //2.5
    public boolean pushStatus(String rd, String token, String pid) {
        Map data = new HashMap();
        if (isWeb(rd)) {
            long reportId = Long.parseLong(rd.replaceFirst(WEB, ""));
            try {
                data = this.getData(reportId, true, token, pid);
            } catch (Exception e) {
                log.info("error = "+e.getStackTrace().toString());
            }
        } else {
            long reportId = Long.parseLong(rd.replaceFirst(BENCH, ""));
            try {
                data = this.getData(reportId, false, token, pid);
            } catch (Exception e) {
                log.info("error = "+e.getStackTrace().toString());
            }
        }
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost httpPost = new HttpPost(yPath + "/ms/testcloudplatform/api/service/test/plan/task/status");
//        CloseableHttpResponse response = null;
        try {
            log.info("开始推送数据 report id = "+rd+" data="+JSON.toJSONString(data));
//            StringEntity stringEntity = new StringEntity(JSON.toJSONString(data), "UTF-8");
//            httpPost.setEntity(stringEntity);
//            httpPost.setHeader("Content-Type", "application/json");
            HttpUtil.createPost(yPath + "/ms/testcloudplatform/api/service/test/plan/task/status").body(JSON.toJSONString(data)).header("Content-Type", "application/json").execute().body();
            Object task_status = data.remove("task_status");
            return task_status != null && (Integer.parseInt(task_status.toString()) == 1 || Integer.parseInt(task_status.toString()) == 3);
        } catch (Exception e) {
            log.error(e.getStackTrace().toString());
            return false;
        } finally {
//            try {
//                if (null != response) {
//                    response.close();
//                }
//                httpClient.close();
//            } catch (IOException e) {
//                //Ignore
//            }
        }
    }

    //2.6
    @PostMapping("/ms/task/api/service/task/sdk/log")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.YI_DONG_YUN_TASK_LOG,
            needAuth = ActionTypeEnum.QUERY
    )
    public BaseResult log(@RequestBody ShiftCloudVO shiftCloudVO,HttpServletRequest request) throws Exception {

        String token = request.getAttribute("token").toString();
        String pid = request.getAttribute("pid").toString();

        BaseResult baseResult = new BaseResult();
        Map data = new HashMap();
        if (StringUtils.isNotBlank(shiftCloudVO.getTool_execute_id())) {
            if (isWeb(shiftCloudVO.getTool_execute_id())) {
                data = this.getData(Long.parseLong(shiftCloudVO.getTool_execute_id().replaceFirst(WEB, "")), true,token,pid);
            } else {
                data = this.getData(Long.parseLong(shiftCloudVO.getTool_execute_id().replaceFirst(BENCH, "")), false, token, pid);
            }
        }
        log.info("主动拉取数据 report id = "+shiftCloudVO.getTool_execute_id()+" data="+JSON.toJSONString(data));
        baseResult.setData(data);
        return baseResult;
    }

    private Map getData(long reportId, boolean isWeb, String token, String pid) throws ParseException {
        HashMap data = new HashMap();
        if (isWeb) {
            ResponseResult<ReportDetailOutput> responseResult = reportController.getReportByReportId(reportId);
            Response<ReportCountDTO> reportCount = reportLocalController.getReportCount(reportId);
            SceneManageIdReq r = new SceneManageIdReq();
            r.setReportId(reportId);
            ResponseResult<SceneManageWrapperResp> d = sceneManageApi.getSceneDetail(r);
            if (null != responseResult && responseResult.getSuccess()) {
                ReportDetailOutput output = responseResult.getData();
                Long id = output.getId();
                Long sceneId = output.getSceneId();
                ReportDetailTempOutput out = reportService.tempReportDetail(sceneId);
                Integer conclusion = output.getConclusion();
                String conclusionRemark = output.getConclusionRemark();
                Integer taskStatus = output.getTaskStatus();
                String testTotalTime = output.getTestTotalTime();
                List<BusinessActivitySummaryBean> businessActivity = output.getBusinessActivity();
                Long totalRequest = output.getTotalRequest();
                data.put("tool_execute_id", WEB + id);
                data.put("tool_task_id", WEB + sceneId);
                data.put("tool_code", "Performance");
                int ts = 0;
                if (null != conclusion && 1 == conclusion) ts = 1;
                else if (null != conclusion && 0 == conclusion && 1 == taskStatus) ts = 2;
                else if (null != conclusion && 0 == conclusion && 2 == taskStatus) ts = 1;
                data.put("task_status", ts);
                data.put("task_message", conclusionRemark);
                String startTime = output.getStartTime();
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
                long min = ChronoUnit.SECONDS.between(Instant.ofEpochMilli(date.getTime()), Instant.ofEpochMilli(System.currentTimeMillis()));
                int i1 = 0;
                int i2 = 0;
                Long ps = 0l;
                if (null != d && d.getSuccess() && null != d.getData()) {
                    ps = d.getData().getPressureTestSecond();
                    int ratio = (int) (((float)min / ps) * 100);
                    ratio=ratio>100?100:ratio;
                    data.put("task_progress", ratio + "%");//TODO testTotalTime is null?
                } else data.put("task_progress", "50%");
                if (null != taskStatus) {
                    Map analysis = new HashMap();
                    LambdaQueryWrapper<YVersionEntity> wrapper = new LambdaQueryWrapper<>();
                    wrapper.select(YVersionEntity::getDids, YVersionEntity::getVid);
                    wrapper.eq(YVersionEntity::getSid, sceneId);
                    YVersionEntity entity = yVersionMapper.selectOne(wrapper);
                    int coverDemand = 0;
                    if (null != entity) coverDemand = JSON.parseArray(entity.getDids()).size();
                    analysis.put("coverDemand", coverDemand);
                    if (null != reportCount && reportCount.getSuccess()) {
                        ReportCountDTO dto = reportCount.getData();
                        if (null != dto) {
                            analysis.put("totalCase", null != dto.getBusinessActivityCount() ? dto.getBusinessActivityCount() : 0);
                            analysis.put("executedCase", null != dto.getBusinessActivityCount() ? dto.getBusinessActivityCount() : 0);
                            analysis.put("succeedCase", null != dto.getBusinessActivityCount() ? dto.getBusinessActivityCount() - (null != dto.getNotpassBusinessActivityCount() ? dto.getNotpassBusinessActivityCount() : 0) : 0);
                            analysis.put("failedCase", null != dto.getNotpassBusinessActivityCount() ? dto.getNotpassBusinessActivityCount() : 0);
                        }
                    }
                    analysis.put("performanceResult", null != conclusion && 1 == conclusion ? true : false);
                    analysis.put("executeDuration", ps);
                    List list = new ArrayList();
                    if (CollectionUtils.isNotEmpty(businessActivity)) {
                        for (int i = 0; i < businessActivity.size(); i++) {
//                            if (businessActivity.get(i).getPassFlag() == 0) {
                                Map failedCaseInfo = new HashMap();
                                failedCaseInfo.put("name", businessActivity.get(i).getBusinessActivityName());
//                                Long t = businessActivity.get(i).getTotalRequest();
                            Long t = out.getTotalRequest();
                            int failCount = 0;
                                DataBean successRate = businessActivity.get(i).getSuccessRate();
                                if (null != successRate && null != t) {
                                    BigDecimal value = (BigDecimal) successRate.getValue();
                                    if (null != value) {
                                        failCount = (int) (t * (100 - value.intValue()) / 100);
                                    }
                                }
                                                        if (businessActivity.get(i).getPassFlag() == 0) {
                                StringBuilder sb = new StringBuilder("业务活动不达标:");
                                BusinessActivitySummaryBean b = businessActivity.get(i);
                                DataBean tps = b.getTps();
                                if (null != tps.getResult() && Float.parseFloat(tps.getResult().toString())<Float.parseFloat(tps.getValue().toString()))sb.append("平均TPS ");
                                DataBean avgRT = b.getAvgRT();
                                if (null != avgRT.getResult() && Float.parseFloat(avgRT.getResult().toString())>Float.parseFloat(avgRT.getValue().toString()))sb.append("平均RT ");
                                DataBean rate = b.getSuccessRate();
                                if (null != rate.getResult() && Float.parseFloat(rate.getResult().toString())<Float.parseFloat(rate.getValue().toString()))sb.append("成功率 ");
                                DataBean sa = b.getSa();
                                if (null != sa.getResult() && Float.parseFloat(sa.getResult().toString())<Float.parseFloat(sa.getValue().toString()))sb.append("SA ");
                                failedCaseInfo.put("reason", sb.toString());
                                failedCaseInfo.put("failCount", failCount);
                                list.add(failedCaseInfo);
                            }
                        }
                    }
                    analysis.put("failedCaseInfo", list);
                    data.put("last_analysis_result_list", JSON.toJSONString(analysis));
                } else {
                    data.put("last_analysis_result_list", "{}");
                }
            }
        } else {
            data.put("tool_execute_id", BENCH + reportId);
            Map result = new HashMap();
            result.put("id", reportId);
            String responseJson = HttpUtil.get(path + "/api/task?tenantCode=yidongyun&token="+token+"&envCode="+pid, result, 10000);
            int s = 0;
            int c1 = 0;
            int c3 = 0;
            long r = 0;
            List l = new ArrayList();
            if (StringUtils.isNotBlank(responseJson)) {
                PressureTask pressureTask = JSON.parseObject(responseJson, PressureTask.class);
                if (null == pressureTask.getStopTime()) {
                    r = Duration.between(pressureTask.getStartTime(),LocalDateTime.now()).toMillis()/1000;
                } else {
                    r = Duration.between(pressureTask.getStartTime(),pressureTask.getStopTime()).toMillis()/1000;
                }
                int percentage = pressureTask.getPercentage();
                data.put("tool_task_id", BENCH + pressureTask.getSceneId());
                result.put("sceneId", pressureTask.getSceneId());
                int status = pressureTask.getStatus();
                int runStatus = pressureTask.getRunStatus();
                int pe = percentage >= 100 ? (runStatus == 2 ? 99 :100) : percentage;
                data.put("task_progress", pe + "%");
//                if (runStatus != 2) {
//                    data.put("task_progress", "100%");
//                }
                if (c3 > 0) status = 3;
                else status = 1;
                data.put("task_status", runStatus);

                String startParam = pressureTask.getStartParam();
                if (StringUtils.isNotBlank(startParam)) {
                    s = JSON.parseArray(startParam).size();
                }
                if (runStatus == 1) c1=s;
                else c3=s;
            }
            data.put("tool_code", "Performance");
            String taskResponseJson = HttpUtil.get(path + "/api/task/n?tenantCode=yidongyun&token="+token+"&envCode="+pid, result, 10000);
            Map analysis = new HashMap();
            if (StringUtils.isNotBlank(taskResponseJson))
                analysis.put("coverDemand", Integer.parseInt(taskResponseJson));
            analysis.put("totalCase", s);
            analysis.put("executedCase", s);
            analysis.put("succeedCase", c1);
            analysis.put("failedCase", c3);
            //TODO failedCaseInfo?
            analysis.put("executeDuration", r);
            analysis.put("task_message", "test result " + JSON.toJSONString(l));
            analysis.put("performanceResult", ((int) data.get("task_status")) == 1 ? true : false);
            data.put("last_analysis_result_list", JSON.toJSONString(analysis));
        }
        return data;
    }

    //2.7
    @GetMapping("/api/c/report/export")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.YI_DONG_YUN_TASK_REPORT,
            needAuth = ActionTypeEnum.DOWNLOAD
    )
    public void export(ShiftCloudVO shiftCloudVO, HttpServletResponse response,HttpServletRequest request) throws Exception {

        String token = request.getAttribute("token").toString();
        String pid = request.getAttribute("pid").toString();

        if (StringUtils.isNotBlank(shiftCloudVO.getTool_execute_id())) {
            if (isWeb(shiftCloudVO.getTool_execute_id())) {
                ResponseResult<String> url = reportController.getExportDownLoadUrl(Long.parseLong(shiftCloudVO.getTool_execute_id().replaceFirst(WEB, "")));
                if (url.getSuccess()) {
                    this.export(url.getData(), response);
                }
            } else {
                List<String> paths = benchExport(Long.parseLong(shiftCloudVO.getTool_execute_id().replaceFirst(BENCH, "")),token,pid);
                paths.forEach(p -> export(p, response));
            }

        }
    }

    public void export(String path, HttpServletResponse response) {
        try {
            if (StringUtils.isNotBlank(path)) {
                File file = FileUtil.file(path);
                String filename = file.getName();
                // 以流的形式下载文件。
                InputStream fis = new BufferedInputStream(new FileInputStream(path));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();
                response.setContentType("application/octet-stream;charset=UTF-8");
                String fileName = new String(filename.getBytes("gb2312"), "iso8859-1");
                response.setHeader("Content-disposition", "attachment;filename=" + fileName);
                OutputStream ouputStream = response.getOutputStream();
                ouputStream.write(buffer);
                ouputStream.flush();
                ouputStream.close();
            }
        } catch (Exception e) {
        }
    }

    private List<String> benchExport(long reportId, String token, String pid) {
        String lockKey = String.format(LockKeyConstants.LOCK_REPORT_EXPORT, reportId);
        if (!distributedLock.tryLockSecondsTimeUnit(lockKey, 0L, 30L)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_EXPORT_ERROR, "操作太频繁!");
        }
        List paths = new ArrayList();
        Map result = new HashMap();
        result.put("id", reportId);
        String taskResponseJson = HttpUtil.get(path + "/api/task/task?tenantCode=yidongyun&token="+token+"&envCode="+pid, result, 10000);
        if (StringUtils.isNotBlank(taskResponseJson)) {
            Map<String, Object> dataModel = new HashMap<>();
            List<PressureTaskResult> t = JSON.parseArray(taskResponseJson, PressureTaskResult.class);
            if (CollectionUtils.isNotEmpty(t)) {
                for (PressureTaskResult p : t) {
                    result.put("reportId", p.getTaskId());
                    String d = HttpUtil.get(path + "/api/benchmark/result/detail?tenantCode=yidongyun&token="+token+"&envCode="+pid, result, 10000);
                    if (StringUtils.isNotBlank(d)) {
                        dataModel.put("list", JSON.parseArray(JSON.parseObject(d).getString("data"),PressureTaskResultVO.class));
                        String content = pdfUtil.parseFreemarker("report/tpl.html", dataModel);
                        String pdf = "report_" + reportId + "_" + ".pdf";
                        try {
                            String path = pdfUtil.exportPDF(content, pdf);
                            while (!(FileUtil.exist(path))) {
                                //一直等待文件生成成功
                            }
                            paths.add(path);

                        } catch (IOException e) {
                            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_EXPORT_ERROR, e.getMessage(), e);
                        } finally {
                            distributedLock.unLock(lockKey);
                        }
                    }
                }
            }
        }
        return paths;
    }

    @GetMapping("/api/c/getVersion")
    public ResponseResult<JSONObject> getVersion() {
        String envCode = WebPluginUtils.traceEnvCode();
        Map data = new HashMap();
        data.put("userId", "admin");
        data.put("projectId", envCode);
        String responseJson = HttpUtil.get(yPath + "/ms/vteam/api/service/issue_version/" + envCode + "/flat", data, 10000);
        return ResponseResult.success(JSON.parseObject(responseJson).getJSONObject("data"));
    }

    @GetMapping("/api/c/getDemands")
    public List getDemands(@RequestParam("versionId") String versionId) {
        String envCode = WebPluginUtils.traceEnvCode();
        // 创建Httpclient对象

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
//            HttpPost httpPost = new HttpPost(yPath + "/ms/vteam/api/service/issue/custom/" + envCode + "/version_iteration/VERSION/" + versionId);
//            httpPost.addHeader("X-DEVOPS-UID", "admin");
//            // 创建请求内容
//            StringEntity entity = new StringEntity("[]", ContentType.APPLICATION_JSON);
//            httpPost.setEntity(entity);
//            // 执行http请求
//            response = httpClient.execute(httpPost);
//            resultString = EntityUtils.toString(response.getEntity(), "UTF8");
            resultString = HttpUtil.createPost(yPath + "/ms/vteam/api/service/issue/custom/" + envCode + "/version_iteration/VERSION/" + versionId).body("[]").header("X-DEVOPS-UID", "admin").execute().body();
            if (StringUtils.isNotBlank(resultString)) {
                return JSON.parseArray(JSONObject.parseObject(resultString).getJSONObject("data").getString("records"), Property.class).stream().map(p -> {
                    Map m = new HashMap();
                    m.put("id", p.getProperty().getId().getValue());
                    m.put("title", p.getProperty().getTitle().getDisplayValue());
                    return m;
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
        } finally {
//            try {
//                response.close();
//            } catch (IOException e) {
//            }
        }
        return null;
    }

    @Data
    private static class Property {
        private Record property;
    }

    @Data
    private static class Record {
        private Id id;
        private Title title;
    }

    @Data
    private static class Id {
        private String value;
    }

    @Data
    private static class Title {
        private String displayValue;
    }

    @Data
    static class PressureTask implements Serializable {
        private static final long serialVersionUID = -1513967790213880569L;

        @TableId(type = IdType.AUTO)
        private Long id;

        /**
         * 用户ID
         */
        @TableField(fill = FieldFill.INSERT)
        private Long userId;

        /**
         * 压测机器ID
         */
        private Long machineId;
        /**
         * 机器md5
         */
        @TableField(exist = false)
        private String machineMac;
        /**
         * 压测场景ID
         */
        private Long sceneId;
        /**
         * 压测场景类型：0普通压测场景，1基准测试场景
         */
        private Integer sceneType;
        /**
         * 压测任务状态{1,2}
         */
        private int status;
        /**
         * 开始发压时间
         */
        private LocalDateTime startTime;
        /**
         * 压测任务停止时间
         */
        private LocalDateTime stopTime;
        /**
         * 压测任务运行时间
         */
        private Long runTime;
        /**
         * 压测脚本路径
         */
        private String jmxUrl;
        /**
         * 压测数据路径
         */
        private String dataUrl;
        /**
         * 压测插件路径
         */
        private String jarUrl;

        /**
         * 压测启动参数
         */
        private String startParam;

        /**
         * 压测启动异常信息
         */
        private String errorMsg;
        /**
         * 创建时间
         */
        @TableField(fill = FieldFill.INSERT)
        private LocalDateTime createTime;
        /**
         * 修改时间
         */
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime updateTime;

        private int runStatus;

        private int percentage;
    }


}
