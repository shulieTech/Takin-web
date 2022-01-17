package io.shulie.takin.web.entrypoint.controller.v2.consumer;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import com.github.pagehelper.util.StringUtil;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerQueryInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowConsumerOutput;
import io.shulie.takin.web.biz.service.ShadowConsumerService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 南风
 * @date 2021/8/31 7:48 下午
 */
@RestController("v2.application.consumer")
@RequestMapping(ApiUrls.TAKIN_API_URL + "v2")
@Api(tags = "接口-v2:影子消费者管理", value = "影子消费者")
public class ApplicationMqConsumerController {

    @Resource
    private ShadowConsumerService shadowConsumerService;

    @GetMapping("/consumers/type")
    @ApiOperation("获取支持的MQ类型")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> queryMqSupportType() {
        return shadowConsumerService.queryMqSupportType();
    }

    @GetMapping("/consumers/type/programme")
    @ApiOperation("获取MQ的影子方案")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> queryMqSupportProgramme(@Validated @NotNull @RequestParam("engName") String engName) {
        return shadowConsumerService.queryMqSupportProgramme(engName);
    }

    @PutMapping("/consumers/update")
    @ApiOperation("修改影子消费者")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_CONSUMER,
        logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_CONSUMER_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public void updateMqConsumers(@ApiParam(required = true) @Validated @RequestBody ShadowConsumerUpdateInput request) {
        shadowConsumerService.updateMqConsumersV2(request);
    }

    @PostMapping("/consumers/create")
    @ApiOperation("创建影子消费者")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHADOW_CONSUMER,
        logMsgKey = BizOpConstants.Message.MESSAGE_SHADOW_CONSUMER_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public void createMqConsumers(@ApiParam(required = true) @Validated @RequestBody ShadowConsumerCreateInput request) {
        request.setTopicGroup(StringUtil.isEmpty(request.getTopicGroup()) ?"":request.getTopicGroup().trim());
        shadowConsumerService.createMqConsumersV2(request, true);
    }

    @GetMapping("/consumers/page")
    @ApiOperation("分页查询影子消费者")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ShadowConsumerOutput> pageMqConsumers(@Validated ShadowConsumerQueryInputV2 request) {
        return shadowConsumerService.pageMqConsumersV2(request);
    }

}
