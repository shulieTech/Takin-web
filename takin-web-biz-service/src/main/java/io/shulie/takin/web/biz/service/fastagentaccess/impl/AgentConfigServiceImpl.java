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
import io.shulie.takin.web.biz.nacos.event.DynamicConfigRefreshEvent;
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
import io.shulie.takin.web.common.util.RedisHelper;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * agent配置管理(AgentConfig)service
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

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 对应的zk地址key
     */
    private final static String ZK_CONFIG_KEY = "simulator.zk.servers";

    @Override
    public void batchInsert(List<AgentConfigCreateRequest> createRequestList) {
        this.cacheEvict(CACHE_KEY_AGENT_CONFIG);
        List<String> enConfigKeyList = new ArrayList<>();
        List<String> zhConfigKeyList = new ArrayList<>();

        createRequestList.stream()
            .filter(item -> item.getType() == null || AgentConfigTypeEnum.GLOBAL.getVal().equals(item.getType()))
            .forEach(item -> {
                enConfigKeyList.add(item.getEnKey());
                zhConfigKeyList.add(item.getZhKey());
            });

        // 1、校验新增的全局配置中是否有重复key，有则直接抛异常
        checkGlobalConfig(enConfigKeyList, zhConfigKeyList);

        // 2、特殊校验zk地址配置是否能连接上
//        createRequestList.forEach(item -> {
//            if (ZK_CONFIG_KEY.equals(item.getEnKey()) && !TestZkConnUtils.test(item.getDefaultValue())) {
//                throw AppCommonUtil.getCommonError("zookeeper地址错误，无法连接");
//            }
//        });

        // 3、批量插入
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
        List<Long> userIdList = WebPluginUtils.queryAllowUserIdList();
        List<Long> deptIdList = WebPluginUtils.queryAllowDeptIdList();
        List<ApplicationDetailResult> applicationMntList = applicationDAO.getApplicationMntByUserIdsAndKeyword(
            userIdList, deptIdList, keyword);
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
        // 值类型为单选
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
        // 过滤是否生效配置
        return filterConfigEffect(list, queryRequest);
    }

    @Override
    public void update(AgentConfigUpdateRequest updateRequest) {
        this.cacheEvict(CACHE_KEY_AGENT_CONFIG);
        AgentConfigDetailResult detailResult = agentConfigDAO.findById(updateRequest.getId());
        Assert.notNull(detailResult, "配置不存在！");
        // 特殊处理校验下zk地址
//        if (ZK_CONFIG_KEY.equals(detailResult.getEnKey()) && !TestZkConnUtils.test(updateRequest.getDefaultValue())) {
//            throw AppCommonUtil.getCommonError("zookeeper地址错误，无法连接");
//        }

        String projectName = updateRequest.getProjectName();
        if (StrUtil.isBlank(projectName)) {
            this.updateWithoutProjectName(updateRequest, detailResult);
        }else{
            this.updateWithProjectName(updateRequest, detailResult);
        }
        applicationContext.publishEvent(new DynamicConfigRefreshEvent(projectName));
    }

    /**
     * 当有应用名称时的更新
     *
     * @param updateRequest 更新所需数据
     * @param detailResult  配置详情
     */
    private void updateWithProjectName(AgentConfigUpdateRequest updateRequest, AgentConfigDetailResult detailResult) {
        // 应用配置，直接更新
        if (AgentConfigTypeEnum.isProject(detailResult.getType())) {
            this.updateConfig(updateRequest.getId(), updateRequest.getDefaultValue());
            return;
        }

        // 全局或者租户全局，需要根据应用的有无进行新增或更新
        if (AgentConfigTypeEnum.isGlobal(detailResult.getType())
            || AgentConfigTypeEnum.isTenantGlobal(detailResult.getType())) {
            String enKey = detailResult.getEnKey();
            AgentConfigDetailResult agentConfig = agentConfigDAO.getByEnKeyAndTypeAndProjectNameWithTenant(enKey,
                AgentConfigTypeEnum.PROJECT.getVal(), updateRequest.getProjectName());
            if (agentConfig != null) {
                this.updateConfig(agentConfig.getId(), updateRequest.getDefaultValue());
                return;
            }

            // 插入
            CreateAgentConfigParam createParam = this.getCreateAgentConfigParam(detailResult,
                updateRequest.getDefaultValue(), AgentConfigTypeEnum.PROJECT.getVal());
            createParam.setProjectName(updateRequest.getProjectName());
            agentConfigDAO.insert(createParam);
        }
    }

    /**
     * 更新配置
     *
     * @param id    主键id
     * @param value 修改的值
     */
    private void updateConfig(Long id, String value) {
        // 只传了id，就直接更新对应的id值
        UpdateAgentConfigParam updateParam = new UpdateAgentConfigParam();
        // 设置操作人
        updateParam.setOperator(getOperator());
        updateParam.setId(id);
        updateParam.setDefaultValue(value);
        agentConfigDAO.updateConfigValue(updateParam);
    }

    /**
     * 当没有应用名称时的更新
     *
     * @param updateRequest 更新所需数据
     * @param detailResult  配置详情
     */
    private void updateWithoutProjectName(AgentConfigUpdateRequest updateRequest,
        AgentConfigDetailResult detailResult) {
        // 如果是应用级别或者是租户级别，直接更新
        if (AgentConfigTypeEnum.isProject(detailResult.getType())
            || AgentConfigTypeEnum.isTenantGlobal(detailResult.getType())) {
            this.updateConfig(updateRequest.getId(), updateRequest.getDefaultValue());
            return;
        }

        if (!AgentConfigTypeEnum.isGlobal(detailResult.getType())) {
            return;
        }

        // 如果是全局级别，判断租户级别是否存在，来是否插入或者更新
        String enKey = detailResult.getEnKey();
        AgentConfigDetailResult agentConfig = agentConfigDAO.getByEnKeyAndTypeWithTenant(enKey,
            AgentConfigTypeEnum.TENANT_GLOBAL.getVal());
        if (agentConfig != null) {
            this.updateConfig(agentConfig.getId(), updateRequest.getDefaultValue());
            return;
        }

        // 插入租户环境全局
        CreateAgentConfigParam createParam = this.getCreateAgentConfigParam(detailResult,
            updateRequest.getDefaultValue(), AgentConfigTypeEnum.TENANT_GLOBAL.getVal());
        agentConfigDAO.insert(createParam);
    }

    /**
     * 获得新增所需数据
     *
     * @param detailResult 详情
     * @param defaultValue 默认值
     * @param type         类型
     * @return 新增所需数据
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

    @Override
    public void useGlobal(Long id) {
        this.cacheEvict(CACHE_KEY_AGENT_CONFIG);
        AgentConfigDetailResult detailResult = agentConfigDAO.findById(id);
        // 如果当前id对应的配置不是应用配置则直接return
        if (detailResult == null || !AgentConfigTypeEnum.PROJECT.getVal().equals(detailResult.getType())) {
            return;
        }
        // 删除应用配置
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
        // 1、查询符合条件的全局配置
        AgentConfigQueryParam queryParam = new AgentConfigQueryParam();
        BeanUtils.copyProperties(queryBO, queryParam);
        queryParam.setTenantId(WebPluginUtils.SYS_DEFAULT_TENANT_ID);
        queryParam.setEnvCode(WebPluginUtils.SYS_DEFAULT_ENV_CODE);
        queryParam.setType(AgentConfigTypeEnum.GLOBAL.getVal());
        List<AgentConfigDetailResult> globalConfigList = agentConfigDAO.listByTypeAndTenantIdAndEnvCode(queryParam);
        if (globalConfigList.isEmpty()) {
            return new HashMap<>(0);
        }

        // 将全局配置放入内存map中，因为后续要进行替换，key为配置key，value为AgentConfigDetailResult对象
        Map<String, AgentConfigDetailResult> configMap = globalConfigList.stream().collect(
            Collectors.toMap(AgentConfigDetailResult::getEnKey, x -> x, (v1, v2) -> v2));

        // 1.2 租户下的全局配置
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

        // 2、查询符合条件的应用配置
        if (StringUtils.isEmpty(queryBO.getUserAppKey())) {
            queryParam.setUserAppKey(WebPluginUtils.traceTenantAppKey());
        } else {
            queryParam.setUserAppKey(queryBO.getUserAppKey());
        }
        List<AgentConfigDetailResult> projectConfigList = agentConfigDAO.findProjectList(queryParam);

        // 3、将应用配置替换掉全局配置
        for (AgentConfigDetailResult detailResult : projectConfigList) {
            // 如果应用配置的key在全局配置中不存在，则直接跳过
            if (!configMap.containsKey(detailResult.getEnKey())) {
                continue;
            }
            configMap.put(detailResult.getEnKey(), detailResult);
        }

        // 如果仅看应用配置则只返回应用配置
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

        // 1、调用大数据接口查询生效列表
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
            // 如果未生效，建议处理方案中加上 "重启后生效"
            if (item.getStatus() != null && !item.getStatus()) {
                response.setProgram("重启后生效");
            }

            // 优化性能，列表接口不需要查询生效值
            if (needEffectValue) {
                // 根据应用名，userAppKey 和 enKey查询应该生效的value
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
     * 校验全局配置key是否有重复，必须中文key不重复，英文key不重复
     * 技术上只要英文key不重复就可以了，但是产品要求中文的key也不允许重复
     *
     * @param enConfigKeys 英文配置key
     * @param zhConfigKeys 中文配置key
     */
    private void checkGlobalConfig(List<String> enConfigKeys, List<String> zhConfigKeys) {
        Set<String> enKeySet = new HashSet<>(enConfigKeys);
        if (enKeySet.size() != enConfigKeys.size()) {
            throw AppCommonUtil.getCommonError("新增列表中存在重复的全局英文key，请检查后再提交");
        }
        Set<String> zhKeySet = new HashSet<>(zhConfigKeys);
        if (zhKeySet.size() != zhConfigKeys.size()) {
            throw AppCommonUtil.getCommonError("新增列表中存在重复的全局中文key，请检查后再提交");
        }

        List<AgentConfigDetailResult> enDbResult = agentConfigDAO.findGlobalConfigByEnKeyList(enConfigKeys);
        if (!CollectionUtils.isEmpty(enDbResult)) {
            StringBuilder stringBuilder = new StringBuilder();
            enDbResult.forEach(item -> stringBuilder.append(item.getEnKey()).append(","));
            String repeatKeys = stringBuilder.substring(0, stringBuilder.toString().length() - 1);
            throw AppCommonUtil.getCommonError("存在重复的全局配置英文key：" + repeatKeys);
        }

        List<AgentConfigDetailResult> zhDbResult = agentConfigDAO.findGlobalConfigByZhKeyList(zhConfigKeys);
        if (!CollectionUtils.isEmpty(zhDbResult)) {
            StringBuilder stringBuilder = new StringBuilder();
            zhDbResult.forEach(item -> stringBuilder.append(item.getZhKey()).append(","));
            String repeatKeys = stringBuilder.substring(0, stringBuilder.toString().length() - 1);
            throw AppCommonUtil.getCommonError("存在重复的全局配置中文key：" + repeatKeys);
        }
    }

    /**
     * 过滤配置是否生效
     *
     * @param list  配置列表
     * @param query 查询条件
     * @return AgentConfigListResponse集合
     */
    private List<AgentConfigListResponse> filterConfigEffect(List<AgentConfigListResponse> list,
        AgentConfigQueryRequest query) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        // 最终返回结果集合
        List<AgentConfigListResponse> responseList = new CopyOnWriteArrayList<>();
        // 已生效配置集合
        List<AgentConfigListResponse> effectList = new CopyOnWriteArrayList<>();
        // 未生效配置集合
        List<AgentConfigListResponse> notEffectList = new CopyOnWriteArrayList<>();

        list.parallelStream().forEach(item -> {
            // 立即生效的配置全部都是已生效
            if (AgentConfigEffectMechanismEnum.IMMEDIATELY.getVal().equals(item.getEffectMechanism())) {
                item.setIsEffect(true);
                effectList.add(item);
            } else {
                // 请求大数据接口，查询当前配置未生效记录数是否大于0
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

        // 如果isEffect为null则不进行筛选，两种状态都返回
        if (query.getIsEffect() == null) {
            responseList.addAll(effectList);
            responseList.addAll(notEffectList);
        } else if (query.getIsEffect()) {
            // 筛选所有已生效配置
            responseList = effectList;
        } else {
            // 筛选未生效配置
            responseList = notEffectList;
        }
        responseList = responseList.parallelStream().sorted(Comparator.comparing(AgentConfigListResponse::getEnKey))
            .sorted(Comparator.comparing(AgentConfigListResponse::getGmtCreate).reversed()).collect(
                Collectors.toList());
        return responseList;
    }

    /**
     * 获取操作人
     *
     * @return 操作人
     */
    private String getOperator() {
        UserExt userExt = WebPluginUtils.traceUser();
        return userExt == null ? LoginConstant.DEFAULT_OPERATOR : userExt.getName();
    }

    private void cacheEvict(String keyPre){
        Set<String> keys = RedisHelper.keys(keyPre);
        if (!CollectionUtils.isEmpty(keys)){
            keys.forEach(RedisHelper::delete);
        }
    }

}
