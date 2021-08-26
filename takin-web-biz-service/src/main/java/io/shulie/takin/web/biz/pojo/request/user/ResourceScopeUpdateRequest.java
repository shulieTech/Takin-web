package io.shulie.takin.web.biz.pojo.request.user;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.biz.pojo.response.user.ResourceScopeResponse;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/5 9:29 下午
 */
@Data
public class ResourceScopeUpdateRequest {

    @NotNull(message = "roleId不为空")
    private Long roleId;

    private List<ResourceScopeResponse> dataPermissionList;
}
