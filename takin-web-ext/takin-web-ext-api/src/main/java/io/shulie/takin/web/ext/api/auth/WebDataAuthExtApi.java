package io.shulie.takin.web.ext.api.auth;

import java.util.List;

import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;

/**
 * @author fanxx
 * @date 2021/8/2 4:32 下午
 * 数据权限，用于控制数据模块的权限
 */
public interface WebDataAuthExtApi extends ExtensionPoint {

    /**
     * 相关操作允许的用户id
     * @return
     */
    List<Long> queryAllowUserIdList();
    List<Long> updateAllowUserIdList();
    List<Long> deleteAllowUserIdList();
    List<Long> enableDisableAllowUserIdList();
    List<Long> startStopAllowUserIdList();
    List<Long> downloadAllowUserIdList();

    /**
     * 相关操作允许的部门id
     * @return
     */
    List<Long> queryAllowDeptIdList();
    List<Long> updateAllowDeptIdList();
    List<Long> deleteAllowDeptIdList();
    List<Long> enableDisableAllowDeptIdList();
    List<Long> startStopAllowDeptIdList();
    List<Long> downloadAllowDeptIdList();
}
