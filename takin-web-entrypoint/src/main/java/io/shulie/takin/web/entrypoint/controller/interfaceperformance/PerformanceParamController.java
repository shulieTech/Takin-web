package io.shulie.takin.web.entrypoint.controller.interfaceperformance;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamDetailRequest;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceParamService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/20 10:09 上午
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/interfaceperformance/param")
@Api(tags = "接口: 接口压测")
@Slf4j
public class PerformanceParamController {
    @Autowired
    private PerformanceParamService performanceParamService;

    @ApiOperation("保存接口压测参数")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseResult save(@RequestBody PerformanceDataFileRequest request) {
        performanceParamService.updatePerformanceData_ext(request);
        return ResponseResult.success();
    }

    @ApiOperation("获取参数详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResponseResult detail(PerformanceParamDetailRequest request) {
        return ResponseResult.success(performanceParamService.detail(request));
    }

    @ApiOperation("获取文件内容详情")
    @RequestMapping(value = "/fileDataDetail", method = RequestMethod.POST)
    public ResponseResult fileDataDetail(@RequestBody PerformanceParamDetailRequest request) {
        return ResponseResult.success(performanceParamService.fileContentDetail(request));
    }
}
