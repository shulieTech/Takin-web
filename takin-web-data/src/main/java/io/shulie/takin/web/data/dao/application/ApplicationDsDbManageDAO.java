package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.model.mysql.ApplicationDsDbManageEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;

import java.util.List;

/**
 * db连接池影子库表配置表(ApplicationDsDbManage)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-08-30 10:59:59
 */
public interface ApplicationDsDbManageDAO {

    List<ApplicationDsDbManageDetailResult> selectList(Long appId);

    void batchSave(List<ApplicationDsDbManageDetailResult> list);


    ApplicationDsDbManageDetailResult selectOneById(Long id);

    void updateById(Long id,ApplicationDsDbManageEntity entity);
}

