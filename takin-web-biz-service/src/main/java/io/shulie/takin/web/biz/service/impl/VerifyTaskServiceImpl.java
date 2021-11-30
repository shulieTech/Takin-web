package io.shulie.takin.web.biz.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Objects;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.utils.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import io.shulie.takin.cloud.common.bean.sla.SlaBean;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import io.shulie.takin.web.biz.service.LeakSqlService;
import com.pamirs.takin.common.constant.VerifyTypeEnum;
import io.shulie.takin.common.beans.component.SelectVO;
import org.apache.commons.collections4.CollectionUtils;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.common.beans.response.ResponseResult;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import io.shulie.takin.web.common.exception.TakinWebException;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import io.shulie.takin.web.biz.service.elasticjoblite.VerifyJob;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import io.shulie.takin.cloud.open.resp.scenetask.SceneActionResp;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import org.springframework.transaction.support.TransactionTemplate;
import io.shulie.takin.web.data.dao.leakverify.LeakVerifyDetailDAO;
import io.shulie.takin.web.data.dao.leakverify.LeakVerifyResultDAO;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.pojo.request.leakverify.VerifyTaskConfig;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import io.shulie.takin.web.data.param.leakverify.LeakVerifyDetailCreateParam;
import io.shulie.takin.web.data.param.leakverify.LeakVerifyResultCreateParam;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlBatchRefsRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStopRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyDetailResponse;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskJobParameter;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStartRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyDsResultResponse;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.service.elasticjoblite.CoordinatorRegistryCenterService;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskRunAssembleRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskRunWithSaveRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskRunWithoutSaveRequest;

/**
 * @author fanxx
 * @date 2021/1/5 3:06 下午
 */
@Slf4j
@Component
public class VerifyTaskServiceImpl implements VerifyTaskService {

    public static String jobSchedulerRedisKey = "job:scheduler:key";

    @Autowired
    LeakSqlService leakSqlService;
    @Autowired
    private RedisClientUtils redis;
    @Autowired
    private SceneTaskApi sceneTaskApi;
    @Autowired
    LeakVerifyResultDAO verifyResultDAO;
    @Autowired
    LeakVerifyDetailDAO verifyDetailDAO;
    @Autowired
    BusinessLinkManageDAO businessLinkManageDAO;
    @Autowired
    private SceneManageService sceneManageService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CoordinatorRegistryCenterService registryCenterService;

    @Override
    public void showdownVerifyTask() {
        Map<Object, Object> serviceMap = redis.hmget(jobSchedulerRedisKey);
        if (!serviceMap.isEmpty()) {
            Set<Object> keySet = serviceMap.keySet();
            keySet.forEach(mapKey -> {
                String tmpRefId = ((String)mapKey).split("\\$")[1];
                Long sceneId = Long.parseLong(tmpRefId);
                SceneManageIdReq req = new SceneManageIdReq();
                req.setId(sceneId);
                ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(req);
                if (!Objects.isNull(response.getData())) {
                    SceneActionResp resp = JSONObject.parseObject(JSON.toJSONString(response.getData()),
                        SceneActionResp.class);
                    Long status = resp.getData();
                    //停止状态
                    if (0L == status) {
                        log.info("压测场景已停止，关闭验证任务，场景ID[{}]", sceneId);
                        Object value = serviceMap.get(mapKey);
                        JobScheduler jobScheduler = JsonHelper.json2Bean(value.toString(),JobScheduler.class);
                        jobScheduler.getSchedulerFacade().shutdownInstance();
                        serviceMap.remove(mapKey);
                        //漏数验证兜底检测
                        log.info("漏数验证兜底检测，场景ID[{}]", sceneId);
                        LeakVerifyTaskRunWithSaveRequest runRequest = new LeakVerifyTaskRunWithSaveRequest();
                        runRequest.setRefType(VerifyTypeEnum.SCENE.getCode());
                        runRequest.setRefId(sceneId);
                        runRequest.setReportId(resp.getReportId());
                        this.runWithResultSave(runRequest);
                    } else {
                        log.debug("压测场景仍在运行，无法关闭验证任务，状态:[{}]", status);
                    }
                } else {
                    log.error("cloud返回的数据为空，无法判断压测场景状态");
                }
            });
        }
    }

