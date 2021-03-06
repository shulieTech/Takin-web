package io.shulie.takin.web.biz.service.scenemanage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneSchedulerTaskResponse;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.data.dao.scenemanage.SceneSchedulerTaskDao;
import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskInsertParam;
import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskQueryParam;
import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskUpdateParam;
import io.shulie.takin.web.data.result.scenemanage.SceneSchedulerTaskResult;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-12-01 10:36
 */
@Slf4j
@Service
public class SceneSchedulerTaskServiceImpl implements SceneSchedulerTaskService {
    @Resource
    private SceneTaskService sceneTaskService;
    @Resource
    private SceneSchedulerTaskDao sceneSchedulerTaskDao;
    /**
     * ?????????????????????
     * ps:??????????????????????????????
     */
    private final ExecutorService threadPool = new ThreadPoolExecutor(10, 10,
        0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
        r -> new Thread(r, "????????????"), new CallerRunsPolicy());

    @Override
    public Long insert(SceneSchedulerTaskCreateRequest request) {
        if (request == null) {return null;}
        if (request.getSceneId() == null) {
            throw new TakinWebException(ExceptionCode.SCENE_SCHEDULER_TASK_SCENE_ID_VALID_ERROR, "????????????-????????????????????????");
        }
        verificationScheduleTime(request.getExecuteTime());
        SceneSchedulerTaskResult result = sceneSchedulerTaskDao.selectBySceneId(request.getSceneId());
        if (result != null) {return result.getId();}
        SceneSchedulerTaskInsertParam insertParam = new SceneSchedulerTaskInsertParam();
        BeanUtils.copyProperties(request, insertParam);
        return sceneSchedulerTaskDao.create(insertParam);
    }

    /**
     * ???????????????????????????????????????1????????????
     */
    public void verificationScheduleTime(Date time) {
        if (time == null) {
            throw new TakinWebException(ExceptionCode.SCENE_SCHEDULER_TASK_EXECUTE_TIME_VALID_ERROR, "????????????-????????????????????????");
        }
        if (time.getTime() - System.currentTimeMillis() < 1000 * 60) {
            throw new TakinWebException(ExceptionCode.SCENE_SCHEDULER_TASK_EXECUTE_TIME_VALID_ERROR, "??????????????????????????????????????????1??????");
        }
    }

    @Override
    public void delete(Long id) {
        sceneSchedulerTaskDao.delete(id);
    }

    @Override
    public void update(SceneSchedulerTaskUpdateRequest updateRequest, Boolean needVerify) {
        if (needVerify != null && needVerify) {verificationScheduleTime(updateRequest.getExecuteTime());}
        SceneSchedulerTaskUpdateParam updateParam = new SceneSchedulerTaskUpdateParam();
        BeanUtils.copyProperties(updateRequest, updateParam);
        sceneSchedulerTaskDao.update(updateParam);
    }

    @Override
    public SceneSchedulerTaskResponse selectBySceneId(Long sceneId) {
        SceneSchedulerTaskResult result = sceneSchedulerTaskDao.selectBySceneId(sceneId);
        if (result == null) {return null;}
        SceneSchedulerTaskResponse response = new SceneSchedulerTaskResponse();
        BeanUtils.copyProperties(result, response);
        return response;
    }

    @Override
    public void deleteBySceneId(Long sceneId) {
        if (sceneId == null) {return;}
        sceneSchedulerTaskDao.deleteBySceneId(sceneId);
    }

    @Override
    public List<SceneSchedulerTaskResponse> selectBySceneIds(List<Long> sceneIds) {
        if (CollectionUtils.isEmpty(sceneIds)) {return Lists.newArrayList();}
        List<SceneSchedulerTaskResult> resultList = sceneSchedulerTaskDao.selectBySceneIds(sceneIds);
        return result2RespList(resultList);
    }

    @Override
    public List<SceneSchedulerTaskResponse> selectByExample(SceneSchedulerTaskQueryRequest request) {
        if (request == null) {return Lists.newArrayList();}
        SceneSchedulerTaskQueryParam queryParam = new SceneSchedulerTaskQueryParam();
        BeanUtils.copyProperties(request, queryParam);
        List<SceneSchedulerTaskResult> resultList = sceneSchedulerTaskDao.selectByExample(queryParam);
        return result2RespList(resultList);
    }

