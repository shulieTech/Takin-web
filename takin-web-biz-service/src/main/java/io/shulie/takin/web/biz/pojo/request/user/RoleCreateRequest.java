package io.shulie.takin.web.biz.pojo.request.user;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/2 4:14 下午
 */
@Data
public class RoleCreateRequest {

    private String roleName;

    private String roleDesc;
}
