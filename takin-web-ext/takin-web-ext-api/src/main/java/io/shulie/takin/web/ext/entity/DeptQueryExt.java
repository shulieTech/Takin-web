package io.shulie.takin.web.ext.entity;

import lombok.Data;

/**
 * @author by: hezhongqi
 * @date 2021/8/2 21:09
 */
@Data
public class DeptQueryExt {
    /**
     * 部门id
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 上级部门id
     */
    private Long parentId;
}
