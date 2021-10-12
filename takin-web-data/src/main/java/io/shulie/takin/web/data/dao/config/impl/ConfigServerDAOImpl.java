package io.shulie.takin.web.data.dao.config.impl;

import io.shulie.takin.web.data.dao.config.ConfigServerDAO;
import io.shulie.takin.web.data.model.mysql.ConfigServerEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * 配置表-服务的配置(ConfigServer)表数据库 dao 层实现
 *
 * @author liuchuan
 * @date 2021-10-12 11:17:20
 */
@Service
public class ConfigServerDAOImpl implements ConfigServerDAO, MPUtil<ConfigServerEntity> {

}

