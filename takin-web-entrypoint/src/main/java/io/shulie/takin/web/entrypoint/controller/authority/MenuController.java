package io.shulie.takin.web.entrypoint.controller.authority;

import java.util.Map;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.ext.entity.MenuResponseExt;
import org.springframework.web.bind.annotation.GetMapping;
import io.shulie.takin.web.biz.service.authority.MenuService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 菜单控制器
 *
 * @author 张天赐
 */
@Slf4j
@RestController
@Api(value = "菜单")
@RequestMapping(ApiUrls.TAKIN_API_URL + "menu")
public class MenuController {
    @Resource
    MenuService menuService;

    /**
     * 查询菜单
     *
     * @return 菜单项
     */
    @GetMapping("list")
    public List<MenuResponseExt> queryMenu() {
        return menuService.queryUserMenuList();
    }

    /**
     * 查询菜单项标识
     *
     * @return key:菜单项标识,value:是否可以访问
     */
    @GetMapping("keys")
    public Map<String, Boolean> queryMenuKeys() {
        return menuService.queryUserMenuKeys();
    }

    /**
     * 查询操作按钮
     *
     * @return 按钮项
     */
    @GetMapping("button")
    public Map<String, Boolean> queryButton() {
        return menuService.queryUserButtonList();
    }
}