    @Override
    public void start(LeakVerifyTaskStartRequest startRequest) {
        LeakVerifyTaskStopRequest stopRequest = new LeakVerifyTaskStopRequest();
        stopRequest.setRefType(startRequest.getRefType());
        stopRequest.setRefId(startRequest.getRefId());
        this.stop(stopRequest);

        LeakVerifyTaskJobParameter jobParameterObject = new LeakVerifyTaskJobParameter();
        jobParameterObject.setRefType(startRequest.getRefType());
        jobParameterObject.setRefId(startRequest.getRefId());
        jobParameterObject.setTimeInterval(startRequest.getTimeInterval());
        jobParameterObject.setReportId(startRequest.getReportId());
        LeakSqlBatchRefsRequest refsRequest = new LeakSqlBatchRefsRequest();
        refsRequest.setBusinessActivityIds(startRequest.getBusinessActivityIds());
        List<VerifyTaskConfig> verifyTaskConfigList = leakSqlService.getVerifyTaskConfig(refsRequest);
        if (CollectionUtils.isEmpty(verifyTaskConfigList)) {
            throw new TakinWebException(ExceptionCode.VERIFY_TASK_RUN_FAILED, "该业务活动暂未配置漏数脚本");
        }
        jobParameterObject.setVerifyTaskConfigList(verifyTaskConfigList);
        String jobParameter = JSON.toJSONString(jobParameterObject);
        JobScheduler jobScheduler = new JobScheduler(registryCenterService.getRegistryCenter(), createJobConfiguration(jobParameter));
        jobScheduler.init();
        String mapKey = startRequest.getRefType() + "$" + startRequest.getRefId();
        redis.hmset(jobSchedulerRedisKey, mapKey, JsonHelper.bean2Json(jobScheduler));
    }

