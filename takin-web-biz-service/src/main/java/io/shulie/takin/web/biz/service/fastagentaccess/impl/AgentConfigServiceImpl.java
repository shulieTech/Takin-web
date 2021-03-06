package io.shulie.takin.web.biz.service.fastagentaccess.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableMap;
import io.shulie.amdb.common.dto.agent.AgentConfigDTO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.AgentConfigClient;
import io.shulie.takin.web.amdb.bean.query.fastagentaccess.AgentConfigQueryDTO;
import io.shulie.takin.web.biz.constant.LoginConstant;
import io.shulie.takin.web.biz.pojo.bo.ConfigListQueryBO;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigEffectQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentDynamicConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigEffectListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigListResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentConfigService;
import io.shulie.takin.web.biz.utils.TestZkConnUtils;
import io.shulie.takin.web.biz.utils.fastagentaccess.AgentVersionUtil;
import io.shulie.takin.web.common.constant.CacheConstants;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigEffectMechanismEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigTypeEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigValueTypeEnum;
import io.shulie.takin.web.common.util.AppCommonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.fastagentaccess.AgentConfigDAO;
import io.shulie.takin.web.data.param.fastagentaccess.AgentConfigQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.CreateAgentConfigParam;
import io.shulie.takin.web.data.param.fastagentaccess.UpdateAgentConfigParam;
import io.shulie.takin.web.data.result.application.AgentConfigDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * agent????????????(AgentConfig)service
 *
 * @author ocean_wll
 * @date 2021-08-12 18:54:56
 */
@Service
public class AgentConfigServiceImpl implements AgentConfigService, CacheConstants {

    @Resource
    private ApplicationDAO applicationDAO;
    @Resource
    private AgentConfigDAO agentConfigDAO;
    @Resource
    private AgentConfigClient agentConfigClient;

    @Autowired
    private AgentConfigService agentConfigService;

    /**
     * ?????????zk??????key
     */
    private final static String ZK_CONFIG_KEY = "simulator.zk.servers";

    @CacheEvict(value = CACHE_KEY_AGENT_CONFIG, allEntries = true)
    @Override
    public void batchInsert(List<AgentConfigCreateRequest> createRequestList) {
        List<String> enConfigKeyList = new ArrayList<>();
        List<String> zhConfigKeyList = new ArrayList<>();

        createRequestList.stream()
            .filter(item -> item.getType() == null || AgentConfigTypeEnum.GLOBAL.getVal().equals(item.getType()))
            .forEach(item -> {
                enConfigKeyList.add(item.getEnKey());
                zhConfigKeyList.add(item.getZhKey());
            });

        // 1????????????????????????????????????????????????key????????????????????????
        checkGlobalConfig(enConfigKeyList, zhConfigKeyList);

        // 2???????????????zk??????????????????????????????
        createRequestList.forEach(item -> {
            if (ZK_CONFIG_KEY.equals(item.getEnKey()) && !TestZkConnUtils.test(item.getDefaultValue())) {
                throw AppCommonUtil.getCommonError("zookeeper???????????????????????????");
            }
        });

        // 3???????????????
        List<CreateAgentConfigParam> createAgentConfigParams = createRequestList.stream()
            .map(item -> {
                CreateAgentConfigParam createAgentConfigParam = new CreateAgentConfigParam();
                BeanUtils.copyProperties(item, createAgentConfigParam);
                createAgentConfigParam.setOperator(getOperator());
                if (!CollectionUtils.isEmpty(item.getValueOptionList())) {
                    createAgentConfigParam.setValueOption(JSON.toJSONString(item.getValueOptionList()));
                }
                createAgentConfigParam.setEffectMinVersionNum(AgentVersionUtil.string2Long(item.getEffectMinVersion()));
                createAgentConfigParam.setTenantId(WebPluginUtils.SYS_DEFAULT_TENANT_ID);
                createAgentConfigParam.setEnvCode(WebPluginUtils.SYS_DEFAULT_ENV_CODE);
                return createAgentConfigParam;
            })
            .collect(Collectors.toList());
        agentConfigDAO.batchInsert(createAgentConfigParams);
    }

