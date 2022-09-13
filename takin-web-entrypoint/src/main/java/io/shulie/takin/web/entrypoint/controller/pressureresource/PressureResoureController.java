package io.shulie.takin.web.entrypoint.controller.pressureresource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceIsolateInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceQueryRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceService;
import io.shulie.takin.web.biz.service.pressureresource.common.CheckStatusEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.SourceTypeEnum;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
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
 * 链路资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:51 PM
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/pressureResource")
@Api(tags = "接口: 链路资源配置")
@Slf4j
public class PressureResoureController {
    private static Logger logger = LoggerFactory.getLogger(PressureResoureController.class);

    @Resource
    private PressureResourceService pressureResourceService;

    @ApiOperation("链路压测资源新增")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseResult create(@RequestBody PressureResourceInput input) {
        // 这里只是页面手工新增入口
        input.setType(SourceTypeEnum.MANUAL.getCode());
        input.setCheckStatus(CheckStatusEnum.CHECK_NO.getCode());
        pressureResourceService.add(input);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseResult update(@RequestBody PressureResourceInput input) {
        input.setType(SourceTypeEnum.MANUAL.getCode());
        pressureResourceService.update(input);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源删除")
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public ResponseResult del(PressureResourceInput input) {
        pressureResourceService.delete(input.getId());
        return ResponseResult.success();
    }

    @ApiOperation("设置数据隔离方式")
    @RequestMapping(value = "/updateIsolate", method = RequestMethod.POST)
    public ResponseResult updateIsolate(@RequestBody PressureResourceIsolateInput input) {
        pressureResourceService.updateIsolate(input);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult list(PressureResourceQueryRequest request) {
        return ResponseResult.success(pressureResourceService.list(request));
    }

    @ApiOperation("链路压测资源查询-详情-编辑链路")
    @RequestMapping(value = "/detailList", method = RequestMethod.GET)
    public ResponseResult detailList(PressureResourceQueryRequest request) {
        return ResponseResult.success(pressureResourceService.detail(request));
    }

    @ApiOperation("链路压测资源查询-汇总数据-应用")
    @RequestMapping(value = "/appInfo", method = RequestMethod.GET)
    public ResponseResult appInfo(PressureResourceQueryRequest request) {
        return ResponseResult.success(pressureResourceService.appInfo(request.getId()));
    }

    @ApiOperation("链路压测资源查询-汇总数据-数据源")
    @RequestMapping(value = "/dsInfo", method = RequestMethod.GET)
    public ResponseResult dsInfo(PressureResourceQueryRequest request) {
        return ResponseResult.success(pressureResourceService.dsInfo(request.getId()));
    }

    //@ApiModelProperty("类型(1-应用检查，2-影子隔离 3-远程调用 )")
    //@ApiModelProperty("状态(0-未开始 1-进行中 2-完成)")
    @ApiOperation("链路压测资源查询-汇总数据-进度")
    @RequestMapping(value = "/progress", method = RequestMethod.GET)
    public ResponseResult progress(PressureResourceQueryRequest request) {
        return ResponseResult.success(pressureResourceService.progress(request.getId()));
    }
}
