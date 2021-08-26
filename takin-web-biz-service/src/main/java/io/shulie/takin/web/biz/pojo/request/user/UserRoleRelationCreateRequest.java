package io.shulie.takin.web.biz.pojo.request.user;

import java.util.List;

import lombok.Data;

/**
 * 给用户分配角色
 *
 * @author ZhangXT
 * @date 2020/11/5 10:02
 */
@Data
public class UserRoleRelationCreateRequest {

    /**
     * 用户ID
     */
    private List<String> accountIds;

    /**
     * 角色ID
     */
    private List<String> roleIds;
}