    @Override
    public List<Map<String, String>> getAllGlobalKey() {
        List<AgentConfigDetailResult> detailResultList = agentConfigDAO.getAllGlobalConfig();
        return detailResultList.stream().map(item -> ImmutableMap.of(item.getEnKey(), item.getZhKey())).collect(
            Collectors.toList());
    }

    @Override
    public List<String> getAllApplication(String keyword) {
        List<Long> userIdList = WebPluginUtils.getQueryAllowUserIdList();
        List<ApplicationDetailResult> applicationMntList = applicationDAO.getApplicationMntByUserIdsAndKeyword(
            userIdList,
            keyword);
        return applicationMntList.stream().map(ApplicationDetailResult::getApplicationName).collect(
            Collectors.toList());
    }

    @Override
    public Boolean checkZhKey(String zhKey) {
        AgentConfigDetailResult result = agentConfigDAO.findGlobalConfigByZhKey(zhKey);
        return result != null;
    }

    @Override
    public Boolean checkEnKey(String enKey) {
        AgentConfigDetailResult result = agentConfigDAO.findGlobalConfigByEnKey(enKey);
        return result != null;
    }

    @Override
    public List<String> getValueOption(Long id) {
        AgentConfigDetailResult result = agentConfigDAO.findById(id);
        // ??????????????????
        if (AgentConfigValueTypeEnum.RADIO.getVal().equals(result.getValueType())) {
            return JSON.parseArray(result.getValueOption(), String.class);
        }
        return null;
    }

    @Override
    public List<AgentConfigListResponse> list(AgentConfigQueryRequest queryRequest) {
        ConfigListQueryBO queryBO = new ConfigListQueryBO();
        queryBO.setProjectName(queryRequest.getProjectName());
        queryBO.setEffectMechanism(queryRequest.getEffectMechanism());
        queryBO.setEnKey(queryRequest.getEnKey());
        queryBO.setReadProjectConfig(queryRequest.getReadProjectConfig());
        List<AgentConfigListResponse> list = agentConfigService.getConfigList(queryBO).values().stream()
            .map(item -> {
                AgentConfigListResponse agentConfigListResponse = new AgentConfigListResponse();
                BeanUtils.copyProperties(item, agentConfigListResponse);
                return agentConfigListResponse;
            }).collect(Collectors.toList());
        // ????????????????????????
        return filterConfigEffect(list, queryRequest);
    }

    @CacheEvict(value = CACHE_KEY_AGENT_CONFIG, allEntries = true)
    @Override
    public void update(AgentConfigUpdateRequest updateRequest) {
        AgentConfigDetailResult detailResult = agentConfigDAO.findById(updateRequest.getId());
        Assert.notNull(detailResult, "??????????????????");
        // ?????????????????????zk??????
        if (ZK_CONFIG_KEY.equals(detailResult.getEnKey()) && !TestZkConnUtils.test(updateRequest.getDefaultValue())) {
            throw AppCommonUtil.getCommonError("zookeeper???????????????????????????");
        }

        String projectName = updateRequest.getProjectName();
        if (StrUtil.isBlank(projectName)) {
            this.updateWithoutProjectName(updateRequest, detailResult);
            return;
        }

        this.updateWithProjectName(updateRequest, detailResult);
    }

