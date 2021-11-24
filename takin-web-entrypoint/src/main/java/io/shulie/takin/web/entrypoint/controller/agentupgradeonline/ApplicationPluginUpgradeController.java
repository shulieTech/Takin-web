package io.shulie.takin.web.entrypoint.controller.agentupgradeonline;

import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.ApplicationPluginUpgradeCreateRequest;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.ApplicationPluginUpgradeRollBackRequest;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.ApplicationPluginUpgradeHistoryResponse;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用升级单(ApplicationPluginUpgrade)controller
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:05
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "plugin/upgrade")
@Api(tags = "")
public class ApplicationPluginUpgradeController {

    @Autowired
    private ApplicationPluginUpgradeService upgradeService;

    @ApiOperation("|_ 创建升级单")
    @PostMapping("/create")
    public Response create(@Validated @RequestBody ApplicationPluginUpgradeCreateRequest createRequest) {
        return upgradeService.pluginUpgrade(createRequest);
    }

    @ApiOperation("|_ 查看升级历史")
    @GetMapping("/history")
    public Response<List<ApplicationPluginUpgradeHistoryResponse>> history() {
        return upgradeService.history();
    }

    @ApiOperation("|_ 查看升级历史详情")
    @PutMapping("/history/detail")
    public Response historyDetail(@Validated @RequestBody ApplicationPluginUpgradeRollBackRequest rollBackRequest) {
        return  upgradeService.historyDetail(rollBackRequest.getUpgradeBatch());
    }

    @ApiOperation("|_ 回滚详情")
    @PutMapping("/rollback/detail")
    public Response<List<String>> rollbackDetail(@Validated @RequestBody ApplicationPluginUpgradeRollBackRequest rollBackRequest) {
        return  upgradeService.rollbackDetail(rollBackRequest.getUpgradeBatch());
    }

    @ApiOperation("|_ 回滚")
    @PutMapping("/rollback")
    public Response rollback(@Validated @RequestBody ApplicationPluginUpgradeRollBackRequest rollBackRequest) {
        return  upgradeService.rollback(rollBackRequest.getUpgradeBatch());
    }



}
