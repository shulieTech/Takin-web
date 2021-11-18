package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.agentupgradeonline.ApplicationPluginUpgradeRefDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationPluginUpgradeRefMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginUpgradeRefEntity;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-11-09 20:41:55
 */
@Service
public class ApplicationPluginUpgradeRefDAOImpl extends ServiceImpl<ApplicationPluginUpgradeRefMapper, ApplicationPluginUpgradeRefEntity>
    implements ApplicationPluginUpgradeRefDAO, MPUtil<ApplicationPluginUpgradeRefEntity> {


    private LambdaQueryWrapper<ApplicationPluginUpgradeRefEntity> buildQuery(LambdaQueryWrapper<ApplicationPluginUpgradeRefEntity> LambdaQueryWrapper) {
        if (Objects.isNull(LambdaQueryWrapper)) {
            return this.getLambdaQueryWrapper().eq(ApplicationPluginUpgradeRefEntity::getIsDeleted, 0);
        } else {
            return LambdaQueryWrapper.eq(ApplicationPluginUpgradeRefEntity::getIsDeleted, 0);
        }
    }

    private ApplicationPluginUpgradeRefDetailResult convertVo(ApplicationPluginUpgradeRefEntity entity){
        return Convert.convert(ApplicationPluginUpgradeRefDetailResult.class,entity);
    }

    private List<ApplicationPluginUpgradeRefDetailResult> convertVos(List<ApplicationPluginUpgradeRefEntity> entities){
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }
        return entities.stream().map(this::convertVo).collect(Collectors.toList());
    }


    @Override
    public List<ApplicationPluginUpgradeRefDetailResult> getList(Long pluginId) {
        LambdaQueryWrapper<ApplicationPluginUpgradeRefEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.eq(ApplicationPluginUpgradeRefEntity::getPluginId,pluginId);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<ApplicationPluginUpgradeRefDetailResult> getList(String upgradeBatch) {
        LambdaQueryWrapper<ApplicationPluginUpgradeRefEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.eq(ApplicationPluginUpgradeRefEntity::getUpgradeBatch,upgradeBatch);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<ApplicationPluginUpgradeRefDetailResult> getList(List<String> upgradeBatchs) {
        LambdaQueryWrapper<ApplicationPluginUpgradeRefEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.in(ApplicationPluginUpgradeRefEntity::getUpgradeBatch,upgradeBatchs);
        return this.convertVos(this.list(queryWrapper));
    }
}

