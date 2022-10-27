package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.model.mysql.ApplicationDsDbTableEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsDbTableDetailResult;

import java.util.List;

/**
 * 业务数据库表(ApplicationDsDbTable)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-09-15 17:21:41
 */
public interface ApplicationDsDbTableDAO {

    List<ApplicationDsDbTableDetailResult> getList(String url,Long appId,String userName);

    List<ApplicationDsDbTableDetailResult> queryList(String url,Long appId,String userName,String bizTable);

    void batchSave(List<ApplicationDsDbTableDetailResult > list);

    List<ApplicationDsDbTableEntity> batchSave_ext(List<ApplicationDsDbTableDetailResult > list);

    void batchDeleted(List<ApplicationDsDbTableDetailResult > list);

    void batchDeleted_V2(List<Long > list);

    ApplicationDsDbTableEntity getOne(Long id);
}

