package io.shulie.takin.web.entrypoint.controller.interfaceperformance;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDebugRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceResultCreateInput;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceConfigService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceDebugService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceResultService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:33 下午
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/interfaceperformance/debug")
@Api(tags = "接口: 接口压测")
@Slf4j
public class PerformanceDebugController {
    @Resource
    private PerformanceConfigService performanceConfigService;

    @Resource
    private PerformanceDebugService performanceDebugService;

    @Resource
    private PerformanceResultService performanceResultService;

    @ApiOperation("调试,如果未设置调试Id,则默认是更新以后开启调试")
    @RequestMapping(value = "/startAndSave", method = RequestMethod.POST)
    public ResponseResult startAndSave(@RequestBody PerformanceDebugRequest request) {
        // 配置Id为空,则需要保存以后，再做调试功能
        if (request.getId() == null) {
            PerformanceConfigCreateInput input = new PerformanceConfigCreateInput();
            BeanUtils.copyProperties(request, input);
            Long configId = performanceConfigService.add(input);
            request.setId(configId);
        } else {
            // ConfigId不为空,保存下当前配置信息
            PerformanceConfigCreateInput input = new PerformanceConfigCreateInput();
            BeanUtils.copyProperties(request, input);
            performanceConfigService.update(input);
        }
        performanceDebugService.debug(request);
        // 返回接口压测Id到前端
        return ResponseResult.success(request.getId());
    }

    @ApiOperation("调试")
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseResult start(@RequestBody PerformanceDebugRequest request) {
        performanceDebugService.debug(request);
        return ResponseResult.success();
    }

    @ApiOperation("获取调试结果")
    @RequestMapping(value = "/result", method = RequestMethod.POST)
    public ResponseResult result(@RequestBody PerformanceResultCreateInput input) {
        return ResponseResult.success(performanceResultService.pageResult(input));
    }
}
