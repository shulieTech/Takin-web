package io.shulie.takin.web.entrypoint.controller.v2.consumer;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.service.ShadowConsumerService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/8/31 7:48 下午
 */
@RestController("v2.application.consumer")
@RequestMapping(APIUrls.TAKIN_API_URL+"v2")
@Api(tags = "接口-v2:影子消费者管理", value = "影子消费者")
public class ApplicationMqConsumerController {

    @Autowired
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

}
