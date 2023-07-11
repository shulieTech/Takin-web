package io.shulie.takin.web.biz.service.scenemanage;

import com.pamirs.takin.entity.domain.entity.scenemanage.SceneScriptRef;
import com.pamirs.takin.entity.domain.query.SceneScriptRefQueryParam;

/**
 * @author mubai
 * @date 2020-05-12 20:22
 */
public interface SceneScriptRefService {

    SceneScriptRef selectByExample(SceneScriptRefQueryParam param);
}
