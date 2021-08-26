package io.shulie.takin.web.ext.entity;

import java.util.List;

import lombok.Data;

/**
 * 菜单响应体
 *
 * @author 张天赐
 */
@Data
public class MenuResponseExt {
    /**
     * 展示名称
     */
    private String title;
    /**
     * 跳转路径
     */
    private String path;
    /**
     * 菜单项类型
     */
    private String type;
    /**
     * 菜单项图标
     */
    private String icon;
    /**
     * key值
     */
    private String key;
    /**
     * 子级菜单项
     */
    private List<MenuResponseExt> children;

}
