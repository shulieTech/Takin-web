package io.shulie.takin.web.data.result.leakcheck;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 2:36 下午
 */
@Data
public class LeakCheckConfigSingleDetailResult {
    private Long id;

    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * 漏数sql
     */
    private List<String> sqls;
}
