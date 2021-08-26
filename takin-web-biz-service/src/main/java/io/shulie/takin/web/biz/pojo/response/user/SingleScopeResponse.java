package io.shulie.takin.web.biz.pojo.response.user;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/5 9:31 下午
 */
@Data
public class SingleScopeResponse {
    /**
     * 数据范围类型id
     */
    private Integer value;

    /**
     * 数据范围类型名称
     */
    private String label;
}
