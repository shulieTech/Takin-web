package io.shulie.takin.web.entrypoint.controller.shift;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.dto.report.ReportCountDTO;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.shift.BaseResult;
import com.pamirs.takin.entity.domain.vo.shift.SceneManagerResult;
import com.pamirs.takin.entity.domain.vo.shift.ShiftCloudVO;
import io.shulie.takin.cloud.sdk.model.common.DataBean;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.service.TagService;
import io.shulie.takin.web.biz.service.UserService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.entrypoint.controller.report.ReportController;
import io.shulie.takin.web.entrypoint.controller.report.ReportLocalController;
import io.shulie.takin.web.entrypoint.controller.scenemanage.SceneTaskController;
import io.swagger.annotations.Api;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Api(tags = "移动云接口", value = "移动云接口")
@RestController
public class ShiftCloudController {

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
    public BaseResult getSceneManagers(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult result = new BaseResult<>();
        try {
            SceneManageQueryVO queryVO = new SceneManageQueryVO();
            queryVO.setPageNumber(shiftCloudVO.getPage_index());
            queryVO.setPageSize(shiftCloudVO.getPage_size());
            if (StringUtils.isNoneBlank(shiftCloudVO.getTask_name())) {
                queryVO.setSceneName(shiftCloudVO.getTask_name());
            }
            if (StringUtils.isNotBlank(shiftCloudVO.getProject_id())) {
                Long id = tagService.getIdByName(shiftCloudVO.getProject_id());
                if (null != id) {
                    queryVO.setTagId(id);
                }
            }
            queryVO.setIsDeleted(0);
            String userName = shiftCloudVO.getAccount();
            if (StringUtils.isNotBlank(userName)) {
                Long id = userService.getIdByName(userName);
                if (null != id) {
                    queryVO.setFilterSql("(" + id + ")");
                }
            }
            ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
            Map<String, List> map = new HashMap<>(1);
            List<SceneManagerResult> list = new ArrayList<>();
            map.put("task_list", list);
            if (null != responseResult && CollectionUtils.isNotEmpty(responseResult.getData())) {
                responseResult.getData().forEach(r -> list.add(new SceneManagerResult(r.getId(), r.getSceneName(), r.getUserId(), r.getUserName())));
                result.setData(map);
            }
            return result;
        } catch (Exception e) {
            result.fail(e.getMessage());
            return result;
        }
    }

    //2.3
    @PostMapping("/api/c/self-service-api/Performance/task_api/relate_task")
    public BaseResult relateTask(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult baseResult = new BaseResult();
        TASK_CACHE.put(shiftCloudVO.getTask_id(), shiftCloudVO.getTool_task_id());
        return baseResult;
    }

