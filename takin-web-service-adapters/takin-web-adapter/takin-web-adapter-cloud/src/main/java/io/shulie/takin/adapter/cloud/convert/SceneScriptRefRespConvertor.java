package io.shulie.takin.adapter.cloud.convert;

import java.util.List;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp.SceneScriptRefResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author mubai
 * @date 2020-10-30 12:54
 */

@Mapper
public interface SceneScriptRefRespConvertor {

    SceneScriptRefRespConvertor INSTANCE = Mappers.getMapper(SceneScriptRefRespConvertor.class);

    /**
     * 数据转换
     *
     * @param output 原数据
     * @return 转换后数据
     */
    SceneManageWrapperResponse.SceneScriptRefResponse of(SceneManageWrapperOutput.SceneScriptRefOutput output);

    /**
     * 数据转换(批量)
     *
     * @param output 原数据
     * @return 转换后数据
     */
    List<SceneScriptRefResp> ofList(List<SceneManageWrapperOutput.SceneScriptRefOutput> output);

}
