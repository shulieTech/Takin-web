package io.shulie.takin.web.data.result.leakcheck;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/4 4:43 下午
 */
@Data
public class LeakCheckConfigBatchDetailResult {
    private Long id;

    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * sql内容
     */
    private String sql;
}
