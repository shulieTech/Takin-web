package io.shulie.takin.web.entrypoint.controller.agent;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.biz.service.AgentService;
import io.shulie.takin.web.common.constant.AgentUrls;
import io.shulie.takin.web.common.constant.CacheConstants;
import io.shulie.takin.web.biz.pojo.request.agent.GetFileRequest;
import io.shulie.takin.web.biz.pojo.request.agent.PushOperateRequest;
import io.shulie.takin.web.biz.pojo.response.agent.AgentApplicationNodeProbeOperateResponse;
import io.shulie.takin.web.biz.pojo.response.agent.AgentApplicationNodeProbeOperateResultResponse;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author liuchuan
 * @date 2021/6/7 9:25 上午
 */
@Slf4j
@RestController
@RequestMapping(AgentUrls.PREFIX_URL + AgentUrls.AGENT_APPLICATION_NODE_PROBE)
@Api(tags = "接口: agent 应用节点探针相关")
public class AgentApplicationNodeController {

    @Resource
    private AgentService agentService;

    @ApiOperation("|_ 探针操作命令")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "appName", value = "应用名称", required = true,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "agentId", value = "agentId", required = true,
            dataType = "string", paramType = "query")
    })
    @GetMapping("operate")
    @Cacheable(CacheConstants.CACHE_KEY_AGENT_APPLICATION_NODE)
    public AgentApplicationNodeProbeOperateResponse getOperate(@RequestParam String appName,
        @RequestParam String agentId) {
        return agentService.getOperateResponse(appName, agentId);
    }

    @ApiOperation("|_ 探针包下载")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "appName", value = "应用名称", required = true,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "agentId", value = "agentId", required = true,
            dataType = "string", paramType = "query")
    })
    @GetMapping("file")
    public void getFile(GetFileRequest getFileRequest, HttpServletResponse response) {
        File file = agentService.getFile(getFileRequest);
        if (file == null || !file.exists()) {
            return;
        }

        CommonUtil.zeroCopyDownload(file, response);
    }

    @ApiOperation("|_ 探针操作结果")
    @PostMapping("operateResult")
    public AgentApplicationNodeProbeOperateResultResponse pushOperateResult(
        @RequestBody @Validated PushOperateRequest pushOperateRequest) {
        return agentService.updateOperateResult(pushOperateRequest);
    }

}
