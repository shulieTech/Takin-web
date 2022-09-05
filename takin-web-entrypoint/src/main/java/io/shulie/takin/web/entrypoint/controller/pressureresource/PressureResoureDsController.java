package io.shulie.takin.web.entrypoint.controller.pressureresource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.pressureresource.*;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceDsService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceService;
import io.shulie.takin.web.biz.service.pressureresource.common.ResourceTypeEnum;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 链路资源配置-数据源隔离
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:51 PM
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/pressureResource/ds")
@Api(tags = "接口: 数据源隔离")
@Slf4j
public class PressureResoureDsController {
    private static Logger logger = LoggerFactory.getLogger(PressureResoureDsController.class);

    @Resource
    private PressureResourceDsService pressureResourceDsService;

    @ApiOperation("链路压测资源新增")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseResult create(@RequestBody PressureResourceRelationDsInput input) {
        pressureResourceDsService.add(input);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-数据源视图")
    @RequestMapping(value = "/listByDs", method = RequestMethod.GET)
    public ResponseResult listByDs(PressureResourceRelationDsRequest request) {
        return ResponseResult.success(pressureResourceDsService.listByDs(request));
    }

    @ApiOperation("链路压测资源-应用视图")
    @RequestMapping(value = "/listByApp", method = RequestMethod.GET)
    public ResponseResult listByApp(PressureResourceRelationDsRequest request) {
        return ResponseResult.success(pressureResourceDsService.listByApp(request));
    }
}