package io.shulie.takin.web.data.dao.scenemanage;

import java.util.List;
import io.shulie.takin.web.data.param.sceneManage.SceneTagRefInsertParam;
import io.shulie.takin.web.data.param.sceneManage.SceneTagRefQueryParam;
import io.shulie.takin.web.data.result.scenemanage.SceneTagRefResult;

/**
 * @author mubai
 * @date 2020-11-30 11:45
 */
public interface SceneTagRefDAO {

    /**
     * 创建场景标签
     *
     * @param paramList
     */
    void createSceneTagRefBatch(List<SceneTagRefInsertParam> paramList);

    void createSceneTagRef(SceneTagRefInsertParam refInsertParam);

    /**
     * 根据场景id查询场景标签关联关系
     *
     * @param sceneId
     * @return
     */
    List<SceneTagRefResult> selectBySceneId(Long sceneId);

    /**
     * 根据场景id删除场景标签关联关系
     *
     * @param sceneId
     */
    void deleteBySceneId(Long sceneId);

    void deleteByIds(List<Long> ids);

    void addSceneTagRef(List<Long> tagIds, Long sceneId);

    List<SceneTagRefResult> selectBySceneIds(List<Long> sceneIds);

    List<SceneTagRefResult> selectByExample(SceneTagRefQueryParam queryParam) ;

}