    /**
     * ??????????????????????????????
     *
     * @param updateRequest ??????????????????
     * @param detailResult  ????????????
     */
    private void updateWithProjectName(AgentConfigUpdateRequest updateRequest, AgentConfigDetailResult detailResult) {
        // ???????????????????????????
        if (AgentConfigTypeEnum.isProject(detailResult.getType())) {
            this.updateConfig(updateRequest.getId(), updateRequest.getDefaultValue());
            return;
        }

        // ???????????????????????????????????????????????????????????????????????????
        if (AgentConfigTypeEnum.isGlobal(detailResult.getType())
            || AgentConfigTypeEnum.isTenantGlobal(detailResult.getType())) {
            String enKey = detailResult.getEnKey();
            AgentConfigDetailResult agentConfig = agentConfigDAO.getByEnKeyAndTypeAndProjectNameWithTenant(enKey,
                AgentConfigTypeEnum.PROJECT.getVal(), updateRequest.getProjectName());
            if (agentConfig != null) {
                this.updateConfig(agentConfig.getId(), updateRequest.getDefaultValue());
                return;
            }

            // ??????
            CreateAgentConfigParam createParam = this.getCreateAgentConfigParam(detailResult,
                updateRequest.getDefaultValue(), AgentConfigTypeEnum.PROJECT.getVal());
            createParam.setProjectName(updateRequest.getProjectName());
            agentConfigDAO.insert(createParam);
        }
    }

    /**
     * ????????????
     *
     * @param id    ??????id
     * @param value ????????????
     */
    private void updateConfig(Long id, String value) {
        // ?????????id???????????????????????????id???
        UpdateAgentConfigParam updateParam = new UpdateAgentConfigParam();
        // ???????????????
        updateParam.setOperator(getOperator());
        updateParam.setId(id);
        updateParam.setDefaultValue(value);
        agentConfigDAO.updateConfigValue(updateParam);
    }

    /**
     * ?????????????????????????????????
     *
     * @param updateRequest ??????????????????
     * @param detailResult  ????????????
     */
    private void updateWithoutProjectName(AgentConfigUpdateRequest updateRequest,
        AgentConfigDetailResult detailResult) {
        // ?????????????????????????????????????????????????????????
        if (AgentConfigTypeEnum.isProject(detailResult.getType())
            || AgentConfigTypeEnum.isTenantGlobal(detailResult.getType())) {
            this.updateConfig(updateRequest.getId(), updateRequest.getDefaultValue());
            return;
        }

        if (!AgentConfigTypeEnum.isGlobal(detailResult.getType())) {
            return;
        }

        // ????????????????????????????????????????????????????????????????????????????????????
        String enKey = detailResult.getEnKey();
        AgentConfigDetailResult agentConfig = agentConfigDAO.getByEnKeyAndTypeWithTenant(enKey,
            AgentConfigTypeEnum.TENANT_GLOBAL.getVal());
        if (agentConfig != null) {
            this.updateConfig(agentConfig.getId(), updateRequest.getDefaultValue());
            return;
        }

        // ????????????????????????
        CreateAgentConfigParam createParam = this.getCreateAgentConfigParam(detailResult,
            updateRequest.getDefaultValue(), AgentConfigTypeEnum.TENANT_GLOBAL.getVal());
        agentConfigDAO.insert(createParam);
    }

    /**
     * ????????????????????????
     *
     * @param detailResult ??????
     * @param defaultValue ?????????
     * @param type         ??????
     * @return ??????????????????
     */
    private CreateAgentConfigParam getCreateAgentConfigParam(AgentConfigDetailResult detailResult,
        String defaultValue, Integer type) {
        CreateAgentConfigParam createParam = new CreateAgentConfigParam();
        createParam.setOperator(getOperator());
        createParam.setType(type);
        createParam.setZhKey(detailResult.getZhKey());
        createParam.setEnKey(detailResult.getEnKey());
        createParam.setDefaultValue(defaultValue);
        createParam.setDesc(detailResult.getDesc());
        createParam.setEffectType(detailResult.getEffectType());
        createParam.setEffectMechanism(detailResult.getEffectMechanism());
        createParam.setEffectMinVersion(detailResult.getEffectMinVersion());
        createParam.setEffectMinVersionNum(detailResult.getEffectMinVersionNum());
        createParam.setEditable(detailResult.getEditable());
        createParam.setValueType(detailResult.getValueType());
        createParam.setValueOption(detailResult.getValueOption());
        createParam.setUserAppKey(WebPluginUtils.traceTenantAppKey());
        return createParam;
    }

