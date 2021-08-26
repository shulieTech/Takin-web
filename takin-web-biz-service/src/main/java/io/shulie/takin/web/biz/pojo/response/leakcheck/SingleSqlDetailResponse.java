package io.shulie.takin.web.biz.pojo.response.leakcheck;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/4 4:06 下午
 */
@Data
public class SingleSqlDetailResponse {
    private Integer order;
    private String sql;
}
