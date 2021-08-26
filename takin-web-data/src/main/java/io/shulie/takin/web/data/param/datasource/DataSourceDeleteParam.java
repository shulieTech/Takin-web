package io.shulie.takin.web.data.param.datasource;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/30 10:16 上午
 */
@Data
public class DataSourceDeleteParam {
    /**
     * 数据源id集合
     */
    List<Long> idList;
}
