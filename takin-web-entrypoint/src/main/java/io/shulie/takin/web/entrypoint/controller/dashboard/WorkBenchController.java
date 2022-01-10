package io.shulie.takin.web.entrypoint.controller.dashboard;

import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.response.dashboard.QuickAccessResponse;
import io.shulie.takin.web.biz.pojo.response.dashboard.UserWorkBenchResponse;
import io.shulie.takin.web.biz.service.dashboard.QuickAccessService;
import io.shulie.takin.web.biz.service.dashboard.WorkBenchService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mubai
 * @date 2020-06-28 17:56
 */

@RestController
@RequestMapping("api/workbench")
@Api(tags = "WorkBenchController", value = "工作台接口")
@Slf4j
public class WorkBenchController {
    @Resource
    private WorkBenchService workBenchService;
    @Resource
    private QuickAccessService quickAccessService;

    @GetMapping("user")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.DASHBOARD_APPMANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public UserWorkBenchResponse workBench() {
        try {
            return workBenchService.getWorkBench();
        } catch (Exception e) {
            log.error("获取工作台数据失败", e);
            throw e;
        }
    }

    @GetMapping("user/access")
    public List<QuickAccessResponse> quickAccess() {
        try {
            return quickAccessService.list();
        } catch (Exception e) {
            log.error("获取快捷入口失败", e);
            throw e;
        }
    }
}
