package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.agentupgradeonline.ApplicationPluginUpgradeDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationPluginUpgradeMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginUpgradeEntity;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用升级单(ApplicationPluginUpgrade)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-11-09 20:45:03
 */
@Service
public class ApplicationPluginUpgradeDAOImpl   extends ServiceImpl<ApplicationPluginUpgradeMapper, ApplicationPluginUpgradeEntity>
    implements ApplicationPluginUpgradeDAO, MPUtil<ApplicationPluginUpgradeEntity> {


    private LambdaQueryWrapper<ApplicationPluginUpgradeEntity> buildQuery(LambdaQueryWrapper<ApplicationPluginUpgradeEntity> LambdaQueryWrapper) {
        if (Objects.isNull(LambdaQueryWrapper)) {
            return this.getLambdaQueryWrapper().eq(ApplicationPluginUpgradeEntity::getIsDeleted, 0);
        } else {
            return LambdaQueryWrapper.eq(ApplicationPluginUpgradeEntity::getIsDeleted, 0);
        }
    }

    private ApplicationPluginUpgradeDetailResult convertVo(ApplicationPluginUpgradeEntity entity){
        return Convert.convert(ApplicationPluginUpgradeDetailResult.class,entity);
    }

    private List<ApplicationPluginUpgradeDetailResult> convertVos(List<ApplicationPluginUpgradeEntity> entities){
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }
        return entities.stream().map(this::convertVo).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationPluginUpgradeDetailResult> getList(Set<String> upgradeBatchs) {
        LambdaQueryWrapper<ApplicationPluginUpgradeEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.in(ApplicationPluginUpgradeEntity::getUpgradeBatch,upgradeBatchs);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<ApplicationPluginUpgradeDetailResult> getListByStatus(Integer status) {
        LambdaQueryWrapper<ApplicationPluginUpgradeEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.eq(ApplicationPluginUpgradeEntity::getPluginUpgradeStatus,status);
        return this.convertVos(this.list(queryWrapper));
    }
}

