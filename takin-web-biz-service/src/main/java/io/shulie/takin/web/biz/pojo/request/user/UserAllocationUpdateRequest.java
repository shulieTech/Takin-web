package io.shulie.takin.web.biz.pojo.request.user;

import lombok.Data;

/**
 * 指派负责人
 *
 * @author ZhangXT
 * @date 2020/11/5 15:26
 */
@Data
public class UserAllocationUpdateRequest {
    /**
     * 数据ID
     */
    private Long dataId;
    /**
     * 菜单code
     */
    private String menuCode;
    /**
     * 用户id
     */
    private Long userId;

}
