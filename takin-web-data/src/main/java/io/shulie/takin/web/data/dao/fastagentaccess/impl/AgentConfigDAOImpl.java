package io.shulie.takin.web.data.dao.fastagentaccess.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigTypeEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.fastagentaccess.AgentConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.AgentConfigMapper;
import io.shulie.takin.web.data.model.mysql.AgentConfigEntity;
import io.shulie.takin.web.data.param.fastagentaccess.AgentConfigQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.AgentProjectConfigQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.CreateAgentConfigParam;
import io.shulie.takin.web.data.param.fastagentaccess.UpdateAgentConfigParam;
import io.shulie.takin.web.data.result.application.AgentConfigDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * agent配置管理(AgentConfig)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-08-12 18:57:01
 */
@Service
public class AgentConfigDAOImpl extends ServiceImpl<AgentConfigMapper, AgentConfigEntity>
    implements AgentConfigDAO, MPUtil<AgentConfigEntity> {

    @Autowired
    private AgentConfigMapper agentConfigMapper;

    @Override
    public void insert(CreateAgentConfigParam createParam) {
        AgentConfigEntity entity = new AgentConfigEntity();
        BeanUtils.copyProperties(createParam, entity);
        agentConfigMapper.insert(entity);
    }

    @Override
    public void batchInsert(List<CreateAgentConfigParam> paramList) {
        if (CollectionUtils.isEmpty(paramList)) {
            return;
        }
        List<AgentConfigEntity> entities = paramList.stream().map(param -> {
            AgentConfigEntity entity = new AgentConfigEntity();
            BeanUtils.copyProperties(param, entity);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(entities);
    }

    @Override
    public AgentConfigDetailResult findGlobalConfigByEnKey(String enKey) {
        AgentConfigDetailResult result = null;
        AgentConfigEntity entity = agentConfigMapper.selectOne(
            this.getLambdaQueryWrapper()
                .eq(AgentConfigEntity::getEnKey, enKey)
                .eq(AgentConfigEntity::getType, AgentConfigTypeEnum.GLOBAL.getVal()));
        if (entity != null) {
            result = new AgentConfigDetailResult();
            BeanUtils.copyProperties(entity, result);
        }
        return result;
    }

    @Override
    public AgentConfigDetailResult findGlobalConfigByZhKey(String zhKey) {
        AgentConfigDetailResult result = null;
        AgentConfigEntity entity = agentConfigMapper.selectOne(
            this.getLambdaQueryWrapper()
                .eq(AgentConfigEntity::getZhKey, zhKey)
                .eq(AgentConfigEntity::getType, AgentConfigTypeEnum.GLOBAL.getVal()));
        if (entity != null) {
            result = new AgentConfigDetailResult();
            BeanUtils.copyProperties(entity, result);
        }
        return result;
    }

    @Override
    public List<AgentConfigDetailResult> findGlobalConfigByEnKeyList(List<String> enKeyList) {
        List<AgentConfigEntity> entityList = agentConfigMapper.selectList(
            this.getLambdaQueryWrapper()
                .in(AgentConfigEntity::getEnKey, enKeyList)
                .eq(AgentConfigEntity::getType, AgentConfigTypeEnum.GLOBAL.getVal()));
        return CommonUtil.list2list(entityList, AgentConfigDetailResult.class);
    }

    @Override
    public List<AgentConfigDetailResult> findGlobalConfigByZhKeyList(List<String> zhKeyList) {
        List<AgentConfigEntity> entityList = agentConfigMapper.selectList(
            this.getLambdaQueryWrapper()
                .in(AgentConfigEntity::getZhKey, zhKeyList)
                .eq(AgentConfigEntity::getType, AgentConfigTypeEnum.GLOBAL.getVal()));
        return CommonUtil.list2list(entityList, AgentConfigDetailResult.class);
    }

    @Override
    public List<AgentConfigDetailResult> getAllGlobalConfig() {
        List<AgentConfigEntity> entityList = agentConfigMapper.selectList(
            this.getLambdaQueryWrapper()
                .eq(AgentConfigEntity::getType, AgentConfigTypeEnum.GLOBAL.getVal()));
        return CommonUtil.list2list(entityList, AgentConfigDetailResult.class);
    }

    @Override
    public AgentConfigDetailResult findById(Long id) {
        AgentConfigDetailResult result = new AgentConfigDetailResult();
        AgentConfigEntity entity = agentConfigMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public AgentConfigDetailResult findProjectConfig(AgentProjectConfigQueryParam queryParam) {
        // 如果查询条件中没有projectName和userAppKey直接返回null
        if (StringUtils.isBlank(queryParam.getProjectName()) || StringUtils.isBlank(queryParam.getUserAppKey())) {
            return null;
        }
        AgentConfigEntity entity = agentConfigMapper.selectOne(this.getLambdaQueryWrapper()
            .eq(AgentConfigEntity::getType, AgentConfigTypeEnum.PROJECT.getVal())
            .eq(AgentConfigEntity::getEnKey, queryParam.getEnKey())
            .eq(AgentConfigEntity::getProjectName, queryParam.getProjectName())
            .eq(AgentConfigEntity::getUserAppKey, queryParam.getUserAppKey()));
        if (entity == null) {
            return null;
        }
        AgentConfigDetailResult result = new AgentConfigDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public List<AgentConfigDetailResult> findGlobalList(AgentConfigQueryParam queryParam) {
        List<AgentConfigEntity> entityList = agentConfigMapper.selectList(this.getLambdaQueryWrapper()
                .eq(AgentConfigEntity::getTenantId, queryParam.getTenantId())
                .eq(AgentConfigEntity::getEnvCode, queryParam.getEnvCode())
                .eq(AgentConfigEntity::getType, AgentConfigTypeEnum.GLOBAL.getVal())
                .eq(queryParam.getEffectMechanism() != null, AgentConfigEntity::getEffectMechanism,
                    queryParam.getEffectMechanism())
                .eq(StringUtils.isNotBlank(queryParam.getEnKey()), AgentConfigEntity::getEnKey, queryParam.getEnKey())
                .le(queryParam.getEffectMinVersionNum() != null, AgentConfigEntity::getEffectMinVersionNum,
                    queryParam.getEffectMinVersionNum()));
        return CommonUtil.list2list(entityList, AgentConfigDetailResult.class);
    }

    @Override
    public List<AgentConfigDetailResult> findProjectList(AgentConfigQueryParam queryParam) {
        if (StringUtils.isBlank(queryParam.getProjectName()) || StringUtils.isBlank(queryParam.getUserAppKey())) {
            return new ArrayList<>();
        }
        List<AgentConfigEntity> entityList = agentConfigMapper.selectList(
            this.getLambdaQueryWrapper()
                .eq(AgentConfigEntity::getType, AgentConfigTypeEnum.PROJECT.getVal())
                .eq(AgentConfigEntity::getProjectName, queryParam.getProjectName())
                .eq(AgentConfigEntity::getUserAppKey, queryParam.getUserAppKey())
                .eq(queryParam.getEffectMechanism() != null, AgentConfigEntity::getEffectMechanism,
                    queryParam.getEffectMechanism())
                .eq(StringUtils.isNotBlank(queryParam.getEnKey()), AgentConfigEntity::getEnKey, queryParam.getEnKey())
                .le(queryParam.getEffectMinVersionNum() != null, AgentConfigEntity::getEffectMinVersionNum,
                    queryParam.getEffectMinVersionNum())
        );
        return CommonUtil.list2list(entityList, AgentConfigDetailResult.class);
    }

    @Override
    public Integer updateConfigValue(UpdateAgentConfigParam updateParam) {
        AgentConfigEntity entity = new AgentConfigEntity();
        entity.setId(updateParam.getId());
        entity.setOperator(updateParam.getOperator());
        entity.setDefaultValue(updateParam.getDefaultValue());
        return agentConfigMapper.updateById(entity);
    }

    @Override
    public Integer deleteById(Long id) {
        return agentConfigMapper.deleteById(id);
    }
}

