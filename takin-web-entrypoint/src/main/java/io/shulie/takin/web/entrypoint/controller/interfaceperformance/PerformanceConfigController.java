package io.shulie.takin.web.entrypoint.controller.interfaceperformance;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigQueryRequest;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceConfigService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:33 下午
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/interfaceperformance")
@Api(tags = "接口: 接口压测")
@Slf4j
public class PerformanceConfigController {
    @Autowired
    private PerformanceConfigService performanceConfigService;

    @ApiOperation("单接口压测新增")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseResult create(@RequestBody PerformanceConfigCreateInput input) {
        performanceConfigService.add(input);
        return ResponseResult.success();
    }

    @ApiOperation("单接口压测新增")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseResult update(@RequestBody PerformanceConfigCreateInput input) {
        performanceConfigService.update(input);
        return ResponseResult.success();
    }

    @ApiOperation("单接口压测删除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseResult delete(@RequestBody PerformanceConfigQueryRequest request) {
        if (request == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_PARAM_ERROR, "参数为空");
        }
        performanceConfigService.delete(request.getId());
        return ResponseResult.success();
    }

    @ApiOperation("单接口压测-压测场景查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResponseResult query(@RequestBody PerformanceConfigQueryRequest request) {
        return ResponseResult.success(performanceConfigService.query(request));
    }
}
