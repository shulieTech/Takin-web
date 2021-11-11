package io.shulie.takin.web.data.dao.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.ApplicationNodeProbeDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationNodeProbeMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationNodeProbeEntity;
import io.shulie.takin.web.data.param.application.CreateApplicationNodeProbeParam;
import io.shulie.takin.web.data.param.probe.UpdateOperateResultParam;
import io.shulie.takin.web.data.result.application.ApplicationNodeProbeResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 应用节点探针操作表(ApplicationNodeProbe)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-06-03 13:43:18
 */
@Service
public class ApplicationNodeProbeDAOImpl implements ApplicationNodeProbeDAO, MPUtil<ApplicationNodeProbeEntity> {

    @Autowired
    private ApplicationNodeProbeMapper applicationNodeProbeMapper;

    @Override
    public ApplicationNodeProbeResult getByApplicationNameAndAgentId(String applicationName, String agentId) {
        ApplicationNodeProbeEntity applicationNodeProbeEntity =
            applicationNodeProbeMapper.selectOne(this.getCustomerLimitOneLambdaQueryWrapper()
                .select(ApplicationNodeProbeEntity::getId,
                    ApplicationNodeProbeEntity::getOperateId, ApplicationNodeProbeEntity::getOperate,
                    ApplicationNodeProbeEntity::getProbeId, ApplicationNodeProbeEntity::getOperateResult)
                .eq(ApplicationNodeProbeEntity::getApplicationName, applicationName)
                .eq(ApplicationNodeProbeEntity::getAgentId, agentId));
        return CommonUtil.copyBeanPropertiesWithNull(applicationNodeProbeEntity, ApplicationNodeProbeResult.class);
    }

    @Override
    public boolean updateById(UpdateOperateResultParam updateOperateResultParam) {
        ApplicationNodeProbeEntity applicationNodeProbeEntity = new ApplicationNodeProbeEntity();
        BeanUtils.copyProperties(updateOperateResultParam, applicationNodeProbeEntity);
        return SqlHelper.retBool(applicationNodeProbeMapper.updateById(applicationNodeProbeEntity));
    }

    @Override
    public boolean create(CreateApplicationNodeProbeParam createApplicationNodeProbeParam) {
        ApplicationNodeProbeEntity applicationNodeProbeEntity = new ApplicationNodeProbeEntity();
        BeanUtils.copyProperties(createApplicationNodeProbeParam, applicationNodeProbeEntity);
        return SqlHelper.retBool(applicationNodeProbeMapper.insert(applicationNodeProbeEntity));
    }

    @Override
    public List<ApplicationNodeProbeResult> listByApplicationNameAndAgentIds(String applicationName,
        List<String> agentIds) {
        List<ApplicationNodeProbeEntity> applicationNodeProbeEntityList =
            applicationNodeProbeMapper.selectList(this.getCustomerLambdaQueryWrapper()
                .select(ApplicationNodeProbeEntity::getId, ApplicationNodeProbeEntity::getOperate,
                    ApplicationNodeProbeEntity::getAgentId, ApplicationNodeProbeEntity::getOperateResult)
                .eq(ApplicationNodeProbeEntity::getApplicationName, applicationName)
                .in(ApplicationNodeProbeEntity::getAgentId, agentIds));
        return CommonUtil.list2list(applicationNodeProbeEntityList, ApplicationNodeProbeResult.class);
    }

    @Override
    public void delByAppNamesAndOperate(Long tenantId, Integer operate, List<String> appNames) {
        applicationNodeProbeMapper.delete(this.getCustomerLambdaQueryWrapper()
            .eq(ApplicationNodeProbeEntity::getOperate, operate)
            .in(CollectionUtils.isNotEmpty(appNames), ApplicationNodeProbeEntity::getApplicationName, appNames));
    }

    @Override
    public ApplicationNodeProbeResult getByApplicationNameAndAgentIdAndMaxTenantId(String applicationName,
        String agentId, Long tenantId) {
        ApplicationNodeProbeEntity entity = applicationNodeProbeMapper.selectOne(this.getLimitOneLambdaQueryWrapper()
            .select(ApplicationNodeProbeEntity::getId,
            ApplicationNodeProbeEntity::getOperateId, ApplicationNodeProbeEntity::getOperate,
            ApplicationNodeProbeEntity::getProbeId, ApplicationNodeProbeEntity::getOperateResult)
            .eq(ApplicationNodeProbeEntity::getApplicationName, applicationName)
            .eq(tenantId != null, ApplicationNodeProbeEntity::getTenantId, tenantId)
            .eq(ApplicationNodeProbeEntity::getAgentId, agentId)
            .orderByDesc(tenantId == null, ApplicationNodeProbeEntity::getTenantId));
        return CommonUtil.copyBeanPropertiesWithNull(entity, ApplicationNodeProbeResult.class);
    }

}