package io.shulie.takin.web.biz.service.scenemanage.impl;

import javax.annotation.Resource;

import com.pamirs.takin.entity.dao.scenemanage.TSceneScriptRefMapper;
import com.pamirs.takin.entity.domain.entity.scenemanage.SceneScriptRef;
import com.pamirs.takin.entity.domain.query.SceneScriptRefQueryParam;
import io.shulie.takin.web.biz.service.scenemanage.SceneScriptRefService;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-05-12 20:23
 */

@Service
public class SceneScriptRefServiceImpl implements SceneScriptRefService {
    @Resource
    private TSceneScriptRefMapper tSceneScriptRefMapper;

    @Override
    public synchronized SceneScriptRef selectByExample(SceneScriptRefQueryParam param) {
        return tSceneScriptRefMapper.selectByExample(param);
    }
}
