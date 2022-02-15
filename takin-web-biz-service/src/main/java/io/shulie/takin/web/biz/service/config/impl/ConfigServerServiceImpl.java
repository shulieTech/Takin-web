package io.shulie.takin.web.biz.service.config.impl;

import io.shulie.takin.web.biz.pojo.request.config.UpdateConfigServerRequest;
import io.shulie.takin.web.biz.service.config.ConfigServerService;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.data.dao.config.ConfigServerDAO;
import io.shulie.takin.web.data.param.config.UpdateConfigServerParam;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
        UserExt userExt = WebPluginUtils.traceUser();
        // 判断是否是超级管理员
        Assert.isTrue(userExt != null && userExt.getUserType() == 0, "没有权限!");

        // 删除缓存
        RedisHelper.hashDelete(ConfigServerHelper.CONFIG_SERVER_GLOBAL_NOT_TENANT_KEY, updateRequest.getKey());

        // 更新数据库
        UpdateConfigServerParam updateConfigServerParam = new UpdateConfigServerParam();
        updateConfigServerParam.setKey(updateConfigServerParam.getKey());
        updateConfigServerParam.setValue(updateConfigServerParam.getValue());
        configServerDAO.updateGlobalValueByKey(updateConfigServerParam);
    }

}
