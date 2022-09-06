package io.shulie.takin.web.entrypoint.controller.pressureresource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceAppInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceAppRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceAppService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 链路资源-应用检查
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:51 PM
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/pressureResource/app")
@Api(tags = "接口: 链路资源-应用检查")
@Slf4j
public class PressureResoureAppController {
    private static Logger logger = LoggerFactory.getLogger(PressureResoureAppController.class);

    @Resource
    private PressureResourceAppService pressureResourceAppService;

    @ApiOperation("链路压测资源-修改")
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ResponseResult update(PressureResourceAppInput input) {
        pressureResourceAppService.update(input);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-应用检查列表")
    @RequestMapping(value = "/checkList", method = RequestMethod.GET)
    public ResponseResult detail(PressureResourceAppRequest request) {
        return ResponseResult.success(pressureResourceAppService.appCheckList(request));
    }
}
