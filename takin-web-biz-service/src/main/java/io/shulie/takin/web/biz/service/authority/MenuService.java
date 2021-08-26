package io.shulie.takin.web.biz.service.authority;

import java.util.Map;
import java.util.List;

import io.shulie.takin.web.ext.entity.MenuResponseExt;

/**
 * 菜单服务
 *
 * @author 张天赐
 */
public interface MenuService {

    /**
     * 查询菜单项
     *
     * @return 全部菜单项
     */
    List<MenuResponseExt> queryMenuList();

    /**
     * 查询菜单项(权限)
     *
     * @return 用户所拥有的菜单项
     */
    List<MenuResponseExt> queryUserMenuList();

    /**
     * 获取用户拥有的菜单项权限
     *
     * @return key:菜单项唯一标识<br/>value:是否拥有权限
     */
    Map<String, Boolean> queryUserMenuKeys();

    /**
     * 查询按钮项
     *
     * @return 全部按钮项
     */
    List<String> queryButtonList();

    /**
     * 查询按钮项(权限)
     *
     * @return 用户所拥有的操作按钮
     */
    Map<String, Boolean> queryUserButtonList();
}
