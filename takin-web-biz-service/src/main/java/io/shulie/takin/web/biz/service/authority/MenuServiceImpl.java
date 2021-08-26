package io.shulie.takin.web.biz.service.authority;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import io.shulie.takin.web.ext.entity.MenuResponseExt;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.ext.api.auth.WebMenuAuthExtApi;
import io.shulie.takin.plugin.framework.core.PluginManager;

/**
 * 菜单服务
 * <p>
 * 实现类
 *
 * @author 张天赐
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    PluginManager pluginManager;
    @Resource
    BaseConfigService baseConfigService;

    /**
     * 查询菜单项
     *
     * @return 全部菜单项
     */
    @Override
    public List<MenuResponseExt> queryMenuList() {
        // 从数据库获取菜单项配置
        TBaseConfig allMenu = baseConfigService.queryByConfigCode("ALL_MENU");
        // 数据库配置项的校验
        if (allMenu == null || StrUtil.isBlank(allMenu.getConfigValue())) {return new ArrayList<>(0);}
        // 配置项进行实体转换
        else {return JSON.parseArray(allMenu.getConfigValue(), MenuResponseExt.class);}
    }

    /**
     * 查询菜单项(权限)
     *
     * @return 用户所拥有的菜单项
     */
    @Override
    public List<MenuResponseExt> queryUserMenuList() {
        // 获取全部菜单项
        List<MenuResponseExt> menus = queryMenuList();
        // 加载拓展插件
        WebMenuAuthExtApi menuAuthExtApi = pluginManager.getExtension(WebMenuAuthExtApi.class);
        //      如果没有拓展插件，则默认返回数据库配置的全部菜单项
        if (menuAuthExtApi == null) {return menus;}
        //      通过拓展插件对数据库配置的菜单项进行筛选
        else {return menuAuthExtApi.filterMenu(menus);}
    }

    /**
     * 获取用户拥有的菜单项权限<p>
     * 拓展模块
     *
     * @return key:菜单项唯一标识<br/>value:是否拥有权限
     */
    @Override
    public Map<String, Boolean> queryUserMenuKeys() {
        // 加载拓展插件
        WebMenuAuthExtApi menuAuthExtApi = pluginManager.getExtension(WebMenuAuthExtApi.class);
        //      如果没有拓展插件，则默认返回数据库配置的全部菜单项
        if (menuAuthExtApi == null) {
            return queryUserMenuList().stream().map(MenuResponseExt::getKey)
                .distinct().filter(StrUtil::isNotBlank)
                .collect(Collectors.toMap(t -> t, c -> true));
        }
        //      通过拓展插件对数据库配置的菜单项进行筛选
        else {return menuAuthExtApi.queryUserMenuKeys();}
    }

    /**
     * 查询按钮项
     *
     * @return 全部按钮项
     */
    @Override
    public List<String> queryButtonList() {
        // 从数据库获取按钮项配置
        TBaseConfig allMenu = baseConfigService.queryByConfigCode("ALL_BUTTON");
        // 数据库配置项的校验
        if (allMenu == null || StrUtil.isBlank(allMenu.getConfigValue())) {return new ArrayList<>(0);}
        // 配置项进行实体转换
        else {return JSON.parseArray(allMenu.getConfigValue(), String.class);}
    }

    /**
     * 查询按钮项(权限)
     *
     * @return 用户所拥有的操作按钮
     */
    @Override
    public Map<String, Boolean> queryUserButtonList() {
        // 获取全部按钮项
        List<String> buttons = queryButtonList();
        // 加载拓展插件
        WebMenuAuthExtApi menuAuthExtApi = pluginManager.getExtension(WebMenuAuthExtApi.class);
        //      如果没有拓展插件，则默认返回数据库配置的全部按钮项
        if (menuAuthExtApi == null) {
            return buttons.stream().collect(Collectors.toMap(key -> key, value -> true));
        }
        //      通过拓展插件对数据库配置的按钮项进行筛选
        else {return menuAuthExtApi.filterButton(buttons);}
    }
}
