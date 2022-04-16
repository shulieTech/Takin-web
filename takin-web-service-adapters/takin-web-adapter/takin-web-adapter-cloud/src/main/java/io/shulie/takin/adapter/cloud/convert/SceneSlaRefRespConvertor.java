package io.shulie.takin.adapter.cloud.convert;

import java.util.List;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp.SceneSlaRefResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author mubai
 * @date 2020-10-30 12:56
 */

@Mapper
public interface SceneSlaRefRespConvertor {

    SceneSlaRefRespConvertor INSTANCE = Mappers.getMapper(SceneSlaRefRespConvertor.class);

    /**
     * 数据转换
     *
     * @param output 原数据
     * @return 转换后数据
     */
    SceneManageWrapperResponse.SceneSlaRefResponse of(SceneManageWrapperOutput.SceneSlaRefOutput output);

    /**
     * 数据转换(批量)
     *
     * @param output 原数据
     * @return 转换后数据
     */
    List<SceneSlaRefResp> ofList(List<SceneManageWrapperOutput.SceneSlaRefOutput> output);
}
