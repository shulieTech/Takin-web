package io.shulie.takin.web.biz.service.scenemanage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneSchedulerTaskResponse;
import io.shulie.takin.web.data.dao.scenemanage.SceneSchedulerTaskDao;
import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskInsertParam;
import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskQueryParam;
import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskUpdateParam;
import io.shulie.takin.web.data.result.scenemanage.SceneSchedulerTaskResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-12-01 10:36
 */
@Slf4j
@Service
public class SceneSchedulerTaskServiceImpl implements SceneSchedulerTaskService {

    @Autowired
    private SceneSchedulerTaskDao sceneSchedulerTaskDao;

    @Autowired
    private SceneTaskService sceneTaskService;

    @Override
    public Long insert(SceneSchedulerTaskCreateRequest request) {
        if (request == null) {
            return null;
        }
        if (request.getSceneId() == null) {
            throw new TakinWebException(ExceptionCode.SCENE_SCHEDULER_TASK_SCENE_ID_VALID_ERROR,
                "sceneId can not be null");
        }
        verificationScheduleTime(request.getExecuteTime());
        SceneSchedulerTaskResult result = sceneSchedulerTaskDao.selectBySceneId(request.getSceneId());
        if (result != null) {
            return result.getId();
        }
        SceneSchedulerTaskInsertParam insertParam = new SceneSchedulerTaskInsertParam();
        BeanUtils.copyProperties(request, insertParam);
        return sceneSchedulerTaskDao.create(insertParam);

    }

    /**
     * 定时压测时间必须在当前时间1分钟之后
     */
    public void verificationScheduleTime(Date time) {
        //if (StringUtils.isBlank(time)) {
        //    if (StringUtils.isBlank(time)) {
        //        throw new TakinWebException(ExceptionCode.SCENE_SCHEDULER_TASK_EXECUTE_TIME_VALID_ERROR,
        //            "executeTime can not be null");
        //    }
        //}

        // Date date = DateUtils.strToDate(time, DateUtils.FORMATE_YMDHM);
        if (time == null) {
            throw new TakinWebException(ExceptionCode.SCENE_SCHEDULER_TASK_EXECUTE_TIME_VALID_ERROR,
                "executeTime can not be null");
        }
        if (time.getTime() - System.currentTimeMillis() < 1000 * 60) {
            throw new TakinWebException(ExceptionCode.SCENE_SCHEDULER_TASK_EXECUTE_TIME_VALID_ERROR, "定时执行时间需要大于当前时间1分钟");
        }
    }

    @Override
    public void delete(Long id) {
        sceneSchedulerTaskDao.delete(id);
    }

    @Override
    public void update(SceneSchedulerTaskUpdateRequest updateRequest, Boolean needVerify) {
        if (needVerify != null && needVerify == true) {
            verificationScheduleTime(updateRequest.getExecuteTime());
        }
        SceneSchedulerTaskUpdateParam updateParam = new SceneSchedulerTaskUpdateParam();
        BeanUtils.copyProperties(updateRequest, updateParam);
        sceneSchedulerTaskDao.update(updateParam);
    }

    @Override
    public SceneSchedulerTaskResponse selectBySceneId(Long sceneId) {
        SceneSchedulerTaskResult result = sceneSchedulerTaskDao.selectBySceneId(sceneId);
        if (result == null) {
            return null;
        }
        SceneSchedulerTaskResponse response = new SceneSchedulerTaskResponse();
        BeanUtils.copyProperties(result, response);
        return response;
    }

    @Override
    public void deleteBySceneId(Long sceneId) {
        if (sceneId == null) {
            return;
        }
        sceneSchedulerTaskDao.deleteBySceneId(sceneId);
    }

    @Override
    public List<SceneSchedulerTaskResponse> selectBySceneIds(List<Long> sceneIds) {
        if (CollectionUtils.isEmpty(sceneIds)) {
            return Lists.newArrayList();
        }
        List<SceneSchedulerTaskResult> resultList = sceneSchedulerTaskDao.selectBySceneIds(sceneIds);
        return result2RespList(resultList);
    }

    @Override
    public List<SceneSchedulerTaskResponse> selectByExample(SceneSchedulerTaskQueryRequest request) {
        if (request == null) {
            return Lists.newArrayList();
        }
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
        if (CollectionUtils.isEmpty(responseList)) {
            return;
        }
        for (SceneSchedulerTaskResponse scheduler : responseList) {
            if (scheduler.getExecuteTime() == null || scheduler.getIsExecuted() == null
                || scheduler.getIsExecuted() != 0) {
                continue;
            }
            Date dbDate = scheduler.getExecuteTime();
            Date now = new Date();
            if (dbDate.before(now)) {
                //执行
                SceneActionParam startParam = new SceneActionParam();
                startParam.setSceneId(scheduler.getSceneId());
                //startParam.setUid(scheduler.getUserId());

                new Thread(() -> {
                    try {
                        sceneTaskService.startTask(startParam);
                    } catch (Exception e) {
                        log.error("执行定时压测任务失败...", e);
                    } finally {
                        SceneSchedulerTaskUpdateRequest updateRequest = new SceneSchedulerTaskUpdateRequest();
                        updateRequest.setId(scheduler.getId());
                        updateRequest.setIsExecuted(2);
                        updateRequest.setIsDeleted(true);
                        this.update(updateRequest, false);
                    }
                }).start();
            }
        }

    }

    List<SceneSchedulerTaskResponse> result2RespList(List<SceneSchedulerTaskResult> resultList) {
        if (CollectionUtils.isEmpty(resultList)) {
            Lists.newArrayList();
        }
        List<SceneSchedulerTaskResponse> responseList = new ArrayList<>();
        resultList.stream().forEach(result -> {
            SceneSchedulerTaskResponse response = new SceneSchedulerTaskResponse();
            BeanUtils.copyProperties(result, response);
            responseList.add(response);
        });
        return responseList;
    }

}
