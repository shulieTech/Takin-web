package io.shulie.takin.web.data.dao.application.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.constant.GuardEnableConstants;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.dao.application.LinkGuardDAO;
import io.shulie.takin.web.data.mapper.mysql.LinkGuardMapper;
import io.shulie.takin.web.data.model.mysql.LinkGuardEntity;
import io.shulie.takin.web.data.param.application.LinkGuardCreateParam;
import io.shulie.takin.web.data.param.application.LinkGuardUpdateUserParam;
import io.shulie.takin.web.data.result.linkguard.LinkGuardResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 挡板dao
 *
 * @author fanxx
 * @date 2020/11/4 8:45 下午
 */
@Service
public class LinkGuardDAOImpl extends ServiceImpl<LinkGuardMapper, LinkGuardEntity> implements LinkGuardDAO, MPUtil<LinkGuardEntity> {

    @Override
    public int insert2(LinkGuardCreateParam param) {
        LinkGuardEntity entity = new LinkGuardEntity();
        BeanUtils.copyProperties(param, entity);
        if (param.getIsEnable() != null) {
            entity.setIsEnable(param.getIsEnable() ? GuardEnableConstants.GUARD_ENABLE : GuardEnableConstants.GUARD_UNABLE);
        }
        return this.getBaseMapper().insert(entity);
    }

    @Override
    public List<LinkGuardResult> selectByAppNameUnderCurrentUser(String appName) {
        List<LinkGuardResult> results = new ArrayList<>();
        LambdaQueryWrapper<LinkGuardEntity> wrapper = new LambdaQueryWrapper<>();
        if (WebPluginUtils.checkUserData()) {
            wrapper.eq(LinkGuardEntity::getCustomerId, WebPluginUtils.getTenantId());
        }
        wrapper.eq(LinkGuardEntity::getApplicationName, appName);
        List<LinkGuardEntity> linkGuardEntities = this.getBaseMapper().selectList(wrapper);
        if (CollectionUtils.isEmpty(linkGuardEntities)) {
            return results;
        }
        return linkGuardEntities.stream().map(item -> {
            LinkGuardResult target = new LinkGuardResult();
            BeanUtils.copyProperties(item, target);
            // 启动
            target.setIsEnable(item.getIsEnable() == GuardEnableConstants.GUARD_ENABLE);
            return target;
        }).collect(Collectors.toList());
    }

    @Override
    public int allocationUser(LinkGuardUpdateUserParam param) {
        if (Objects.isNull(param.getApplicationId())) {
            return 0;
        }
        LambdaQueryWrapper<LinkGuardEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LinkGuardEntity::getApplicationId, param.getApplicationId());
        List<LinkGuardEntity> linkGuardEntityList = this.getBaseMapper().selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(linkGuardEntityList)) {
            for (LinkGuardEntity entity : linkGuardEntityList) {
                entity.setUserId(param.getUserId());
                this.getBaseMapper().updateById(entity);
            }
        }
        return 0;
    }

    @Override
    public List<LinkGuardEntity> listFromExportByApplicationId(Long applicationId) {
        return this.list(this.getLambdaQueryWrapper().select(LinkGuardEntity::getId, LinkGuardEntity::getMethodInfo,
                LinkGuardEntity::getGroovy, LinkGuardEntity::getIsEnable, LinkGuardEntity::getRemark)
            .eq(LinkGuardEntity::getApplicationId, applicationId));
    }

    @Override
    public void updateAppName(Long applicationId, String appName) {
        LambdaUpdateWrapper<LinkGuardEntity> wrapper = this.getLambdaUpdateWrapper();
        wrapper.eq(LinkGuardEntity::getApplicationId, applicationId);
        wrapper.set(LinkGuardEntity::getApplicationName, appName);
        this.update(wrapper);
    }

}
