package io.shulie.takin.web.data.dao.fastagentaccess.impl;

import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.fastagentaccess.AgentVersionDAO;
import io.shulie.takin.web.data.mapper.mysql.AgentVersionMapper;
import io.shulie.takin.web.data.model.mysql.AgentVersionEntity;
import io.shulie.takin.web.data.param.fastagentaccess.AgentVersionQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.CreateAgentVersionParam;
import io.shulie.takin.web.data.result.fastagentaccess.AgentVersionDetailResult;
import io.shulie.takin.web.data.result.fastagentaccess.AgentVersionListResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * agentVersion dao层
 *
 * @author ocean_wll
 * @date 2021/8/12 4:03 下午
 */
@Service
public class AgentVersionDAOImpl implements AgentVersionDAO, MPUtil<AgentVersionEntity> {

    @Autowired
    private AgentVersionMapper agentVersionMapper;

    @Override
    public PagingList<AgentVersionListResult> page(AgentVersionQueryParam queryParam) {
        Page<AgentVersionEntity> entityPage = agentVersionMapper.selectPage(this.setPage(queryParam),
            this.getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(queryParam.getVersion()), AgentVersionEntity::getVersion,
                    queryParam.getVersion())
                .eq(StringUtils.isNotBlank(queryParam.getFirstVersion()), AgentVersionEntity::getFirstVersion,
                    queryParam.getFirstVersion())
                .orderByDesc(AgentVersionEntity::getVersionNum));
        List<AgentVersionEntity> records = entityPage.getRecords();
        if (entityPage.getTotal() == 0) {
            return PagingList.empty();
        }

        return PagingList.of(CommonUtil.list2list(records, AgentVersionListResult.class), entityPage.getTotal());
    }

    @Override
    public AgentVersionDetailResult selectByVersion(String version) {
        LambdaQueryWrapper<AgentVersionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentVersionEntity::getVersion, version)
            .orderByDesc(AgentVersionEntity::getVersionNum);
        return CommonUtil.copyBeanPropertiesWithNull(agentVersionMapper.selectOne(queryWrapper),
            AgentVersionDetailResult.class);
    }

    @Override
    public void deleteByVersion(String version) {
        LambdaQueryWrapper<AgentVersionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentVersionEntity::getVersion, version);
        agentVersionMapper.delete(wrapper);
    }

    @Override
    public Integer insert(CreateAgentVersionParam createParam) {
        return agentVersionMapper.insert(BeanUtil.copyProperties(createParam, AgentVersionEntity.class));
    }

    @Override
    public List<String> findFirstVersionList() {
        return agentVersionMapper.findFirstVersionList();
    }

    @Override
    public List<String> findAllVersionList() {
        return agentVersionMapper.findAllVersionList();
    }

    @Override
    public AgentVersionDetailResult findMaxVersionAgent() {
        return agentVersionMapper.findMaxVersionAgent();
    }
}
