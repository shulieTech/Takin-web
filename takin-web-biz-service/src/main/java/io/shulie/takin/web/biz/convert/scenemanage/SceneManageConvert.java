package io.shulie.takin.web.biz.convert.scenemanage;

import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneScriptRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneSlaRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.TimeVO;
import io.shulie.takin.cloud.sdk.model.common.TimeBean;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SceneManageConvert {
    SceneManageConvert INSTANCE = Mappers.getMapper(SceneManageConvert.class);

    List<SceneBusinessActivityRefVO> ofBusinessActivityConfig(List<SceneManageWrapperResp.SceneBusinessActivityRefResp> businessActivityConfig);

    TimeVO ofTimeVO(TimeBean pressureTestTime);

    List<SceneScriptRefVO> ofSceneScriptRefVO(List<SceneManageWrapperResp.SceneScriptRefResp> uploadFile);

    List<SceneSlaRefVO> ofSceneSlaRefVO(List<SceneManageWrapperResp.SceneSlaRefResp> stopCondition);
}
