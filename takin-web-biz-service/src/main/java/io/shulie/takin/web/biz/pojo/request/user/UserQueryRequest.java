package io.shulie.takin.web.biz.pojo.request.user;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author ZhangXT
 * @date 2020/11/4 14:58
 */
@Data
public class UserQueryRequest extends PagingDevice {

    /**
     * 成员/用户名称
     */
    private String accountName;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 用户id
     */
    //    private List<String> userIds;

}
