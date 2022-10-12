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
    List<Long> createAllowUserIdList();
    List<Long> updateAllowUserIdList();
    List<Long> deleteAllowUserIdList();
    List<Long> enableDisableAllowUserIdList();
    List<Long> startStopAllowUserIdList();
    List<Long> downloadAllowUserIdList();
    List<Long> reportAllowUserIdList();
    List<Long> pigeonholeAllowUserIdList();
    List<Long> shareAllowUserIdList();
    List<Long> copyAllowUserIdList();

    /**
     * 相关操作允许的部门id
     * @return
     */
    List<Long> queryAllowDeptIdList();
    List<Long> createAllowDeptIdList();
    List<Long> updateAllowDeptIdList();
    List<Long> deleteAllowDeptIdList();
    List<Long> enableDisableAllowDeptIdList();
    List<Long> startStopAllowDeptIdList();
    List<Long> downloadAllowDeptIdList();
    List<Long> reportAllowDeptIdList();
    List<Long> pigeonholeAllowDeptIdList();
    List<Long> shareAllowDeptIdList();
    List<Long> copyAllowDeptIdList();
}
