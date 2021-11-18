package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginUpgradeEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 应用升级单(ApplicationPluginUpgrade)表数据库 mapper
 *
 * @author ocean_wll
 * @date 2021-11-09 20:45:03
 */
public interface ApplicationPluginUpgradeMapper extends BaseMapper<ApplicationPluginUpgradeEntity> {

    /**
     * 根据应用Id及状态查询最新的升级单
     *
     * @param applicationId 应用id
     * @param status        状态
     * @return ApplicationPluginUpgradeEntity
     */
    ApplicationPluginUpgradeEntity queryLatestUpgradeByAppIdAndStatus(@Param("applicationId") Long applicationId,
        @Param("pluginUpgradeStatus") Integer status);

}

