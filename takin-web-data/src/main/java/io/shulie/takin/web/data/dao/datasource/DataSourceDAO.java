package io.shulie.takin.web.data.dao.datasource;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.datasource.DataSourceCreateParam;
import io.shulie.takin.web.data.param.datasource.DataSourceDeleteParam;
import io.shulie.takin.web.data.param.datasource.DataSourceQueryParam;
import io.shulie.takin.web.data.param.datasource.DataSourceSingleQueryParam;
import io.shulie.takin.web.data.param.datasource.DataSourceUpdateParam;
import io.shulie.takin.web.data.result.datasource.DataSourceResult;

/**
 * @author fanxx
 * @date 2020/12/30 9:55 上午
 */
public interface DataSourceDAO {

    /**
     * 分页查询数据源列表
     *
     * @param queryParam
     * @return
     */
    PagingList<DataSourceResult> selectPage(DataSourceQueryParam queryParam);

    /**
     * 新增数据源
     *
     * @param createParam
     * @return
     */
    int insert(DataSourceCreateParam createParam);

    /**
     * 更新数据源
     *
     * @param updateParam
     * @return
     */
    int update(DataSourceUpdateParam updateParam);

    /**
     * 删除数据源
     *
     * @param deleteParam
     * @return
     */
    int delete(DataSourceDeleteParam deleteParam);

    /**
     * 查询单个数据源信息
     *
     * @param queryParam
     * @return
     */
    DataSourceResult selectSingle(DataSourceSingleQueryParam queryParam);

    /**
     * 批量查询数据源信息
     *
     * @param queryParam
     * @return
     */
    List<DataSourceResult> selectList(DataSourceQueryParam queryParam);
}
