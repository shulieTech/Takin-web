package io.shulie.takin.web.entrypoint.controller.application;

import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerUpdateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumersOperateInput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowConsumerOutput;
import io.shulie.takin.web.biz.service.ShadowConsumerService;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
import io.shulie.takin.web.biz.pojo.request.application.ShadowConsumerDeleteRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shiyajian
 * create: 2021-02-04
 */
@RestController
@RequestMapping("/api/consumers")
@Api(tags = "接口:影子消费者管理", value = "影子消费者")
public class ApplicationMqConsumerController {

    @Autowired
    private ShadowConsumerService shadowConsumerService;

    @GetMapping("/get")
    @ApiOperation("查看影子消费者")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public ShadowConsumerOutput getMqConsumerById(@Validated @NotNull @RequestParam("id") Long id) {
        return shadowConsumerService.getMqConsumerById(id);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询影子消费者")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ShadowConsumerOutput> pageMqConsumers(@Validated ShadowConsumerQueryInput request) {
        return shadowConsumerService.pageMqConsumers(request);
    }

    @PostMapping("/create")
    @ApiOperation("创建影子消费者")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_CONSUMER,
        logMsgKey = Message.MESSAGE_SHADOW_CONSUMER_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public void createMqConsumers(@Validated @RequestBody ShadowConsumerCreateInput request) {
        shadowConsumerService.createMqConsumers(request);
    }

    @PutMapping("/update")
    @ApiOperation("修改影子消费者")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_CONSUMER,
        logMsgKey = Message.MESSAGE_SHADOW_CONSUMER_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public void updateMqConsumers(@Validated @RequestBody ShadowConsumerUpdateInput request) {
        shadowConsumerService.updateMqConsumers(request);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除影子消费者")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_CONSUMER,
        logMsgKey = Message.MESSAGE_SHADOW_CONSUMER_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public void deleteMqConsumers(@Validated @RequestBody ShadowConsumerDeleteRequest request) {
        shadowConsumerService.deleteMqConsumers(request.getIds());
    }

    @PostMapping("/operate")
    @ApiOperation("（加入/取消）影子消费者")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_CONSUMER,
        logMsgKey = Message.MESSAGE_SHADOW_CONSUMER_ADD_REMOVE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public void operateMqConsumers(@Validated @RequestBody ShadowConsumersOperateInput request) {
        shadowConsumerService.operateMqConsumers(request);
    }

}
