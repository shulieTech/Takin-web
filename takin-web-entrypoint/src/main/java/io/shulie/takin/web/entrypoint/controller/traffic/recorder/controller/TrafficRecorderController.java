package io.shulie.takin.web.entrypoint.controller.traffic.recorder.controller;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.request.TrafficRecorderExportCsvRequest;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.request.TrafficRecorderQueryRequest;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.request.TrafficRecorderTaskConfigRequest;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.response.TrafficRecorderResponse;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.service.TrafficRecorderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenxingxing
 * @date 2023/2/7 10:57 上午
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "/traffic/recorder")
@Api(tags = "接口: 流量录制接口")
@Slf4j
public class TrafficRecorderController {

    @Resource
    TrafficRecorderService trafficRecorderService;

    @GetMapping("/list")
    @ApiOperation("分页查询流量记录")
    public PagingList<TrafficRecorderResponse> query(TrafficRecorderQueryRequest request) {
        return trafficRecorderService.queryTrafficRecorder(request);
    }


    @GetMapping("/queryAppList")
    @ApiOperation("分页查询流量记录")
    public ResponseResult<List<String>> queryAppList() {
        List<String> appList = new ArrayList<>();
        appList.add("servicebusiness_pro");
        appList.add("querybusiness_pro");
        appList.add("importantbusiness_pro");
        appList.add("call_esframework");
        appList.add("transactbusiness_pro");
        appList.add("logservice_pro");
        return ResponseResult.success(appList);
    }

    @GetMapping("/queryServiceList")
    @ApiOperation("分页查询流量记录")
    public ResponseResult<List<String>> queryServiceList(@RequestParam String appName) {
        List<String> serviceList = new ArrayList<>();
        serviceList.add("/servicebusiness/tingYun/queryTingYuPlatformSwitch");
        serviceList.add("/servicequerybusiness/operationservice/theFirstToRecommendForYou");
        serviceList.add("/serviceimportantbusiness/query/getPhoneByDetailTip.htm");
        serviceList.add("/serviceimportantbusiness/home/resourceQueryNew");
        serviceList.add("/servicetransactbusiness/query/flow/getCarefullyChosen");
        serviceList.add("/servicebusiness/onlineService/jumpNew");
        return ResponseResult.success(serviceList);
    }

    @GetMapping("/createTask")
    @ApiOperation("创建任务")
    public ResponseResult<Long> createTask(TrafficRecorderTaskConfigRequest request) {

        //写入配置表
        Long taskId = 123L;

        return ResponseResult.success(taskId);
    }


    @GetMapping("/checkTaskStatus")
    @ApiOperation("查询任务状态")
    public ResponseResult<Long> checkTaskStatus(@RequestParam Long taskId) {

        return ResponseResult.success();
    }

    @GetMapping("/exportCsv")
    @ApiOperation("导出csv")
    public ResponseResult<Long> exportCsv(TrafficRecorderExportCsvRequest request) {

        return ResponseResult.success();
    }



}
