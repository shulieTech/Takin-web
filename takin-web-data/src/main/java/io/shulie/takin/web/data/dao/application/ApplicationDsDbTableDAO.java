package io.shulie.takin.web.data.dao.application;

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

    void batchSave(List<ApplicationDsDbTableDetailResult > list);


    void batchDeleted(List<ApplicationDsDbTableDetailResult > list);
}

