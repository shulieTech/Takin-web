package io.shulie.takin.web.data.dao.agent;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.AgentPluginMapper;
import io.shulie.takin.web.data.model.mysql.AgentPluginEntity;
import io.shulie.takin.web.data.result.agent.AgentPluginResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/10/13 10:59 上午
 */
@Component
public class AgentPluginDAOImpl implements AgentPluginDAO {

    @Autowired
    private AgentPluginMapper agentPluginMapper;

    @Override
    public List<AgentPluginResult> getAgentPluginList() {
        List<AgentPluginResult> agentPluginResultList = Lists.newArrayList();
        LambdaQueryWrapper<AgentPluginEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                AgentPluginEntity::getId,
                AgentPluginEntity::getPluginType,
                AgentPluginEntity::getPluginName
        );
        List<AgentPluginEntity> agentPluginEntityList = agentPluginMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(agentPluginEntityList)) {
            return agentPluginResultList;
        }
        agentPluginResultList = agentPluginEntityList.stream().map(agentPluginEntity -> {
            AgentPluginResult agentPluginResult = new AgentPluginResult();
            agentPluginResult.setId(agentPluginEntity.getId());
            agentPluginResult.setPluginType(agentPluginEntity.getPluginType());
            agentPluginResult.setPluginName(agentPluginEntity.getPluginName());
            return agentPluginResult;
        }).collect(Collectors.toList());
        return agentPluginResultList;
    }
}
