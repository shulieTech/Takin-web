package io.shulie.takin.web.entrypoint.controller.v2.ds;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/8/27 1:56 下午
 */
@Slf4j
@RestController("v2.application.ds")
@RequestMapping(APIUrls.TAKIN_API_URL+"v2")
@Api(tags = "接口-v2:影子库表管理", value = "影子库表管理")
public class DsController {

    @Autowired
    private DsService dsService;

    @ApiOperation("查询影子库表列表")
    @GetMapping("link/ds/manage")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Response dsQuery(@RequestParam(value = "applicationId", required = true) Long applicationId) {
        return dsService.dsQueryV2(applicationId);
    }


    @ApiOperation("查询影子库表详情")
    @GetMapping("link/ds/manage/detail")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Response dsQueryDetail(@RequestParam(value = "applicationId", required = true) Long applicationId,
                                  @RequestParam(value = "recordId", required = true) Long recordId ,
                                  @RequestParam(value = "middlewareType",required = true ) String middlewareType,
                                  @RequestParam(value = "agentSourceType",required = true ) String agentSourceType) {
        return dsService.dsQueryDetailV2(applicationId,recordId,middlewareType,agentSourceType);
    }

    @ApiOperation("获取影子库表隔离方案")
    @GetMapping("link/ds/manage/programme")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> dsQueryDetail(@RequestParam(value = "plugName", required = true) String plugName,
                                        @RequestParam(value = "middlewareType",required = true ) String middlewareType) {
        return dsService.queryDsType(middlewareType,plugName);
    }


    @ApiOperation("编辑影子配置")
    @GetMapping("link/ds/config")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.UPDATE
    )
    public Response dsUpdateConfig(@RequestBody @Validated ApplicationDsUpdateInputV2 updateRequestV2) {
        return dsService.dsUpdateConfig(updateRequestV2);
    }

}
