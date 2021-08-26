package io.shulie.takin.web.biz.pojo.response.user;

import java.util.List;

import lombok.Data;

/**
 * 菜单操作权限返参
 *
 * @author fanxx
 * @date 2020/11/5 5:55 下午
 */
@Data
public class ResourceActionResponse {

    /**
     * 菜单id
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 功能模块是否开启
     */
    private Boolean checked;

    /**
     * 菜单编码
     */
    private String key;

    /**
     * 功能权限列表
     */
    private List<SingleActionResponse> groupList;
}
