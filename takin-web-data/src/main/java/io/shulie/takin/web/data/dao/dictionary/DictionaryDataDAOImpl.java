package io.shulie.takin.web.data.dao.dictionary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.dao.dict.TDictionaryDataMapper;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/3 8:32 下午
 */
@Component
public class DictionaryDataDAOImpl implements DictionaryDataDAO{

    @Resource
    private TDictionaryDataMapper tDictionaryDataMapper;
    @Override
    public List<TDictionaryVo> getDictByCode(String code) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("valueActive", "Y");
        paramMap.put("typeAlias", code);
        paramMap.put("tenantId", WebPluginUtils.traceTenantId());
        paramMap.put("envCode", WebPluginUtils.traceEnvCode());
        List<TDictionaryVo> vos = tDictionaryDataMapper.queryDictionaryList(paramMap);
        if(CollectionUtils.isEmpty(vos)) {
            return Lists.newArrayList();
        }
        return vos.stream().filter(t -> t.getTypeAlias().equals(code)).collect(Collectors.toList());
    }

    @Override
    public List<TDictionaryVo> getDictByCode(String code, Long tenantId, String envCode) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("valueActive", "Y");
        paramMap.put("typeAlias", code);
        // 传入租户 环境
        paramMap.put("tenantId", tenantId);
        paramMap.put("envCode", envCode);
        List<TDictionaryVo> vos = tDictionaryDataMapper.queryDictionaryList(paramMap);
        if(CollectionUtils.isEmpty(vos)) {
            return Lists.newArrayList();
        }
        return vos.stream().filter(t -> t.getTypeAlias().equals(code)).collect(Collectors.toList());
    }
}
