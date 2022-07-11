package io.shulie.takin.web.entrypoint.controller.shift;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
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
import io.shulie.takin.web.data.mapper.mysql.YVersionMapper;
import io.shulie.takin.web.data.model.mysql.YVersionEntity;
import io.shulie.takin.web.entrypoint.controller.report.ReportController;
import io.shulie.takin.web.entrypoint.controller.report.ReportLocalController;
import io.shulie.takin.web.entrypoint.controller.scenemanage.SceneTaskController;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
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

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Value("${benchmark.path}")
    private String path;

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
            if (StringUtils.isNotBlank(userName)) {
                id = userService.getIdByName(userName);
                if (null != id) {
                    queryVO.setFilterSql("(" + id + ")");
                    queryVO.setUserId(id);
                }
            }
            ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
            Map<String, List> map = new HashMap<>(1);
            List<SceneManagerResult> list = new ArrayList<>();
            map.put("task_list", list);
            if (null != responseResult && CollectionUtils.isNotEmpty(responseResult.getData())) {
                responseResult.getData().forEach(r -> list.add(new SceneManagerResult(WEB + r.getId(), r.getSceneName(), r.getUserId(), r.getUserName())));
            }
            if (list.size() < shiftCloudVO.getPage_size()) {
                //TODO 数据不足是拿基准测试补齐
                Map data = new HashMap();
                String responseJson = HttpUtil.get(path + "/api/benchmark/scene/query?userId=" + id + "&current=" + shiftCloudVO.getPage_index() + "&pageSize=" + shiftCloudVO.getPage_size() + "&status=", data, 10000);
                if (StringUtils.isNotBlank(responseJson)) {
                    Long finalId = id;
                    JSON.parseObject(responseJson).getJSONObject("data").getJSONArray("records").forEach(o -> {
                        JSONObject j = JSON.parseObject(o.toString());
                        list.add(new SceneManagerResult(BENCH + j.getString("id"), j.getString("sceneName"), finalId, null));
                    });
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
    public BaseResult executeTask(@RequestBody ShiftCloudVO shiftCloudVO) {
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
                                boolean status = this.pushStatus(WEB + toolExecuteId);
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
                getId(flag, id, type = 1);
                if (!flag[0]) getId(flag, id, type = 2);
                if (!flag[0]) baseResult.fail("压力机繁忙");
                else {
                    Map data = new HashMap();
                    data.put("machineId", id.get());
                    data.put("machineType", type);
                    data.put("sceneId", taskId.replaceFirst(BENCH, ""));
                    data.put("type", "BENCHMARK");
                    String responseJson = HttpUtil.post(path + "api/pressure/start", data, 10000);
                    if (StringUtils.isNotBlank(responseJson)) {
                        Integer code = JSON.parseObject(responseJson).getInteger("code");
                        if (null == code || 200 != code)
                            baseResult.fail(JSON.parseObject(responseJson).getString("message"));
                        else {
                            Map result = new HashMap(1);
                            result.put("tool_execute_id", BENCH + JSON.parseObject(responseJson).getLong("data"));
                            ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);
                            pool.scheduleWithFixedDelay(() -> {
                                boolean status = this.pushStatus(BENCH + JSON.parseObject(responseJson).getLong("data"));
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
        }
    }

    private void getId(final boolean[] flag, final AtomicLong id, int i) {
        Map data = new HashMap();
        String responseJson = HttpUtil.get(path + "/api/machine/list?type=" + i, data, 10000);
        if (StringUtils.isNotBlank(responseJson)) {
            JSON.parseObject(responseJson).getJSONArray("data").forEach(o -> {
                if (!flag[0]) {
                    JSONObject j = JSON.parseObject(o.toString());
                    if (StringUtils.equals(j.getString("status"), "0")) {
                        flag[0] = true;
                        id.set(j.getInteger("id"));
                    }
                }
            });
        }
    }

    private boolean isWeb(String taskId) {
        return StringUtils.isNotBlank(taskId) && taskId.startsWith(WEB);
    }

    //2.5
    public boolean pushStatus(String rd) {
        Map data = new HashMap();
        if (isWeb(rd)) {
            long reportId = Long.parseLong(rd.replaceFirst(WEB, ""));
            try {
                data = this.getData(reportId);
            }catch (Exception e) {}
        } else {
            //TODO bench
            return false;
        }
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://devops.testcloud.com/ms/testcloudplatform/api/service/test/plan/task/status");
        CloseableHttpResponse response = null;
        try {
            StringEntity stringEntity = new StringEntity(JSON.toJSONString(data), "UTF-8");
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-Type", "application/json");
            response = httpClient.execute(httpPost);
            Object task_status = data.remove("task_status");
            return task_status != null && (Integer.parseInt(task_status.toString()) == 1 || Integer.parseInt(task_status.toString()) == 3);
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
    public BaseResult log(@RequestBody ShiftCloudVO shiftCloudVO) throws Exception {
        BaseResult baseResult = new BaseResult();
        if (StringUtils.isNotBlank(shiftCloudVO.getTool_execute_id())) {
            if (isWeb(shiftCloudVO.getTool_execute_id())) {
                Map data = this.getData(Long.parseLong(shiftCloudVO.getTool_execute_id().replaceFirst(WEB, "")));
                baseResult.setData(data);
            } else {
                HashMap data = new HashMap();
                data.put("tool_execute_id", shiftCloudVO.getTool_execute_id().replaceFirst(BENCH, ""));
                Map result = new HashMap();
                String responseJson = HttpUtil.get(path + "/api/task", result, 10000);
                int s = 0;
                int c1 = 0;
                int c3 = 0;
                long r = 0;
                if (StringUtils.isNotBlank(responseJson)) {
                    PressureTask pressureTask = JSON.parseObject(responseJson, PressureTask.class);
                    r = pressureTask.getRunTime();
                    data.put("tool_task_id", pressureTask.getSceneId());
                    int status = pressureTask.getStatus();
                    data.put("task_progress","50%");
                    if (status == 2) {
                        data.put("task_progress","100%");
                        String taskResponseJson = HttpUtil.get(path + "/api/task/task", result, 10000);
                        if (StringUtils.isNotBlank(responseJson)) {
                            List<PressureTaskResult> pressureTaskResults = JSON.parseArray(taskResponseJson, PressureTaskResult.class);
                            for (PressureTaskResult p : pressureTaskResults) {
                                if (StringUtils.isNotBlank(p.getTestResult())) c1+=1;
                                else c3+=1;
                            }
                        }
                    }
                   if (c3>0) status = 3;
                   else status = 1;
                    data.put("task_status",status);
                    String startParam = pressureTask.getStartParam();
                    if (StringUtils.isNotBlank(startParam)) {
                        s = JSON.parseArray(startParam).size();
                    }
                }
                data.put("tool_code", "Performance");
                String taskResponseJson = HttpUtil.get(path + "/api/task/n", result, 10000);
                Map analysis = new HashMap();
                if (StringUtils.isNotBlank(taskResponseJson)) analysis.put("coverDemand", Integer.parseInt(taskResponseJson));
                analysis.put("totalCase",s);
                analysis.put("executedCase",s);
                analysis.put("succeedCase",c1);
                analysis.put("failedCase",c3);
                //TODO failedCaseInfo?
                analysis.put("executeDuration",r);
                data.put("last_analysis_result_list", JSON.toJSONString(analysis));
            }
        }

        return baseResult;
    }

    private Map getData(long reportId) throws ParseException {
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
            data.put("tool_execute_id", String.valueOf(id));
            data.put("tool_task_id", String.valueOf(sceneId));
            data.put("tool_code", "Performance");
            int ts = 0;
            if (null != conclusion && 1 == conclusion) ts = 1;
            else if (null != conclusion && 0 == conclusion && 1 == taskStatus) ts = 2;
            else if (null != conclusion && 0 == conclusion && 2 == taskStatus) ts = 3;
            data.put("task_status", ts);
            data.put("task_message", conclusionRemark);
            String startTime = output.getStartTime();
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
            long min = ChronoUnit.MINUTES.between(Instant.ofEpochMilli(date.getTime()), Instant.ofEpochMilli(System.currentTimeMillis()));
            int i1 = testTotalTime.indexOf(" ");
            int i2 = testTotalTime.indexOf("'");
            String ms = testTotalTime.substring(i1 + 1, i2);
            Double mi = Double.valueOf(ms);
            double tm = min / mi * 100 > 100 ? 100 : min / mi * 100;
            data.put("task_progress", String.valueOf(tm).substring(0, String.valueOf(tm).indexOf(".")) + "%");//TODO testTotalTime is null?
            if (null != taskStatus && taskStatus == 2) {
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
                String a = testTotalTime.substring(0, testTotalTime.indexOf("h"));
                String b = testTotalTime.substring(i1 + 1, i2);
                String c = testTotalTime.substring(i2 + 1, testTotalTime.length() - 1);
                int ti = Integer.parseInt(a) * 3600 + Integer.parseInt(b) * 60 + Integer.parseInt(c);
                analysis.put("executeDuration", ti);
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
                                BigDecimal value = (BigDecimal) successRate.getValue();
                                if (null != value) {
                                    failCount = (int) (t * (100 - value.intValue()) / 100);
                                }
                            }
                            failedCaseInfo.put("reason", JSON.toJSONString(businessActivity.get(i)));
                            failedCaseInfo.put("failCount", failCount);
                            list.add(failedCaseInfo);
                        }
                    }
                }
                analysis.put("failedCaseInfo", list);
                data.put("last_analysis_result_list", JSON.toJSONString(analysis));
            } else {
                data.put("last_analysis_result_list", null);
            }
        }
        return data;
    }

    //2.7
    @GetMapping("/api/c/report/export")
    public void export(@RequestBody ShiftCloudVO shiftCloudVO, HttpServletResponse response) throws Exception {
        if (StringUtils.isNotBlank(shiftCloudVO.getTool_execute_id())) {
            if (isWeb(shiftCloudVO.getTool_execute_id())) {
                ResponseResult<String> url = reportController.getExportDownLoadUrl(Long.parseLong(shiftCloudVO.getTool_execute_id().replaceFirst(WEB, "")));
                if (url.getSuccess()) {
                    String path = url.getData();
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
            } else {
                //TODO bench
            }
        }
    }

    @GetMapping("/api/c/getVersion")
    public ResponseResult<JSONObject> getVersion() {
        String envCode = WebPluginUtils.traceEnvCode();
        Map data = new HashMap();
        data.put("userId", "admin");
        data.put("projectId", envCode);
        String responseJson = HttpUtil.get("http://devops.testcloud.com/ms/vteam/api/service/issue_version/" + envCode + "/flat", data, 10000);
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
            HttpPost httpPost = new HttpPost("http://devops.testcloud.com/ms/vteam/api/service/issue/custom/" + envCode + "/version_iteration/VERSION/" + versionId);
            httpPost.addHeader("X-DEVOPS-UID", "admin");
            // 创建请求内容
            StringEntity entity = new StringEntity("[]", ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "UTF8");
            if (StringUtils.isNotBlank(resultString)) {
                return JSON.parseArray(JSONObject.parseObject(resultString).getJSONObject("data").getString("records"), Property.class).stream().map(p -> {
                    Map m = new HashMap();
                    m.put("id", p.getProperty().getId().getId());
                    m.put("title", p.getProperty().getTitle().getDisplayValue());
                    return m;
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
        } finally {
            try {
                response.close();
            } catch (IOException e) {
            }
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
        private String id;
    }

    @Data
    private static class Title {
        private String displayValue;
    }

    @Data
    class PressureTask implements Serializable {
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
    }

    @Data
    class PressureTaskResult {
        /**
         * 任务ID
         */
        private Long taskId;
        /**
         * 场景ID
         */
        private Long sceneId;
        /**
         * 用户ID
         */
        private Long userId;
        /**
         * 压力机ID
         */
        private Long machineId;
        /**
         * 压力机信息
         */
        private String machine;
        /**
         * 基准测试名称
         */
        private String suite;
        /**
         * 测试结果，json字符串
         */
        private String testResult;
        /**
         * 测试开始时间
         */
        private LocalDateTime startTime;
        /**
         * 测试结束时间
         */
        private LocalDateTime endTime;

    }
}
