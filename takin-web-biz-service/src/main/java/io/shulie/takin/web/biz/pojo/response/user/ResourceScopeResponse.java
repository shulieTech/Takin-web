package io.shulie.takin.web.biz.pojo.response.user;

import java.util.List;

import lombok.Data;

/**
 * 菜单数据权限返参
 *
 * @author fanxx
 * @date 2020/11/5 9:30 下午
 */
@Data
public class ResourceScopeResponse {
    /**
     * 菜单id
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 选中的数据范围类型
     */
    private Integer checked;

    /**
     * 菜单编码
     */
    private String key;

    /**
     * 数据范围列表
     */
    private List<SingleScopeResponse> groupList;
}
