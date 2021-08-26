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
     * 获取查询权限用户
     * todo 后续废弃
     *
     * @return
     */
    List<Long> getQueryAllowUserIdList();

    /**
     * 获取更新权限用户
     * todo 后续废弃
     *
     * @return
     */
    List<Long> getUpdateAllowUserIdList();

    /**
     * 获取删除权限用户
     * todo 后续废弃
     *
     * @return
     */
    List<Long> getDeleteAllowUserIdList();

    /**
     * 获取启动关闭权限用户
     * todo 后续废弃
     *
     * @return
     */
    List<Long> getEnableDisableAllowUserIdList();

    List<Long> getDownloadAllowUserIdList();

    List<Long> getStartStopAllowUserIdList();
}
