package io.shulie.takin.web.data.dao.script;

import io.shulie.takin.web.data.result.scriptmanage.ScriptManageDeployResult;

/**
 * @author liuchuan
 * @date 2021/11/3 9:00 下午
 */
public interface ScriptManageDeployDAO {

    /**
     * 通过id获得脚本实例详情
     *
     * @param scriptDeployId 脚本实例id, 表主键id
     * @return 脚本实例详情
     */
    ScriptManageDeployResult getById(Long scriptDeployId);

}
