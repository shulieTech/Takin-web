package io.shulie.takin.web.data.dao.agent;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.AgentPluginLibSupportMapper;
import io.shulie.takin.web.data.model.mysql.AgentPluginLibSupportEntity;
import io.shulie.takin.web.data.result.agent.AgentPluginLibSupportResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author fanxx
 * @date 2020/10/13 11:00 上午
 */
@Component
public class AgentPluginLibSupportDAOImpl implements io.shulie.takin.web.data.dao.agent.AgentPluginLibSupportDAO {

    @Autowired
    private AgentPluginLibSupportMapper agentPluginLibSupportMapper;

    @Override
    public List<AgentPluginLibSupportResult> getAgentPluginLibSupportList() {
        List<AgentPluginLibSupportResult> agentPluginLibSupportResultList = Lists.newArrayList();
        LambdaQueryWrapper<AgentPluginLibSupportEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            AgentPluginLibSupportEntity::getId,
            AgentPluginLibSupportEntity::getPluginId,
            AgentPluginLibSupportEntity::getLibName,
            AgentPluginLibSupportEntity::getLibVersionRegexp,
            AgentPluginLibSupportEntity::getIsIgnore
        );
        List<AgentPluginLibSupportEntity> agentPluginLibSupportEntityList = agentPluginLibSupportMapper
            .selectList(wrapper);
        if (CollectionUtils.isEmpty(agentPluginLibSupportEntityList)) {
            return agentPluginLibSupportResultList;
        }
        agentPluginLibSupportResultList = agentPluginLibSupportEntityList.stream().map(
            agentPluginLibSupportEntity -> {
                AgentPluginLibSupportResult agentPluginLibSupportResult = new AgentPluginLibSupportResult();
                agentPluginLibSupportResult.setId(agentPluginLibSupportEntity.getId());
                agentPluginLibSupportResult.setPluginId(agentPluginLibSupportEntity.getPluginId());
                agentPluginLibSupportResult.setLibName(agentPluginLibSupportEntity.getLibName());
                agentPluginLibSupportResult.setLibVersionRegexp(agentPluginLibSupportEntity.getLibVersionRegexp());
                agentPluginLibSupportResult.setIsIgnore(agentPluginLibSupportEntity.getIsIgnore());
                return agentPluginLibSupportResult;
            }).collect(Collectors.toList());
        return agentPluginLibSupportResultList;
    }
}
