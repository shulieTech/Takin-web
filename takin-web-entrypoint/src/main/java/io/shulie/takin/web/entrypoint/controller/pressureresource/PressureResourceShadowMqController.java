package io.shulie.takin.web.entrypoint.controller.pressureresource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMqConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMqConsumerQueryRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceMqConsumerService;
import io.shulie.takin.web.biz.service.pressureresource.common.ModuleEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.SourceTypeEnum;
import io.shulie.takin.web.biz.service.pressureresource.vo.CommandTaskVo;
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
 * @author xingchen
 * @description: TODO
 * @date 2022/10/11 9:46 AM
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/pressureResource/mqconsumer")
@Api(tags = "接口: 关联表")
@Slf4j
public class PressureResourceShadowMqController {
    @Resource
    private PressureResourceMqConsumerService pressureResourceMqConsumerService;

    @Resource
    private PressureResourceCommonService pressureResourceCommonService;

    @ApiOperation("链路压测资源-新增影子消费者")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseResult create(@RequestBody PressureResourceMqConsumerCreateInput request) {
        request.setType(SourceTypeEnum.MANUAL.getCode());
        pressureResourceMqConsumerService.create(request);

        CommandTaskVo taskVo = new CommandTaskVo();
        taskVo.setResourceId(request.getResourceId());
        taskVo.setModule(ModuleEnum.MQ.getCode());
        pressureResourceCommonService.pushRedisCommand(taskVo);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-修改影子消费者")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseResult update(@RequestBody PressureResourceMqConsumerCreateInput request) {
        pressureResourceMqConsumerService.update(request);

        CommandTaskVo taskVo = new CommandTaskVo();
        taskVo.setResourceId(request.getResourceId());
        taskVo.setModule(ModuleEnum.MQ.getCode());
        pressureResourceCommonService.pushRedisCommand(taskVo);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-修改影子消费者-开启/关闭消费")
    @RequestMapping(value = "/consumerTag", method = RequestMethod.POST)
    public ResponseResult consumerTag(@RequestBody PressureResourceMqConsumerCreateInput request) {
        pressureResourceMqConsumerService.processConsumerTag(request);
        return ResponseResult.success();
    }

    @ApiOperation("链路压测资源-分页")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult list(PressureResourceMqConsumerQueryRequest request) {
        return ResponseResult.success(pressureResourceMqConsumerService.list(request));
    }

    @ApiOperation("链路压测资源-删除")
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public ResponseResult del(PressureResourceMqConsumerCreateInput input) {
        pressureResourceMqConsumerService.delete(input.getId());
        return ResponseResult.success();
    }
}
