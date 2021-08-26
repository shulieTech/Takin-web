package io.shulie.takin.web.biz.pojo.response.user;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/5 6:01 下午
 */
@Data
public class SingleActionResponse {

    /**
     * 功能权限id
     */
    private String value;

    /**
     * 功能权限名称
     */
    private String label;

    /**
     * 是否被选中，选中true未选中false
     */
    private Boolean checked;
}
