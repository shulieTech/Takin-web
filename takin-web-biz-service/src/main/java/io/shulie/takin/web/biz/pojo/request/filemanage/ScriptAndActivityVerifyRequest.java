package io.shulie.takin.web.biz.pojo.request.filemanage;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.scenemanage.SceneBusinessActivityRef;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneScriptRefOpen;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScriptAndActivityVerifyRequest {

    private boolean isPressure;
    private Long sceneId;
    private String sceneName;
    private Integer scriptType;
    private boolean absolutePath;
    private List<SceneBusinessActivityRef> businessActivityList;
    private List<SceneScriptRefOpen> scriptList;
    private Integer version;
    private ScriptManageDeployDetailResponse deployDetail;
}
