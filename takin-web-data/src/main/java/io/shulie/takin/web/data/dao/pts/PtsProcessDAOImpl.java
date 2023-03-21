package io.shulie.takin.web.data.dao.pts;

import java.util.List;
import java.util.stream.Collectors;

import io.shulie.takin.web.data.dao.linkmanage.SceneDAO;
import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author junshi
 * @ClassName PtsProcessDAOImpl
 * @Description
 * @createTime 2023年03月16日 15:28
 */
@Service
public class PtsProcessDAOImpl implements PtsProcessDAO{

    @Autowired
    private SceneDAO sceneDAO;

    @Override
    public Long addProcess(SceneCreateParam param) {
        sceneDAO.insert(param);
        return param.getId();
    }

    @Override
    public void updateProcess(SceneUpdateParam param) {
        sceneDAO.update(param);
    }

    @Override
    public SceneResult getProcessById(Long id) {
        return sceneDAO.getSceneDetail(id);
    }

    @Override
    public boolean existProcessQueryByName(Long processId, SceneQueryParam param) {
        List<SceneResult> resultList = sceneDAO.selectListByName(param);
        if(processId == null) {
            return CollectionUtils.isNotEmpty(resultList);
        }
        List<SceneResult> filterList = resultList.stream().filter(data -> data.getId() != processId).collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(filterList);
    }
}
