package io.shulie.takin.web.entrypoint.controller.fastagentaccess;

import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants.ModuleCode;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.ErrorLogQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.ErrorLogListResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AmdbManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 异常日志接口
 * @Author ocean_wll
 * @Date 2021/8/18 3:39 下午
 */
@RestController
@RequestMapping(APIUrls.TRO_API_URL + "fast/agent/access/errorLog")
@Api(tags = "接口：异常日志接口")
public class ErrorLogController {

    @Autowired
    private AmdbManageService amdbManageService;

    @ApiOperation("|_ 异常日志列表查询")
    @GetMapping("/list")
    @AuthVerification(
        moduleCode = ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ErrorLogListResponse> agentList(ErrorLogQueryRequest queryRequest) {
        return amdbManageService.pageErrorLog(queryRequest);
    }
}
