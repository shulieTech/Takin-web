package io.shulie.takin.web.data.dao.application;

import io.shulie.takin.web.data.model.mysql.ApplicationDsCacheManageEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsCacheManageDetailResult;

import java.util.List;

/**
 * 缓存影子库表配置表(ApplicationDsCacheManage)表数据库 dao 层
 *
 * @author 南风
 * @date 2021-08-30 14:39:57
 */
public interface ApplicationDsCacheManageDAO {

    List<ApplicationDsCacheManageDetailResult> selectList(Long appId);

    void batchSave(List<ApplicationDsCacheManageDetailResult> list);

    ApplicationDsCacheManageDetailResult selectOneById(Long id);

    void updateById(Long id, ApplicationDsCacheManageEntity entity);

}

