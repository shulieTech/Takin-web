package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.agentupgradeonline.WebApplicationPluginUpgradeDAO;
import io.shulie.takin.web.data.mapper.mysql.WebApplicationPluginUpgradeMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginUpgradeEntity;
import io.shulie.takin.web.data.result.agentUpgradeOnline.ApplicationPluginUpgradeDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 应用升级单(ApplicationPluginUpgrade)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-11-09 20:45:03
 */
@Service
public class WebApplicationPluginUpgradeDAOImpl
        extends ServiceImpl<WebApplicationPluginUpgradeMapper, ApplicationPluginUpgradeEntity>
        implements WebApplicationPluginUpgradeDAO, MPUtil<ApplicationPluginUpgradeEntity> {

    @Autowired
    private WebApplicationPluginUpgradeMapper applicationPluginUpgradeMapper;

    @Override
    public ApplicationPluginUpgradeDetailResult queryLatestUpgradeByAppIdAndStatus(Long applicationId, Integer status) {
        ApplicationPluginUpgradeEntity entity = applicationPluginUpgradeMapper.queryLatestUpgradeByAppIdAndStatus(
                applicationId, status);
        if (entity == null) {
            return null;
        }
        ApplicationPluginUpgradeDetailResult result = new ApplicationPluginUpgradeDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }
}

