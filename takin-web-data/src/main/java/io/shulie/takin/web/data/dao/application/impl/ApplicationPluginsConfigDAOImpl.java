package io.shulie.takin.web.data.dao.application.impl;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.application.ApplicationPluginsConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationPluginsConfigMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginsConfigEntity;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * (ApplicationPluginsConfig)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-05-18 16:55:22
 */
@Service
public class ApplicationPluginsConfigDAOImpl extends ServiceImpl<ApplicationPluginsConfigMapper, ApplicationPluginsConfigEntity>
    implements ApplicationPluginsConfigDAO, MPUtil<ApplicationPluginsConfigEntity> {

    @Override
    public IPage<ApplicationPluginsConfigEntity> findListPage(ApplicationPluginsConfigParam param) {
        LambdaQueryWrapper<ApplicationPluginsConfigEntity> wrapper = getLambdaQueryWrapper();
        if (Objects.nonNull(param.getApplicationId())) {
            wrapper.eq(ApplicationPluginsConfigEntity::getApplicationId, param.getApplicationId());
        }
        wrapper.eq(ApplicationPluginsConfigEntity::getIsDeleted, 0);
        return this.page(setPage(param.getCurrentPage(), param.getPageSize()),
            wrapper.eq(ApplicationPluginsConfigEntity::getTenantId, param.getTenantId()));
    }

    @Override
    public List<ApplicationPluginsConfigEntity> findList(ApplicationPluginsConfigParam param) {
        LambdaQueryWrapper<ApplicationPluginsConfigEntity> wrapper = getLambdaQueryWrapper();
        if (Objects.nonNull(param.getApplicationId())) {
            wrapper.eq(ApplicationPluginsConfigEntity::getApplicationId, param.getApplicationId());
        }
        if (Objects.nonNull(param.getTenantId())) {
            wrapper.eq(ApplicationPluginsConfigEntity::getTenantId, param.getTenantId());
        }
        if (Objects.nonNull(param.getApplicationName())) {
            wrapper.eq(ApplicationPluginsConfigEntity::getApplicationName, param.getApplicationName());
        }
        if (Objects.nonNull(param.getConfigKey())) {
            wrapper.eq(ApplicationPluginsConfigEntity::getConfigKey, param.getConfigKey());
        }
        wrapper.eq(ApplicationPluginsConfigEntity::getIsDeleted, 0);
        return this.list(wrapper);
    }
}