    @CacheEvict(value = CACHE_KEY_AGENT_CONFIG, allEntries = true)
    @Override
    public void useGlobal(Long id) {
        AgentConfigDetailResult detailResult = agentConfigDAO.findById(id);
        // ????????????id??????????????????????????????????????????return
        if (detailResult == null || !AgentConfigTypeEnum.PROJECT.getVal().equals(detailResult.getType())) {
            return;
        }
        // ??????????????????
        agentConfigDAO.deleteById(id);
    }

    @Override
    public Map<String, String> agentConfig(AgentDynamicConfigQueryRequest queryRequest) {
        ConfigListQueryBO queryBO = new ConfigListQueryBO();
        queryBO.setProjectName(queryRequest.getProjectName());
        queryBO.setEffectMechanism(queryRequest.getEffectMechanism());
        queryBO.setEffectMinVersionNum(AgentVersionUtil.string2Long(queryRequest.getVersion()));
        Map<String, AgentConfigDetailResult> configList = agentConfigService.getConfigList(queryBO);
        return configList.values().stream().collect(
            Collectors.toMap(AgentConfigDetailResult::getEnKey, AgentConfigDetailResult::getDefaultValue));
    }

    @Cacheable(value = CACHE_KEY_AGENT_CONFIG, keyGenerator = CACHE_KEY_GENERATOR_BY_TENANT_INFO)
    @Override
    public Map<String, AgentConfigDetailResult> getConfigList(ConfigListQueryBO queryBO) {
        // 1????????????????????????????????????
        AgentConfigQueryParam queryParam = new AgentConfigQueryParam();
        BeanUtils.copyProperties(queryBO, queryParam);
        queryParam.setTenantId(WebPluginUtils.SYS_DEFAULT_TENANT_ID);
        queryParam.setEnvCode(WebPluginUtils.SYS_DEFAULT_ENV_CODE);
        queryParam.setType(AgentConfigTypeEnum.GLOBAL.getVal());
        List<AgentConfigDetailResult> globalConfigList = agentConfigDAO.listByTypeAndTenantIdAndEnvCode(queryParam);
        if (globalConfigList.isEmpty()) {
            return new HashMap<>(0);
        }

        // ???????????????????????????map????????????????????????????????????key?????????key???value???AgentConfigDetailResult??????
        Map<String, AgentConfigDetailResult> configMap = globalConfigList.stream().collect(
            Collectors.toMap(AgentConfigDetailResult::getEnKey, x -> x, (v1, v2) -> v2));

        // 1.2 ????????????????????????
        if (!StringUtils.isEmpty(queryBO.getUserAppKey()) && !StringUtils.isEmpty(queryBO.getEnvCode())) {
            TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(queryBO.getUserAppKey(), queryBO.getEnvCode());
            if (tenantInfoExt != null) {
                queryParam.setTenantId(tenantInfoExt.getTenantId());
            }
        } else {
            queryParam.setTenantId(WebPluginUtils.traceTenantId());
        }

        if (!StringUtils.isEmpty(queryBO.getEnvCode())) {
            queryParam.setEnvCode(queryBO.getEnvCode());
        } else {
            queryParam.setEnvCode(WebPluginUtils.traceEnvCode());
        }
        queryParam.setType(AgentConfigTypeEnum.TENANT_GLOBAL.getVal());
        List<AgentConfigDetailResult> tenantGlobalConfigList = agentConfigDAO.listByTypeAndTenantIdAndEnvCode(
            queryParam);
        if (!tenantGlobalConfigList.isEmpty()) {
            Map<String, AgentConfigDetailResult> tenantConfigMap = tenantGlobalConfigList.stream().collect(
                Collectors.toMap(AgentConfigDetailResult::getEnKey, x -> x, (v1, v2) -> v2));
            configMap.putAll(tenantConfigMap);
        }

        // 2????????????????????????????????????
        if (StringUtils.isEmpty(queryBO.getUserAppKey())) {
            queryParam.setUserAppKey(WebPluginUtils.traceTenantAppKey());
        } else {
            queryParam.setUserAppKey(queryBO.getUserAppKey());
        }
        List<AgentConfigDetailResult> projectConfigList = agentConfigDAO.findProjectList(queryParam);

        // 3???????????????????????????????????????
        for (AgentConfigDetailResult detailResult : projectConfigList) {
            // ?????????????????????key?????????????????????????????????????????????
            if (!configMap.containsKey(detailResult.getEnKey())) {
                continue;
            }
            configMap.put(detailResult.getEnKey(), detailResult);
        }

        // ????????????????????????????????????????????????
        if (queryBO.getReadProjectConfig() != null && queryBO.getReadProjectConfig()) {
            configMap = configMap.values().stream()
                .filter(item -> AgentConfigTypeEnum.PROJECT.getVal().equals(item.getType()))
                .collect(Collectors.toMap(AgentConfigDetailResult::getEnKey, x -> x));
        }
        return configMap;
    }

