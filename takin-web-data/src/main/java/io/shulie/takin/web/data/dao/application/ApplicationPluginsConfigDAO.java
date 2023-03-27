package io.shulie.takin.web.data.dao.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginsConfigEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;

import java.util.List;

/**
 * (ApplicationPluginsConfig)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-05-18 16:55:22
 */
public interface ApplicationPluginsConfigDAO extends IService<ApplicationPluginsConfigEntity> {
    IPage<ApplicationPluginsConfigEntity> findListPage(ApplicationPluginsConfigParam param);

    List<ApplicationPluginsConfigEntity> findList(ApplicationPluginsConfigParam param);

    void deleteByAppName(String applicationName);
}