    @Override
    public void executeSchedulerPressureTask() {
        SceneSchedulerTaskQueryRequest request = new SceneSchedulerTaskQueryRequest();
        Date previousSeconds = DateUtils.getPreviousNSecond(-67);
        String time = DateUtils.dateToString(previousSeconds, DateUtils.FORMATE_YMDHM);
        request.setEndTime(time);
        List<SceneSchedulerTaskResponse> responseList = this.selectByExample(request);
        if (CollectionUtils.isEmpty(responseList)) {return;}
        for (SceneSchedulerTaskResponse scheduler : responseList) {
            if (scheduler.getExecuteTime() == null || scheduler.getIsExecuted() == null
                || scheduler.getIsExecuted() != 0) {
                continue;
            }
            Date dbDate = scheduler.getExecuteTime();
            Date now = new Date();
            if (dbDate.before(now)) {
                // ????????????
                String lockKey = JobRedisUtils.getSchedulerRedis(scheduler.getTenantId(),scheduler.getEnvCode(),scheduler.getId());
                if (RedisHelper.hasKey(lockKey)) {
                    continue;
                }
                RedisHelper.setValue(lockKey,true);
                threadPool.submit(() -> {
                    // ??????????????????
                    TenantCommonExt ext = new TenantCommonExt();
                    ext.setSource(ContextSourceEnum.JOB.getCode());
                    ext.setTenantId(scheduler.getTenantId());
                    ext.setEnvCode(scheduler.getEnvCode());
                    TenantInfoExt infoExt = WebPluginUtils.getTenantInfo(scheduler.getTenantId());
                    if(infoExt == null) {
                        log.error("????????????????????????{}???",scheduler.getTenantId());
                        return;
                    }
                    ext.setTenantAppKey(infoExt.getTenantAppKey());
                    try {
                        WebPluginUtils.setTraceTenantContext(ext);
                        SceneActionParam param = new SceneActionParam();
                        param.setSceneId(scheduler.getSceneId());
                        CheckResultVo resultVo;
                        do {
                            resultVo = sceneTaskService.preCheck(param);
                            param.setResourceId(resultVo.getResourceId());
                        } while (CheckStatus.PENDING.ordinal() == resultVo.getStatus());
                        if (CheckStatus.FAIL.ordinal() == resultVo.getStatus()) { // ??????
                            throw new RuntimeException(StringUtils.join(resultVo.getCheckList(), ","));
                        }
                        //??????
                        SceneActionParam startParam = new SceneActionParam();
                        startParam.setSceneId(scheduler.getSceneId());
                        startParam.setEnvCode(scheduler.getEnvCode());
                        startParam.setTenantId(scheduler.getTenantId());
                        // ?????????????????????????????????
                        UserExt userInfo = WebPluginUtils.getUserExtByUserId(scheduler.getUserId());
                        if (userInfo != null) {
                            WebPluginUtils.setCloudUserData(new ContextExt() {{
                                setUserId(userInfo.getId());
                                setUserName(userInfo.getName());
                            }});
                        }
                        sceneTaskService.startTask(startParam);
                    } catch (Exception e) {
                        log.error("??????????????????????????????...", e);
                    } finally {
                        SceneSchedulerTaskUpdateRequest updateRequest = new SceneSchedulerTaskUpdateRequest();
                        updateRequest.setId(scheduler.getId());
                        updateRequest.setIsExecuted(2);
                        updateRequest.setIsDeleted(true);
                        updateRequest.setIsDeleted(true);
                        this.update(updateRequest, false);
                        // ??????
                        RedisHelper.delete(lockKey);
                    }
                });
            }
        }
    }

    List<SceneSchedulerTaskResponse> result2RespList(List<SceneSchedulerTaskResult> resultList) {
        if (CollectionUtils.isEmpty(resultList)) {return Lists.newArrayList();}
        List<SceneSchedulerTaskResponse> responseList = new ArrayList<>();
        resultList.forEach(result -> {
            SceneSchedulerTaskResponse response = new SceneSchedulerTaskResponse();
            BeanUtils.copyProperties(result, response);
            responseList.add(response);
        });
        return responseList;
    }
}