    @Override
    public PagingList<AgentConfigEffectListResponse> queryConfigEffectList(AgentConfigEffectQueryRequest queryRequest) {
        boolean needEffectValue = queryRequest.getNeedEffectValue() == null || queryRequest.getNeedEffectValue();

        // 1??????????????????????????????????????????
        AgentConfigQueryDTO queryDTO = new AgentConfigQueryDTO();
        BeanUtils.copyProperties(queryRequest, queryDTO);
        queryDTO.setAppName(queryRequest.getProjectName());
        queryDTO.setConfigKey(queryRequest.getEnKey());
        queryDTO.setStatus(queryRequest.getIsEffect());
        PagingList<AgentConfigDTO> configList = agentConfigClient.effectList(queryDTO);
        List<AgentConfigDTO> dtoList = configList.getList();
        if (CollectionUtils.isEmpty(dtoList)) {
            return PagingList.empty();
        }

        List<AgentConfigEffectListResponse> responseList = dtoList.parallelStream().map(item -> {
            AgentConfigEffectListResponse response = new AgentConfigEffectListResponse();
            response.setAgentId(item.getAgentId());
            response.setIsEffect(item.getStatus());
            response.setProjectName(item.getAppName());
            // ????????????????????????????????????????????? "???????????????"
            if (item.getStatus() != null && !item.getStatus()) {
                response.setProgram("???????????????");
            }

            // ???????????????????????????????????????????????????
            if (needEffectValue) {
                // ??????????????????userAppKey ??? enKey?????????????????????value
                AgentConfigDetailResult configDetailResult = queryByEnKeyAndProject(queryRequest.getEnKey(),
                    item.getAppName());
                if (configDetailResult != null) {
                    response.setEffectVal(configDetailResult.getDefaultValue());
                }
            }
            return response;
        }).collect(Collectors.toList());

        return PagingList.of(responseList, configList.getTotal());
    }

    @Override
    public AgentConfigDetailResult queryByEnKeyAndProject(String enKey, String projectName) {
        if (StringUtils.isEmpty(enKey)) {
            return null;
        }
        ConfigListQueryBO queryBO = new ConfigListQueryBO();
        queryBO.setEnKey(enKey);
        queryBO.setProjectName(projectName);

        Map<String, AgentConfigDetailResult> map = getConfigList(queryBO);
        return map.get(enKey);
    }

