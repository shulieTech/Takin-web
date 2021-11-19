package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.excel.util.CollectionUtils;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.agentupgradeonline.AgentReportDAO;
import io.shulie.takin.web.data.mapper.mysql.AgentReportMapper;
import io.shulie.takin.web.data.model.mysql.AgentReportEntity;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateAgentReportParam;
import io.shulie.takin.web.data.result.application.AgentReportDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
        LambdaQueryWrapper<AgentReportEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.in(AgentReportEntity::getApplicationId, appIds);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<AgentReportDetailResult> getListByStatus(List<Integer> statusList) {
        LambdaQueryWrapper<AgentReportEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.in(AgentReportEntity::getStatus, statusList);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<AgentReportDetailResult> getList() {
        LambdaQueryWrapper<AgentReportEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public Integer insertOrUpdate(CreateAgentReportParam createAgentReportParam) {
        AgentReportEntity entity = new AgentReportEntity();
        BeanUtils.copyProperties(createAgentReportParam, entity);
        return agentReportMapper.insertOrUpdate(entity);
    }

    @Override
    public void clearExpiredData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        agentReportMapper.delete(this.getLambdaQueryWrapper()
            .lt(AgentReportEntity::getGmtUpdate, simpleDateFormat.format(System.currentTimeMillis() - 5 * 60 * 1000)));
    }
}

