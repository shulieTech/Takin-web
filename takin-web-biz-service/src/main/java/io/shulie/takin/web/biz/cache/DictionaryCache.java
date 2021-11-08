package io.shulie.takin.web.biz.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.dao.dict.TDictionaryDataMapper;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.HttpTypeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.LinkChangeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.LinkChangeTypeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.LinkLevelEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.LinkTypeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess.AgentConfigEditableEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess.AgentConfigEffectMechanismEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess.AgentConfigEffectTypeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess.AgentConfigTypeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess.AgentConfigValueTypeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess.AgentStatusEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess.PluginStatusEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastagentaccess.ProbeStatusEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastdebug.DebugHttpTypeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastdebug.DebugRequestTypeEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.remotecall.RemoteCallConfigEnumMapping;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 常量字典枚举类
 *
 * @author vernon
 * @date 2019/12/2 16:51
 */
@Component
public class DictionaryCache {

    private static final Map<String, List<EnumResult>> DICTIONARY_MAP = Maps.newHashMap();
    @Resource
    private TDictionaryDataMapper tDictionaryDataMapper;

    public static EnumResult getObjectByParam(String key, Integer valueCode) {
        return getObjectByParam(key, String.valueOf(valueCode));
    }

    public static EnumResult getObjectByParamByLabel(String key, String label) {
        if (key == null || label == null) {
            return null;
        }
        List<EnumResult> dataList = DICTIONARY_MAP.get(key);
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return dataList.stream().filter(data -> data.getLabel().equals(label)).findFirst().orElse(
            null);

    }

    public static EnumResult getObjectByParam(String key, String valueCode) {
        if (key == null || valueCode == null) {
            return null;
        }
        List<EnumResult> dataList = DICTIONARY_MAP.get(key);
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        EnumResult result = dataList.stream().filter(data -> data.getValue().equals(valueCode)).findFirst().orElse(
            null);
        if (result == null) {
        }
        return result;
    }

    @PostConstruct
    public void initDictionary() {
        DICTIONARY_MAP.put("link_level", LinkLevelEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("isCore", LinkTypeEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("isChange", LinkChangeEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("changeType", LinkChangeTypeEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("http_type", HttpTypeEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("DEBUG_REQUEST_TYPE", DebugHttpTypeEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("DEBUG_HTTP_TYPE", DebugRequestTypeEnumMapping.neededEnumResults());
        // 远程调用
        DICTIONARY_MAP.put("REMOTE_CALL_CONFIG_TYPE", RemoteCallConfigEnumMapping.neededEnumResults());
        //dicMap.put("REMOTE_CALL_TYPE", RemoteCallTypeEnumMapping.neededEnumResults());

        // agent快速接入
        DICTIONARY_MAP.put("agent_config_editable", AgentConfigEditableEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("agent_config_effect_mechanism", AgentConfigEffectMechanismEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("agent_config_effect_type", AgentConfigEffectTypeEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("agent_config_type", AgentConfigTypeEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("agent_config_value_type", AgentConfigValueTypeEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("agent_status", AgentStatusEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("agent_plugin_status", PluginStatusEnumMapping.neededEnumResults());
        DICTIONARY_MAP.put("agent_probe_status", ProbeStatusEnumMapping.neededEnumResults());

        //数据字段
        fillDictFromDatabase();
    }

    private void fillDictFromDatabase() {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("valueActive", "Y");
        paramMap.put("tenantId", WebPluginUtils.traceTenantId());
        paramMap.put("envCode", WebPluginUtils.traceEnvCode());
        List<TDictionaryVo> voList = null;
            tDictionaryDataMapper.queryDictionaryList(paramMap);
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }
        //分组
        Map<String, List<TDictionaryVo>> groupMap = voList.stream().collect(
            Collectors.groupingBy(TDictionaryVo::getTypeAlias));
        //组合数据
        groupMap.forEach((key, value) -> {
            List<EnumResult> resultList = Lists.newArrayList();
            value.forEach(data -> {
                EnumResult result = new EnumResult();
                try {
                    result.setNum(Integer.parseInt(data.getValueOrder()));
                } catch (Exception e) {
                }
                result.setLabel(data.getValueName());
                result.setValue(data.getValueCode());
                result.setDisable("N".equalsIgnoreCase(data.getValueActive()));
                resultList.add(result);
            });
            DICTIONARY_MAP.put(key, resultList);
        });
    }

    public Map<String, List<EnumResult>> getDicMap(String key) {
        if (StringUtils.isEmpty(key)) {
            return DICTIONARY_MAP;
        } else {
            HashMap<String, List<EnumResult>> resultMap = new HashMap<>(2);
            resultMap.put(key, DICTIONARY_MAP.get(key));
            return resultMap;
        }
    }

}
