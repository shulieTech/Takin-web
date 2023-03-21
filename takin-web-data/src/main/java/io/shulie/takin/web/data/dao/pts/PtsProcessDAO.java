package io.shulie.takin.web.data.dao.pts;

import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;

/**
 * @author junshi
 * @ClassName PtsProcessDAO
 * @Description
 * @createTime 2023年03月16日 15:27
 */
public interface PtsProcessDAO {

    Long addProcess(SceneCreateParam param);

    void updateProcess(SceneUpdateParam param);

    SceneResult getProcessById(Long id);

    boolean existProcessQueryByName(Long processId, SceneQueryParam param);
}
