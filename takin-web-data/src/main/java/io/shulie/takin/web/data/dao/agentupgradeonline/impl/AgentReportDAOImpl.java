package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.agentupgradeonline.AgentReportDAO;
import io.shulie.takin.web.data.mapper.mysql.AgentReportMapper;
import io.shulie.takin.web.data.model.mysql.AgentReportEntity;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateAgentReportParam;
import io.shulie.takin.web.data.result.application.AgentReportDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 探针心跳数据(AgentReport)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-11-09 20:35:33
 */
@Service
public class AgentReportDAOImpl extends ServiceImpl<AgentReportMapper, AgentReportEntity>
        implements AgentReportDAO, MPUtil<AgentReportEntity> {

    @Resource
    private AgentReportMapper agentReportMapper;

    private LambdaQueryWrapper<AgentReportEntity> buildQuery(LambdaQueryWrapper<AgentReportEntity> LambdaQueryWrapper) {
        if (Objects.isNull(LambdaQueryWrapper)) {
            return this.getLambdaQueryWrapper().eq(AgentReportEntity::getIsDeleted, 0);
        } else {
            return LambdaQueryWrapper.eq(AgentReportEntity::getIsDeleted, 0);
        }
    }

    private AgentReportDetailResult convertVo(AgentReportEntity entity) {
        return Convert.convert(AgentReportDetailResult.class, entity);
    }

    private List<AgentReportDetailResult> convertVos(List<AgentReportEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::convertVo).collect(Collectors.toList());
    }

    @Override
    public List<AgentReportDetailResult> getList(List<Long> appIds) {
        LambdaQueryWrapper<AgentReportEntity> queryWrapper = this.buildQuery(this.getLambdaQueryWrapper());
        queryWrapper.in(AgentReportEntity::getApplicationId, appIds);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<AgentReportDetailResult> getListByStatus(List<Integer> statusList) {
        LambdaQueryWrapper<AgentReportEntity> queryWrapper = this.buildQuery(this.getLambdaQueryWrapper());
        queryWrapper.in(AgentReportEntity::getStatus, statusList);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<AgentReportDetailResult> getList() {
        LambdaQueryWrapper<AgentReportEntity> queryWrapper = this.buildQuery(this.getLambdaQueryWrapper());
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public Integer insertOrUpdate(CreateAgentReportParam createAgentReportParam) {
        AgentReportEntity entity = new AgentReportEntity();
        BeanUtils.copyProperties(createAgentReportParam, entity);
        entity.setTenantId(WebPluginUtils.traceTenantId());
        entity.setEnvCode(WebPluginUtils.traceEnvCode());
        return agentReportMapper.insertOrUpdate(entity);
    }

    @Override
    public void clearExpiredData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(System.currentTimeMillis() - 120 * 1000);

        // 先去数据库查有多少记录
        Integer needDeleteCount = agentReportMapper.selectDeleteCount(date);
        if (needDeleteCount == 0) {
            return;
        }
        int count = needDeleteCount % 100 == 0 ? needDeleteCount / 100 : needDeleteCount / 100 + 1;
        for (int i = 0; i < count; i++) {
            List<Long> needDeleteList = agentReportMapper.selectDeleteIds(date, i * 100, 100);
            if (!CollectionUtils.isEmpty(needDeleteList)) {
                agentReportMapper.deleteByIds(needDeleteList);
            }
        }
    }

    @Override
    public AgentReportDetailResult queryAgentReportDetail(Long applicationId, String agentId) {
        AgentReportEntity entity = agentReportMapper.selectOne(this.getLambdaQueryWrapper()
                .eq(AgentReportEntity::getApplicationId, applicationId)
                .eq(AgentReportEntity::getAgentId, agentId));
        if (entity == null) {
            return null;
        }
        AgentReportDetailResult result = new AgentReportDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public void updateAgentIdById(Long id, String agentId) {
        AgentReportEntity entity = new AgentReportEntity();
        entity.setId(id);
        entity.setAgentId(agentId);
        agentReportMapper.updateById(entity);
    }
}

