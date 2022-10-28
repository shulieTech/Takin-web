package io.shulie.takin.web.entrypoint.controller.agent;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.entity.domain.dto.ApplicationSwitchStatusDTO;
import com.pamirs.takin.entity.domain.dto.config.WhiteListSwitchDTO;
import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsAgentVO;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsServerVO;
import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.micrometer.core.annotation.Timed;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.agent.vo.ShadowConsumerVO;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.pojo.output.application.ShadowServerConfigurationOutput;
import io.shulie.takin.web.biz.service.ApplicationPluginsConfigService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.AgentUrls;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AgentUrls.PREFIX_URL)
@Api(tags = "Agent应用配置")
@Slf4j
@Timed(histogram = true, percentiles = {0.9, 0.95})
public class AgentPullController {

    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @Autowired
    private ApplicationPluginsConfigService configService;

    @ApiOperation("拉取影子库表配置")
    @GetMapping(value = AgentUrls.SHADOW_DB_TABLE_URL)
    public Response<List<DsAgentVO>> getConfigs(@RequestParam("appName") String appName) {
        return Response.success(agentConfigCacheManager.getShadowDb(appName));
    }

    @ApiOperation("拉取影子消费者配置")
    @GetMapping(value = AgentUrls.SHADOW_SHADOW_CONSUMER_URL)
    public Response<List<ShadowConsumerVO>> getShadowConsumer(@RequestParam("appName") String appName) {
        return Response.success(agentConfigCacheManager.getShadowConsumer(appName));
    }

    @ApiOperation("拉取Es影子索引配置")
    @GetMapping(value = AgentUrls.SHADOW_ES_SERVER_URL)
    public Response<List<DsServerVO>> getShadowEsServer(@RequestParam("appName") String appName) {
        return Response.success(agentConfigCacheManager.getShadowEsServers(appName));
    }

    @ApiOperation("拉取kafka影子索引配置")
    @GetMapping(value = AgentUrls.SHADOW_KAFKA_CLUSTER_URL)
    public Response<List<DsServerVO>> getShadowKafkaCluster(@RequestParam("appName") String appName) {
        return Response.success(agentConfigCacheManager.getShadowKafkaCluster(appName));
    }

    @ApiOperation("拉取Hbase影子配置")
    @GetMapping(value = AgentUrls.SHADOW_HBASE_SERVER_URL)
    public Response<List<DsServerVO>> getShadowHbaseServer(@RequestParam("appName") String appName) {
        return Response.success(agentConfigCacheManager.getShadowHbase(appName));
    }

    @ApiOperation(value = "拉取Job配置")
    @GetMapping(value = AgentUrls.TAKIN_SHADOW_JOB_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<TShadowJobConfig>> queryByAppName(
        @RequestParam(value = "appName", defaultValue = "") String appName) {
        return Response.success(agentConfigCacheManager.getShadowJobs(appName));
    }

    @ApiOperation("拉取影子库server配置")
    @GetMapping(value = AgentUrls.SHADOW_SERVER_URL)
    public Response<List<ShadowServerConfigurationOutput>> getShadowServerConfigs(
        @RequestParam("appName") String appName) {
        return Response.success(agentConfigCacheManager.getShadowServer(appName));
    }

    @GetMapping(value = AgentUrls.GUARD_URL)
    @ApiOperation("挡板列表查询接口")
    public Response<List<LinkGuardVo>> getGuardList(
        @ApiParam(name = "applicationName", value = "系统名字")
        @RequestParam(value = "applicationName") String applicationName) {
        return Response.success(agentConfigCacheManager.getGuards(applicationName));
    }

    @GetMapping(value = AgentUrls.APP_WHITE_LIST_SWITCH_STATUS)
    @ApiOperation(value = "查看全局白名单开关")
    public Response<WhiteListSwitchDTO> getWhiteListSwitch() {
        WhiteListSwitchDTO switchDTO = new WhiteListSwitchDTO();
        switchDTO.setConfigCode(ConfigConstants.WHITE_LIST_SWITCH);
        switchDTO.setSwitchFlagFix(agentConfigCacheManager.getAllowListSwitch());
        return Response.success(switchDTO);
    }

    @ApiOperation("获取应用压测开关状态接口")
    @GetMapping(value = AgentUrls.APP_PRESSURE_SWITCH_STATUS)
    public Response<ApplicationSwitchStatusDTO> agentAppSwitchInfo() {
        return Response.success(agentConfigCacheManager.getPressureSwitch());
    }

    @GetMapping(AgentUrls.APPLICATION_PLUGINS_CONFIG_REDIS)
    @ApiOperation("影子key过期时间查询")
    public Response<Object> queryByAppName(
        @ApiParam(name = "applicationName", value = "系统名字") String applicationName,
        @ApiParam(name = "configKey", value = "配置项key") String configKey
    ) {
        return Response.success(agentConfigCacheManager.getAppPluginConfig(CommonUtil.generateRedisKey(applicationName,configKey)));
    }

    /**
     * 新版白名单 黑名单 mock数据
     *
     * @return
     */
    @ApiOperation(value = "远程调用接口获取")
    @RequestMapping(value = AgentUrls.TAKIN_REMOTE_CALL_URL, method = RequestMethod.GET)
    public ResponseResult<AgentRemoteCallVO> getRemoteCallConfig(
        @ApiParam(name = "appName", value = "应用名") @RequestParam("appName") String appName) {
        return ResponseResult.success(agentConfigCacheManager.getRemoteCallConfig(appName));
    }

    /**
     * agent1.0的白名单获取方式，兼容当前的白名单方式
     * @param appName -
     * @param userAppKey -
     * @return
     */
    @ApiOperation(value = "白名单获取")
    @RequestMapping(value = "/confcenter/wbmnt/query/{userAppKey}", method = RequestMethod.GET)
    public ResponseResult<AgentRemoteCallVO> wbmntQuery(
            @ApiParam(name = "appName", value = "应用名") @RequestParam("appName") String appName,
            @PathParam("userAppKey") String userAppKey) {
        return ResponseResult.success(agentConfigCacheManager.getRemoteCallConfig(appName));
    }

}
