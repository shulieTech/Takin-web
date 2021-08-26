package io.shulie.takin.web.data.param.leakcheck;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 2:32 下午
 */
@Data
public class LeakCheckConfigDetailCreateParam {

    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * sql集合
     */
    private List<String> sqlList;
}
