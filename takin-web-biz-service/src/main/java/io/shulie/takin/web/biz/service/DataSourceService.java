package io.shulie.takin.web.biz.service;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceCreateRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceQueryRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceTestRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceUpdateTagsRequest;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceDetailResponse;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceDictionaryResponse;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceListResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
public interface DataSourceService {
    void createDatasource(DataSourceCreateRequest createRequest);

    void updateDatasource(DataSourceUpdateRequest updateRequest);

    void deleteDatasource(List<Long> datasourceIds);

    PagingList<DatasourceListResponse> listDatasource(DataSourceQueryRequest queryRequest);

    List<DatasourceDictionaryResponse> listDatasourceNoPage();

    DatasourceDetailResponse getDatasource(Long datasourceId);

    List<TagManageResponse> getDatasourceTags();

    void updateDatasourceTags(DataSourceUpdateTagsRequest updateTagsRequest);

    String testConnection(DataSourceTestRequest testRequest);

    List<String> getBizActivitiesName(Long datasourceId);
}