    /**
     * ??????????????????key??????????????????????????????key??????????????????key?????????
     * ?????????????????????key???????????????????????????????????????????????????key??????????????????
     *
     * @param enConfigKeys ????????????key
     * @param zhConfigKeys ????????????key
     */
    private void checkGlobalConfig(List<String> enConfigKeys, List<String> zhConfigKeys) {
        Set<String> enKeySet = new HashSet<>(enConfigKeys);
        if (enKeySet.size() != enConfigKeys.size()) {
            throw AppCommonUtil.getCommonError("??????????????????????????????????????????key????????????????????????");
        }
        Set<String> zhKeySet = new HashSet<>(zhConfigKeys);
        if (zhKeySet.size() != zhConfigKeys.size()) {
            throw AppCommonUtil.getCommonError("??????????????????????????????????????????key????????????????????????");
        }

        List<AgentConfigDetailResult> enDbResult = agentConfigDAO.findGlobalConfigByEnKeyList(enConfigKeys);
        if (!CollectionUtils.isEmpty(enDbResult)) {
            StringBuilder stringBuilder = new StringBuilder();
            enDbResult.forEach(item -> stringBuilder.append(item.getEnKey()).append(","));
            String repeatKeys = stringBuilder.substring(0, stringBuilder.toString().length() - 1);
            throw AppCommonUtil.getCommonError("?????????????????????????????????key???" + repeatKeys);
        }

        List<AgentConfigDetailResult> zhDbResult = agentConfigDAO.findGlobalConfigByZhKeyList(zhConfigKeys);
        if (!CollectionUtils.isEmpty(zhDbResult)) {
            StringBuilder stringBuilder = new StringBuilder();
            zhDbResult.forEach(item -> stringBuilder.append(item.getZhKey()).append(","));
            String repeatKeys = stringBuilder.substring(0, stringBuilder.toString().length() - 1);
            throw AppCommonUtil.getCommonError("?????????????????????????????????key???" + repeatKeys);
        }
    }

    /**
     * ????????????????????????
     *
     * @param list  ????????????
     * @param query ????????????
     * @return AgentConfigListResponse??????
     */
    private List<AgentConfigListResponse> filterConfigEffect(List<AgentConfigListResponse> list,
        AgentConfigQueryRequest query) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        // ????????????????????????
        List<AgentConfigListResponse> responseList = new CopyOnWriteArrayList<>();
        // ?????????????????????
        List<AgentConfigListResponse> effectList = new CopyOnWriteArrayList<>();
        // ?????????????????????
        List<AgentConfigListResponse> notEffectList = new CopyOnWriteArrayList<>();

        list.parallelStream().forEach(item -> {
            // ??????????????????????????????????????????
            if (AgentConfigEffectMechanismEnum.IMMEDIATELY.getVal().equals(item.getEffectMechanism())) {
                item.setIsEffect(true);
                effectList.add(item);
            } else {
                // ????????????????????????????????????????????????????????????????????????0
                AgentConfigEffectQueryRequest queryRequest = new AgentConfigEffectQueryRequest();
                queryRequest.setEnKey(item.getEnKey());
                queryRequest.setProjectName(query.getProjectName());
                queryRequest.setIsEffect(false);
                queryRequest.setNeedEffectValue(false);
                PagingList<AgentConfigEffectListResponse> effectListResponsePagingList = queryConfigEffectList(
                    queryRequest);
                if (effectListResponsePagingList.getTotal() > 0) {
                    item.setIsEffect(false);
                    notEffectList.add(item);
                } else {
                    item.setIsEffect(true);
                    effectList.add(item);
                }
            }
        });

        // ??????isEffect???null??????????????????????????????????????????
        if (query.getIsEffect() == null) {
            responseList.addAll(effectList);
            responseList.addAll(notEffectList);
        } else if (query.getIsEffect()) {
            // ???????????????????????????
            responseList = effectList;
        } else {
            // ?????????????????????
            responseList = notEffectList;
        }
        responseList = responseList.parallelStream().sorted(Comparator.comparing(AgentConfigListResponse::getEnKey))
            .sorted(Comparator.comparing(AgentConfigListResponse::getGmtCreate).reversed()).collect(
                Collectors.toList());
        return responseList;
    }

    /**
     * ???????????????
     *
     * @return ?????????
     */
    private String getOperator() {
        UserExt userExt = WebPluginUtils.traceUser();
        return userExt == null ? LoginConstant.DEFAULT_OPERATOR : userExt.getName();
    }

}
