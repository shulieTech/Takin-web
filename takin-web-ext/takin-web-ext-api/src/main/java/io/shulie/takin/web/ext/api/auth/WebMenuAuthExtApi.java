package io.shulie.takin.web.ext.api.auth;

import java.util.List;
import java.util.Map;

import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;
import io.shulie.takin.web.ext.entity.MenuResponseExt;

/**
 * 菜单授权模块拓展接口
 * 用于菜单模块的权限控制
 *
 * @author 张天赐
 */
public interface WebMenuAuthExtApi extends ExtensionPoint {
    /**
     * 过滤菜单项
     *
     * @param menus 全部菜单项
     * @return 过滤后的菜单项
     */
    List<MenuResponseExt> filterMenu(List<? extends MenuResponseExt> menus);

    /**
     * 获取用户拥有的菜单项权限
     *
     * @return key:菜单项唯一标识<br/>value:是否拥有权限
     */
    Map<String, Boolean> queryUserMenuKeys();

    /**
     * 过滤按钮项
     *
     * @param buttons 全部按钮项
     * @return 过滤后的按钮项
     */
    Map<String, Boolean> filterButton(List<String> buttons);
}