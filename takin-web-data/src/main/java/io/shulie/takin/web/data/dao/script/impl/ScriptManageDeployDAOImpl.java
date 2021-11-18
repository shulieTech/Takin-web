package io.shulie.takin.web.data.dao.script.impl;

import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.script.ScriptManageDeployDAO;
import io.shulie.takin.web.data.mapper.mysql.ScriptManageDeployMapper;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageDeployResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuchuan
 * @date 2021/10/26 11:17 上午
 */
@Service
public class ScriptManageDeployDAOImpl implements ScriptManageDeployDAO {

    @Autowired
    private ScriptManageDeployMapper scriptManageDeployMapper;

    @Override
    public ScriptManageDeployResult getById(Long scriptDeployId) {
        return CommonUtil.copyBeanPropertiesWithNull(scriptManageDeployMapper.selectById(scriptDeployId),
            ScriptManageDeployResult.class);
    }

}
