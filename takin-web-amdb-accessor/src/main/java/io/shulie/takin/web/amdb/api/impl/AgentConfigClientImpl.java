package io.shulie.takin.web.amdb.api.impl;

import java.util.List;


import cn.hutool.json.JSONUtil;
import io.shulie.amdb.common.dto.agent.AgentConfigDTO;
import io.shulie.amdb.common.dto.agent.AgentStatInfoDTO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.AgentConfigClient;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.bean.query.fastagentaccess.AgentConfigQueryDTO;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 3:55 下午
 */
@Component
@Slf4j
public class AgentConfigClientImpl implements AgentConfigClient {

    /**
     * agent配置生效状态统计api
     */
    private static final String AGENT_CONFIG_STATUS_COUNT = "/amdb/db/api/agentConfig/count";

    /**
     * 配置生效列表查询api
     */
    private static final String AGENT_CONFIG_EFFECT_LIST = "/amdb/db/api/agentConfig/queryConfig";

    @Autowired
    private AmdbClientProperties properties;

    @Override
    public AgentStatInfoDTO agentConfigStatusCount(String configKey, String projectName) {
        String url = properties.getUrl().getAmdb() + AGENT_CONFIG_STATUS_COUNT;
        AgentConfigQueryDTO queryDTO = new AgentConfigQueryDTO();
        queryDTO.setConfigKey(configKey);
        queryDTO.setAppName(projectName);
        //设置租户表示和环境编码
        queryDTO.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        queryDTO.setEnvCode(WebPluginUtils.traceEnvCode());

        try {
            AmdbResult<AgentStatInfoDTO> amdbResponse = AmdbHelper.builder()
                    .httpMethod(HttpMethod.POST)
                    .url(url)
                    .param(queryDTO)
                    .exception(TakinWebExceptionEnum.AGENT_CONFIG_CLIENT_QUERY_ERROR)
                    .eventName("查询探针配置状态数")
                    .one(AgentStatInfoDTO.class);
            if (amdbResponse == null || !amdbResponse.getSuccess()) {
                log.error("前往amdb查询配置状态统计返回异常,响应信息：{}", JSONUtil.toJsonStr(amdbResponse));
                return null;
            }
            return amdbResponse.getData();

        } catch (Exception e) {
            log.error("前往amdb查询配置状态统计报错：{}", JSONUtil.toJsonStr(queryDTO), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    @Override
    public PagingList<AgentConfigDTO> effectList(AgentConfigQueryDTO queryDTO) {
        String url = properties.getUrl().getAmdb() + AGENT_CONFIG_EFFECT_LIST;
        try {
            // 因为tro-web的分页从0开始大数据的分页从1开始，所以这里需要加1
            queryDTO.setCurrentPage(queryDTO.getRealCurrent());
            //设置租户表示和环境编码
            queryDTO.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            queryDTO.setEnvCode(WebPluginUtils.traceEnvCode());
            AmdbResult<List<AgentConfigDTO>> amdbResponse = AmdbHelper.builder()
                    .httpMethod(HttpMethod.POST)
                    .url(url)
                    .param(queryDTO)
                    .eventName("查询探针配置影响列表")
                    .exception(TakinWebExceptionEnum.AGENT_CONFIG_CLIENT_QUERY_ERROR)
                    .list(AgentConfigDTO.class);
            if (amdbResponse == null || !amdbResponse.getSuccess()) {
                log.error("前往amdb查询配置生效列表返回异常,响应信息：{}", JSONUtil.toJsonStr(amdbResponse));
                return PagingList.empty();
            }
            List<AgentConfigDTO> data = amdbResponse.getData();
            if (CollectionUtils.isEmpty(data)) {
                return PagingList.empty();
            }
            return PagingList.of(data, amdbResponse.getTotal());

        } catch (Exception e) {
            log.error("前往amdb查询配置生效列表报错：{}", JSONUtil.toJsonStr(queryDTO), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }
}
