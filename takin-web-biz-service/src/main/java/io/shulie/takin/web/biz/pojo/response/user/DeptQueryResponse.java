package io.shulie.takin.web.biz.pojo.response.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/3/8 10:58 上午
 */
@Data
@ApiModel("部门查询对象")
public class DeptQueryResponse {
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