    //2.4
    @PostMapping("/api/c/self-service-api/Performance/task_api/execute_task")
    public BaseResult executeTask(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult baseResult = new BaseResult();
        try {
            String taskId = shiftCloudVO.getTool_task_id();
            SceneActionParam param = new SceneActionParam();
            param.setSceneId(Long.parseLong(taskId));
            param.setLeakSqlEnable(false);
            param.setContinueRead("0");
            WebResponse<SceneActionResp> response = sceneTaskController.start(param);
            if (null != response && response.getSuccess()) {
                SceneActionResp sceneActionResp = response.getData();
                if (null != sceneActionResp) {
                    Long toolExecuteId = sceneActionResp.getData();
                    if (null != toolExecuteId) {
                        Map data = new HashMap(1);
                        data.put("tool_execute_id", String.valueOf(toolExecuteId));
                        ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);
                        pool.scheduleWithFixedDelay(()->{
                            boolean status = this.pushStatus(toolExecuteId);
                            if (status) {
                                pool.shutdown();
                            }
                        },30,30, TimeUnit.SECONDS);
                        baseResult.setData(data);
                    }
                }
            }
            return baseResult;
        } catch (Exception e) {
            baseResult.fail(e.getMessage());
            return baseResult;
        }
    }

    //2.5
    public boolean pushStatus(long reportId) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://devops.testcloud.com/ms/testcloudplatform/api/service/test/plan/task/status");
        CloseableHttpResponse response = null;
        try {
            Map data = this.getData(reportId);
            StringEntity stringEntity = new StringEntity(JSON.toJSONString(data), "UTF-8");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            Object task_status = data.remove("task_status");
            return task_status != null && Integer.parseInt(task_status.toString()) == 2;
        } catch (Exception e) {
            //Ignore
            return false;
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

    //2.6
    @PostMapping("/ms/task/api/service/task/sdk/log")
    public BaseResult log(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult baseResult = new BaseResult();
        if (StringUtils.isNotBlank(shiftCloudVO.getTool_execute_id())) {
            Map data = this.getData(Long.parseLong(shiftCloudVO.getTool_execute_id()));
            baseResult.setData(data);
        }

        return baseResult;
    }

    private Map getData(long reportId) {
        HashMap data = new HashMap();
        ResponseResult<ReportDetailOutput> responseResult = reportController.getReportByReportId(reportId);
        Response<ReportCountDTO> reportCount = reportLocalController.getReportCount(reportId);
        if (null != responseResult && responseResult.getSuccess()) {
            ReportDetailOutput output = responseResult.getData();
            Long id = output.getId();
            Long sceneId = output.getSceneId();
            Integer conclusion = output.getConclusion();
            String conclusionRemark = output.getConclusionRemark();
            Integer taskStatus = output.getTaskStatus();
            String testTotalTime = output.getTestTotalTime();
            List<BusinessActivitySummaryBean> businessActivity = output.getBusinessActivity();
            Long totalRequest = output.getTotalRequest();
            data.put("tool_execute_id", id);
            data.put("tool_task_id", sceneId);
            data.put("tool_code", "Performance");
            data.put("task_status", null != conclusion && 1 == conclusion ? 1 : 2);
            data.put("task_message", conclusionRemark);
            data.put("task_progress", testTotalTime);
            data.put("task_status",taskStatus);
            if (null != taskStatus && taskStatus == 2) {
                Map analysis = new HashMap();
                analysis.put("coverDemand", 0);
                if (null != reportCount && reportCount.getSuccess()) {
                    ReportCountDTO dto = reportCount.getData();
                    if (null != dto) {
                        analysis.put("totalCase", null != dto.getBusinessActivityCount() ? dto.getBusinessActivityCount() : 0);
                        analysis.put("succeedCase", null != dto.getBusinessActivityCount() ? dto.getBusinessActivityCount() - (null != dto.getNotpassBusinessActivityCount() ? dto.getNotpassBusinessActivityCount() : 0) : 0);
                        analysis.put("failedCase", null != dto.getNotpassBusinessActivityCount() ? dto.getNotpassBusinessActivityCount() : 0);
                    }
                }
                analysis.put("executedCase", null != totalRequest ? totalRequest : 0);
                analysis.put("performanceResult", null != conclusion && 1 == conclusion ? 1 : 2);
                analysis.put("executeDuration", testTotalTime);
                List list = new ArrayList();
                if (CollectionUtils.isNotEmpty(businessActivity)) {
                    for (int i = 0; i < businessActivity.size(); i++) {
                        if (businessActivity.get(i).getPassFlag() == 0) {
                            Map failedCaseInfo = new HashMap();
                            failedCaseInfo.put("name", businessActivity.get(i).getBusinessActivityName());
                            Long t = businessActivity.get(i).getTotalRequest();
                            int failCount = 0;
                            DataBean successRate = businessActivity.get(i).getSuccessRate();
                            if (null != successRate) {
                                Integer value = (Integer) successRate.getValue();
                                if (null != value) {
                                    failCount = (int) (t * (100 - value) / 100);
                                }
                            }
                            failedCaseInfo.put("reason", JSON.toJSONString(businessActivity.get(i)));
                            failedCaseInfo.put("failCount", failCount);
                            list.add(failedCaseInfo);
                        }
                    }
                }
                analysis.put("failedCaseInfo", list);
                data.put("last_analysis_result_list", analysis);
            } else {
                data.put("last_analysis_result_list", "none");
            }
        }
        return data;
    }

    //2.7
    @PostMapping("/api/c/report/export")
    public BaseResult export(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult baseResult = new BaseResult();
        if (StringUtils.isNotBlank(shiftCloudVO.getTool_execute_id())) {
            ResponseResult<String> url = reportController.getExportDownLoadUrl(Long.parseLong(shiftCloudVO.getTool_execute_id()));
            baseResult.setData(url);
        }

        return baseResult;
    }
}
