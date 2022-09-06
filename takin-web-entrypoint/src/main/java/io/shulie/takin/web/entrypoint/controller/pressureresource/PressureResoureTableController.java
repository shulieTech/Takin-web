package io.shulie.takin.web.entrypoint.controller.pressureresource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationTableInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationTableRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceTableService;
import io.shulie.takin.web.biz.service.pressureresource.common.SourceTypeEnum;
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
 * 链路资源配置-影子表配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:51 PM
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/pressureResource/table")
@Api(tags = "接口: 数据源隔离")
@Slf4j
public class PressureResoureTableController {
    @Resource
    private PressureResourceTableService pressureResourceTableService;

    @ApiOperation("链路压测资源-影子表新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseResult save(@RequestBody PressureResourceRelationTableInput input) {
        input.setType(SourceTypeEnum.MANUAL.getCode());
        pressureResourceTableService.save(input);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-影子表列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult list(PressureResourceRelationTableRequest request) {
        return ResponseResult.success(pressureResourceTableService.pageList(request));
    }

    @ApiOperation("链路压测资源-影子表-修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseResult update(@RequestBody PressureResourceRelationTableInput input) {
        pressureResourceTableService.update(input);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-影子表-删除")
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public ResponseResult del(PressureResourceRelationTableInput input) {
        pressureResourceTableService.delete(input.getId());
        return ResponseResult.success();
    }
}
