package io.shulie.takin.web.biz.pojo.request.user;

import java.util.List;

import lombok.Data;

/**
 * 给用户重置（清空）角色
 *
 * @author ZhangXT
 * @date 2020/11/5 09:57
 */
@Data
public class UserRoleRelationBatchDeleteRequest {
    /**
     * 用户ID
     */
    private List<String> accountIds;
}
