package io.shulie.takin.web.entrypoint.controller.pressureresource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMockInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateRemoteCallRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceRemoteCallService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 链路资源配置-远程调用
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:51 PM
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/pressureResource/remotecall")
@Api(tags = "接口: 数据源隔离")
@Slf4j
public class PressureResoureRemouteCallController {
    @Resource
    private PressureResourceRemoteCallService pressureResourceRemoteCallService;

    @ApiOperation("链路压测资源-远程调用列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult list(PressureResourceRelateRemoteCallRequest request) {
        return ResponseResult.success(pressureResourceRemoteCallService.pageList(request));
    }

    @ApiOperation("链路压测资源-远程调用-update")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseResult mock(@RequestBody PressureResourceMockInput input) {
        pressureResourceRemoteCallService.update(input);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-远程调用-平均响应时间")
    @RequestMapping(value = "/avgRt", method = RequestMethod.GET)
    public ResponseResult avgRt(PressureResourceRelateRemoteCallRequest input) {
        return ResponseResult.success(pressureResourceRemoteCallService.getServiceAvgRt(input.getId()));
    }

}
