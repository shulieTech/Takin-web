package io.shulie.takin.web.data.dao.linkmanage;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.param.scene.ScenePageQueryParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;

/**
 * @author fanxx
 * @date 2020/11/4 2:56 下午
 */
public interface SceneDAO {
    int insert(SceneCreateParam param);

    /**
     * 指定责任人-业务流程
     *
     * @param updateParam
     * @return
     */
    int allocationUser(SceneUpdateParam updateParam);

    List<SceneResult> selectList(SceneQueryParam queryParam);



    int update(SceneUpdateParam sceneUpdateParam);

    SceneResult getSceneDetail(Long id);

    /**
     * 分页查询业务流程列表
     * @param queryParam
     * @return
     */
    PagingList<SceneResult> selectPageList(ScenePageQueryParam queryParam);
}
