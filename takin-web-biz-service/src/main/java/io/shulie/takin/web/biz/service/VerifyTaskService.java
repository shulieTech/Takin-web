package io.shulie.takin.web.biz.service;

import java.util.Map;
import java.util.Set;

import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskJobParameter;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskRunWithSaveRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskRunWithoutSaveRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStartRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStopRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;

/**
 * @author fanxx
 * @date 2021/1/5 3:05 下午
 */
public interface VerifyTaskService {
    /**
     * 定时任务 租户隔离
     * @param ext
     */
    void showdownVerifyTask();

    void start(LeakVerifyTaskStartRequest startRequest);

    void stop(LeakVerifyTaskStopRequest stopRequest);

    LeakVerifyTaskResultResponse runWithoutResultSave(LeakVerifyTaskRunWithoutSaveRequest runRequest);

    LeakVerifyTaskResultResponse runWithResultSave(LeakVerifyTaskRunWithSaveRequest runRequest);

    void saveVerifyResult(LeakVerifyTaskJobParameter jobParameter, Map<Integer, Integer> resultMap);

    Set<String> queryVerifyTask();
}
