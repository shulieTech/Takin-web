package io.shulie.takin.web.biz.service.config.impl;

import io.shulie.takin.web.biz.pojo.request.config.UpdateConfigServerRequest;
import io.shulie.takin.web.biz.service.config.ConfigServerService;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.data.dao.config.ConfigServerDAO;
import io.shulie.takin.web.data.result.config.ConfigServerDetailResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuchuan
 * @date 2021/10/20 2:03 下午
 */
@Service
public class ConfigServerServiceImpl implements ConfigServerService {

    @Autowired
    private ConfigServerDAO configServerDAO;

    @Override
    public void update(UpdateConfigServerRequest updateRequest) {
        // 查出该用户是否有这个配置
        ConfigServerDetailResult configServer = configServerDAO.getTenantEnvConfigByKey(updateRequest.getKey());


        // 如果没有, 查看是否有全局的

        // 如果没有, 则报错

        // 如果有, 则创建

        // 如果有, 直接更新


        // 清除 redis 缓存
        RedisHelper.hashDelete(ConfigServerHelper.getConfigServerRedisKey(WebPluginUtils.traceTenantAppKey(),
            WebPluginUtils.traceEnvCode()));
    }

}
