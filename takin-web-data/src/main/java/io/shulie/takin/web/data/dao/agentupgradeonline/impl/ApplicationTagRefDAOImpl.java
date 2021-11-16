package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.agentupgradeonline.ApplicationTagRefDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationTagRefMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationTagRefEntity;
import io.shulie.takin.web.data.result.application.ApplicationTagRefDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用标签表(ApplicationTagRef)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-11-09 20:48:38
 */
@Service
public class ApplicationTagRefDAOImpl extends ServiceImpl<ApplicationTagRefMapper, ApplicationTagRefEntity>
        implements ApplicationTagRefDAO, MPUtil<ApplicationTagRefEntity> {

    private LambdaQueryWrapper<ApplicationTagRefEntity> buildQuery(LambdaQueryWrapper<ApplicationTagRefEntity> LambdaQueryWrapper) {
        if (Objects.isNull(LambdaQueryWrapper)) {
            return this.getLambdaQueryWrapper().eq(ApplicationTagRefEntity::getIsDeleted, 0);
        } else {
            return LambdaQueryWrapper.eq(ApplicationTagRefEntity::getIsDeleted, 0);
        }
    }

    private ApplicationTagRefDetailResult convertVo(ApplicationTagRefEntity entity){
        return Convert.convert(ApplicationTagRefDetailResult.class,entity);
    }

    private List<ApplicationTagRefDetailResult> convertVos(List<ApplicationTagRefEntity> entities){
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }
        return entities.stream().map(this::convertVo).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationTagRefDetailResult> getList(List<Long> applicationIds) {
        LambdaQueryWrapper<ApplicationTagRefEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.in(ApplicationTagRefEntity::getApplicationId,applicationIds);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<ApplicationTagRefDetailResult> getList(Long tagId) {
        LambdaQueryWrapper<ApplicationTagRefEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        queryWrapper.eq(ApplicationTagRefEntity::getTagId,tagId);
        return this.convertVos(this.list(queryWrapper));
    }

    @Override
    public List<ApplicationTagRefDetailResult> getListByTenant() {
        LambdaQueryWrapper<ApplicationTagRefEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        return this.convertVos(this.list(queryWrapper));
    }
}