    private LiteJobConfiguration createJobConfiguration(String jobParameter) {
        LeakVerifyTaskJobParameter jobParameterObject = JSON.parseObject(jobParameter,
            LeakVerifyTaskJobParameter.class);
        String jobName = "验证任务$" + jobParameterObject.getRefType() + "$" + jobParameterObject.getRefId();
        int second = Calendar.getInstance().get(Calendar.SECOND);
        int interval = jobParameterObject.getTimeInterval();
        String cron = second + " */" + interval + " * * * ?";
        log.info(jobName + ",cron表达式:[{}]", cron);
        JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder(jobName, cron, 1).jobParameter(
            jobParameter).build();
        SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig,
            VerifyJob.class.getCanonicalName());
        return LiteJobConfiguration.newBuilder(simpleJobConfig).overwrite(Boolean.TRUE).build();
    }

    @Override
    public void stop(LeakVerifyTaskStopRequest stopRequest) {
        //关闭验证任务线程池
        Integer refType = stopRequest.getRefType();
        Long refId = stopRequest.getRefId();
        String mapKey = refType + "$" + refId;
        Map<Object, Object> map = redis.hmget(jobSchedulerRedisKey);
        if (map.containsKey(mapKey)) {
            Object value = map.get(mapKey);
            JobScheduler scheduler = JsonHelper.json2Bean(value.toString(),JobScheduler.class);
            log.info("开始关闭验证任务:[{},{}]",
                Objects.requireNonNull(VerifyTypeEnum.getTypeByCode(stopRequest.getRefType())).name(),
                stopRequest.getRefId());
            scheduler.getSchedulerFacade().shutdownInstance();
            redis.hmdelete(jobSchedulerRedisKey, mapKey);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("error:", e);
            }
            log.info("验证任务已关闭:[{},{}]",
                Objects.requireNonNull(VerifyTypeEnum.getTypeByCode(stopRequest.getRefType())).name(),
                stopRequest.getRefId());
        } else {
            log.debug("验证任务不存在:[{},{}]",
                Objects.requireNonNull(VerifyTypeEnum.getTypeByCode(stopRequest.getRefType())).name(),
                stopRequest.getRefId());
        }
    }

    public LeakVerifyTaskResultResponse run(LeakVerifyTaskRunAssembleRequest runAssembleRequest, Boolean isSaveResult) {
        VerifyJob verifyJob = new VerifyJob();
        LeakVerifyTaskJobParameter jobParameter = new LeakVerifyTaskJobParameter();
        Integer refType = runAssembleRequest.getRefType();
        Long refId = runAssembleRequest.getRefId();
        List<Long> businessActivityIds = runAssembleRequest.getBusinessActivityIds();
        jobParameter.setRefType(refType);
        jobParameter.setRefId(refId);
        jobParameter.setReportId(runAssembleRequest.getReportId());

        LeakSqlBatchRefsRequest refsRequest = new LeakSqlBatchRefsRequest();
        refsRequest.setBusinessActivityIds(businessActivityIds);
        List<VerifyTaskConfig> verifyTaskConfigList = leakSqlService.getVerifyTaskConfig(refsRequest);
        if (CollectionUtils.isEmpty(verifyTaskConfigList)) {
            throw new TakinWebException(ExceptionCode.VERIFY_TASK_RUN_FAILED, "该业务活动暂未配置漏数脚本");
        }
        jobParameter.setVerifyTaskConfigList(verifyTaskConfigList);
        Map<Integer, Integer> resultMap = verifyJob.run(jobParameter);
        if (isSaveResult) {
            //验证结果入库
            saveVerifyResult(jobParameter, resultMap);
            log.info("兜底验证结果入库:[{},{}]", Objects.requireNonNull(VerifyTypeEnum.getTypeByCode(refType)).name(), refId);
            return null;
        } else {
            //组装验证结果
            LeakVerifyTaskResultResponse taskResultResponse = new LeakVerifyTaskResultResponse();
            taskResultResponse.setRefType(refType);
            taskResultResponse.setRefId(refId);
            taskResultResponse.setStatus(VerifyResultStatusEnum.UNCHECK.getCode());
            List<LeakVerifyDsResultResponse> dsResultResponseList = Lists.newArrayList();
            verifyTaskConfigList.forEach(verifyTaskConfig -> {
                LeakVerifyDsResultResponse dsResultResponse = new LeakVerifyDsResultResponse();
                dsResultResponse.setDatasourceId(verifyTaskConfig.getDatasourceId());
                dsResultResponse.setDatasourceName(verifyTaskConfig.getDatasourceName());
                dsResultResponse.setJdbcUrl(verifyTaskConfig.getJdbcUrl());

                List<String> sqlList = verifyTaskConfig.getSqls();
                List<LeakVerifyDetailResponse> detailResponseList = Lists.newArrayList();
                for (int i = 0; i < sqlList.size(); i++) {
                    String sql = sqlList.get(i);
                    String keyString = refType + refId + verifyTaskConfig.getDatasourceId() + sql;
                    Integer mapKey = keyString.hashCode();
                    LeakVerifyDetailResponse detailResponse = new LeakVerifyDetailResponse();
                    detailResponse.setOrder(i);
                    detailResponse.setSql(sql);
                    if (resultMap.containsKey(mapKey)) {
                        Integer count = resultMap.get(mapKey);
                        switch (count) {
                            case 0:
                                detailResponse.setStatus(VerifyResultStatusEnum.NORMAL.getCode());
                                break;
                            case 1:
                                detailResponse.setStatus(VerifyResultStatusEnum.LEAKED.getCode());
                                break;
                            case 3:
                                detailResponse.setStatus(VerifyResultStatusEnum.FAILED.getCode());
                            default:
                        }
                    } else {
                        detailResponse.setStatus(VerifyResultStatusEnum.UNCHECK.getCode());
                    }
                    SelectVO vo = new SelectVO();
                    vo.setValue(detailResponse.getStatus().toString());
                    vo.setLabel(VerifyResultStatusEnum.getLabelByCode(detailResponse.getStatus()));
                    detailResponse.setStatusResponse(vo);
                    detailResponseList.add(detailResponse);
                }
                dsResultResponse.setDetailResponseList(detailResponseList);
                Map<Integer, List<LeakVerifyDetailResponse>> groupDetailResultMap
                    = detailResponseList.stream().collect(Collectors.groupingBy(LeakVerifyDetailResponse::getStatus));

                if (groupDetailResultMap.containsKey(VerifyResultStatusEnum.LEAKED.getCode())) {
                    dsResultResponse.setStatus(VerifyResultStatusEnum.LEAKED.getCode());
                } else if (groupDetailResultMap.containsKey(VerifyResultStatusEnum.FAILED.getCode())) {
                    dsResultResponse.setStatus(VerifyResultStatusEnum.FAILED.getCode());
                } else if (groupDetailResultMap.containsKey(VerifyResultStatusEnum.UNCHECK.getCode())) {
                    dsResultResponse.setStatus(VerifyResultStatusEnum.UNCHECK.getCode());
                } else {
                    dsResultResponse.setStatus(VerifyResultStatusEnum.NORMAL.getCode());
                }
                SelectVO vo = new SelectVO();
                vo.setValue(dsResultResponse.getStatus().toString());
                vo.setLabel(VerifyResultStatusEnum.getLabelByCode(dsResultResponse.getStatus()));
                dsResultResponse.setStatusResponse(vo);
                dsResultResponseList.add(dsResultResponse);
            });
            Map<Integer, List<LeakVerifyDsResultResponse>> groupDsResultMap
                = dsResultResponseList.stream().collect(Collectors.groupingBy(LeakVerifyDsResultResponse::getStatus));
            if (groupDsResultMap.containsKey(VerifyResultStatusEnum.LEAKED.getCode())) {
                taskResultResponse.setStatus(VerifyResultStatusEnum.LEAKED.getCode());
            } else if (groupDsResultMap.containsKey(VerifyResultStatusEnum.FAILED.getCode())) {
                taskResultResponse.setStatus(VerifyResultStatusEnum.FAILED.getCode());
            } else if (groupDsResultMap.containsKey(VerifyResultStatusEnum.UNCHECK.getCode())) {
                taskResultResponse.setStatus(VerifyResultStatusEnum.UNCHECK.getCode());
            } else {
                taskResultResponse.setStatus(VerifyResultStatusEnum.NORMAL.getCode());
            }
            taskResultResponse.setDsResultResponseList(dsResultResponseList);
            Integer taskStatus = taskResultResponse.getStatus();
            SelectVO vo = new SelectVO();
            vo.setValue(taskStatus.toString());
            vo.setLabel(VerifyResultStatusEnum.getLabelByCode(taskStatus));
            taskResultResponse.setStatusResponse(vo);
            return taskResultResponse;
        }
    }

    @Override
    public LeakVerifyTaskResultResponse runWithoutResultSave(LeakVerifyTaskRunWithoutSaveRequest runRequest) {
        LeakVerifyTaskRunAssembleRequest runAssembleRequest = new LeakVerifyTaskRunAssembleRequest();
        runAssembleRequest.setRefType(VerifyTypeEnum.ACTIVITY.getCode());
        runAssembleRequest.setRefId(runRequest.getBusinessActivityId());
        runAssembleRequest.setBusinessActivityIds(Collections.singletonList(runRequest.getBusinessActivityId()));
        return this.run(runAssembleRequest, Boolean.FALSE);
    }

    @Override
    public LeakVerifyTaskResultResponse runWithResultSave(LeakVerifyTaskRunWithSaveRequest runRequest) {
        LeakVerifyTaskRunAssembleRequest runAssembleRequest = new LeakVerifyTaskRunAssembleRequest();
        runAssembleRequest.setRefType(runRequest.getRefType());
        runAssembleRequest.setRefId(runRequest.getRefId());
        runAssembleRequest.setReportId(runRequest.getReportId());
        if (runRequest.getRefType().equals(VerifyTypeEnum.SCENE.getCode())) {
            Long sceneId = runRequest.getRefId();
            ResponseResult<SceneManageWrapperResp> webResponse = sceneManageService.detailScene(sceneId);
            SceneManageWrapperDTO sceneData = BeanUtil.copyProperties(webResponse.getData(), SceneManageWrapperDTO.class);
            List<Long> businessActivityIds =
                sceneData.getBusinessActivityConfig().stream().map(
                    SceneBusinessActivityRefDTO::getBusinessActivityId).collect(
                    Collectors.toList());
            runAssembleRequest.setBusinessActivityIds(businessActivityIds);
        } else if (runRequest.getRefType().equals(VerifyTypeEnum.FLOW.getCode())) {
            // 根据业务流程id, 获得业务活动ids
            runAssembleRequest.setBusinessActivityIds(businessLinkManageDAO.listIdsByBusinessFlowId(runRequest.getRefId()));
        } else {
            runAssembleRequest.setBusinessActivityIds(Collections.singletonList(runRequest.getRefId()));
        }
        return this.run(runAssembleRequest, Boolean.TRUE);
    }

    @Override
    public void saveVerifyResult(LeakVerifyTaskJobParameter jobParameter, Map<Integer, Integer> resultMap) {
        if (resultMap.isEmpty()) {
            log.warn("验证结果为空，保存漏数结果失败:[{},{}]", Objects
                    .requireNonNull(VerifyTypeEnum.getTypeByCode(jobParameter.getRefType())).name(),
                jobParameter.getRefId());
            return;
        }
        transactionTemplate.execute((s) -> {
            Long refId = jobParameter.getRefId();
            Integer refType = jobParameter.getRefType();
            Long reportId = jobParameter.getReportId();
            List<VerifyTaskConfig> verifyTaskConfigList = jobParameter.getVerifyTaskConfigList();
            verifyTaskConfigList.forEach(verifyConfig -> {
                Long datasourceId = verifyConfig.getDatasourceId();
                // 生成验证结果基础信息
                LeakVerifyResultCreateParam resultCreateParam = new LeakVerifyResultCreateParam();
                resultCreateParam.setRefType(refType);
                resultCreateParam.setRefId(refId);
                resultCreateParam.setReportId(reportId);
                resultCreateParam.setDbresourceId(verifyConfig.getDatasourceId());
                resultCreateParam.setDbresourceName(verifyConfig.getDatasourceName());
                resultCreateParam.setDbresourceUrl(verifyConfig.getJdbcUrl());
                //后续定义一个枚举类：是否漏数 0:正常;1:漏数;2:未检测;3:检测失败
                Long resultId = verifyResultDAO.insert(resultCreateParam);
                List<String> sqlList = verifyConfig.getSqls();
                List<LeakVerifyDetailCreateParam> detailCreateParamList = Lists.newArrayList();
                //验证结果详情入库
                sqlList.forEach(sql -> {
                    LeakVerifyDetailCreateParam detailCreateParam = new LeakVerifyDetailCreateParam();
                    detailCreateParam.setResultId(resultId);
                    detailCreateParam.setLeakSql(sql);
                    String keyString = refType + refId + datasourceId + sql;
                    Integer sqlKey = keyString.hashCode();
                    if (resultMap.containsKey(sqlKey)) {
                        Integer count = resultMap.get(sqlKey);
                        if (VerifyResultStatusEnum.LEAKED.getCode().equals(count)) {
                            SceneManageIdReq queryReq = new SceneManageIdReq();
                            queryReq.setId(refId);
                            ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(queryReq);
                            if (!Objects.isNull(response.getData())) {
                                SceneActionResp resp = JSONObject.parseObject(JSON.toJSONString(response.getData()),
                                    SceneActionResp.class);
                                Long status = resp.getData();
                                if (2L == status) {
                                    //如果压测场景正在运行则立刻停止压测任务
                                    SceneManageIdReq stopReq = new SceneManageIdReq();
                                    // 触发sla终止压测
                                    SlaBean slaBean = new SlaBean();
                                    slaBean.setRuleName("数据验证");
                                    // 对象数据源
                                    slaBean.setBusinessActivity(verifyConfig.getDatasourceName() + "(" + sql + ")");
                                    slaBean.setRule("存在漏数");
                                    stopReq.setSlaBean(slaBean);
                                    stopReq.setId(refId);
                                    sceneTaskApi.stopTask(stopReq);
                                    log.warn("存在漏数，触发SLA终止压测，sceneId={}, sql={}", refId, sql);
                                }
                            }
                            detailCreateParam.setStatus(VerifyResultStatusEnum.LEAKED.getCode());
                        } else if (VerifyResultStatusEnum.FAILED.getCode().equals(count)) {
                            detailCreateParam.setStatus(VerifyResultStatusEnum.FAILED.getCode());
                        } else {
                            detailCreateParam.setStatus(VerifyResultStatusEnum.NORMAL.getCode());
                        }
                    } else {
                        detailCreateParam.setStatus(VerifyResultStatusEnum.UNCHECK.getCode());
                    }
                    detailCreateParamList.add(detailCreateParam);
                });
                verifyDetailDAO.insertBatch(detailCreateParamList);
            });
            return null;
        });
    }

    @Override
    public Set<String> queryVerifyTask() {
        Set<String> stringSet = Sets.newHashSet();
        Map<Object, Object> map = redis.hmget(jobSchedulerRedisKey);
        if (!map.isEmpty()) {
            stringSet = map.keySet().stream().map(t -> (String)t).collect(Collectors.toSet());
        }
        return stringSet;
    }
}
