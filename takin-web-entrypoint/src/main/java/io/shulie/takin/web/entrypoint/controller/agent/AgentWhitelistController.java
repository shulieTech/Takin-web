package io.shulie.takin.web.entrypoint.controller.agent;

import io.shulie.takin.web.biz.service.AgentService;
import io.shulie.takin.web.common.constant.AgentUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * agent 白名单相关控制层
 *
 * @author liuchuan
 * @date 2021/10/26 2:42 下午
 */
@RestController
@RequestMapping(AgentUrls.PREFIX + "/whitelist/")
@Api(tags = "接口: agent 白名单相关")
public class AgentWhitelistController {

    @Autowired
    private AgentService agentService;

    @ApiOperation("|_ 是否更新白名单内容")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "tenantAppKey", value = "租户 key", required = true,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "sign", value = "内容标志", required = true,
            dataType = "string", paramType = "query")
    })
    @GetMapping("isUpdate")
    public Integer getOperate(@RequestParam String tenantAppKey, @RequestParam String sign) {
        return 0;
    }

}
