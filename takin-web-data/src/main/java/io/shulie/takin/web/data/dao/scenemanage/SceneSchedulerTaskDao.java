package io.shulie.takin.web.data.dao.scenemanage;

import java.util.List;

import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskInsertParam;
import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskQueryParam;
import io.shulie.takin.web.data.param.sceneManage.SceneSchedulerTaskUpdateParam;
import io.shulie.takin.web.data.result.scenemanage.SceneSchedulerTaskResult;

/**
 * @author mubai
 * @date 2020-11-30 21:26
 */

public interface SceneSchedulerTaskDao {

    Long create(SceneSchedulerTaskInsertParam param);

    void delete(Long id);

    SceneSchedulerTaskResult selectBySceneId(Long sceneId);

    void update(SceneSchedulerTaskUpdateParam param);

    void deleteBySceneId(Long sceneId) ;

    List<SceneSchedulerTaskResult> selectBySceneIds(List<Long> sceneIds);

    List<SceneSchedulerTaskResult> selectByExample(SceneSchedulerTaskQueryParam queryParam);
}
