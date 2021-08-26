package io.shulie.takin.web.data.dao.datasource;

import java.util.List;

import io.shulie.takin.web.data.result.datasource.DataSourceTagRefResult;

/**
 * @author fanxx
 * @date 2020/12/29 4:22 下午
 */
public interface DataSourceTagRefDAO {
    /**
     * 新增数据源和tag关联关系
     *
     * @param tagIds
     * @param dataSourceId
     */
    void addDataSourceTagRef(List<Long> tagIds, Long dataSourceId);

    /**
     * 根据数据源id查询关联关系
     *
     * @param dataSourceId
     * @return
     */
    List<DataSourceTagRefResult> selectDataSourceTagRefByScriptId(Long dataSourceId);

    /**
     * 批量删除关联关系
     *
     * @param dataSourceTagRefIds
     */
    void deleteByIds(List<Long> dataSourceTagRefIds);

    /**
     * 根据tagId列表查询数据源和tag的关联关系
     *
     * @param tagIds
     * @return
     */
    List<DataSourceTagRefResult> selectDataSourceTagRefByTagIds(List<Long> tagIds);

    /**
     * 根据数据源id批量查询标签
     *
     * @param dataSourceIds
     * @return
     */
    List<DataSourceTagRefResult> selectTagRefByDataSourceIds(List<Long> dataSourceIds);

    /**
     * 根据数据源id删除关联关系
     *
     * @param dataSourceId
     */
    void deleteByDataSourceId(Long dataSourceId);
}
