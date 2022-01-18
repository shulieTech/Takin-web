package io.shulie.takin.web.entrypoint.controller.leakverify;

import java.util.Set;

import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskRunWithoutSaveRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStartRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStopRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanxx
 * @date 2021/1/8 2:45 下午
 */
@Slf4j
@RestController
@RequestMapping("/api/leak/verify")
@Api(tags = "漏数验证任务管理")
public class LeakVerifyTaskController {

    @Autowired
    private VerifyTaskService verifyTaskService;


    @PostMapping("/start")
    @ApiOperation(value = "开始周期性验证任务")
    public void start(@RequestBody LeakVerifyTaskStartRequest startRequest) {
        verifyTaskService.start(startRequest);
    }

    @PostMapping("/stop")
    @ApiOperation(value = "停止周期性验证任务")
    public void stop(@RequestBody LeakVerifyTaskStopRequest stopRequest) {
        verifyTaskService.stop(stopRequest);
    }

    @PostMapping("/run")
    @ApiOperation(value = "运行单次验证任务")
    public LeakVerifyTaskResultResponse run(@RequestBody LeakVerifyTaskRunWithoutSaveRequest runRequest) {
        return verifyTaskService.runWithoutResultSave(runRequest);
    }

    @GetMapping("/query")
    @ApiOperation(value = "查看所有验证任务")
    public Set<String> queryVerifyTask() {
        return verifyTaskService.queryVerifyTask();
    }
}
