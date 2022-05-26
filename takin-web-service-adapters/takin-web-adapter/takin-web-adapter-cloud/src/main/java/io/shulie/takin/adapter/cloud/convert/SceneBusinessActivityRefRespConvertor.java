package io.shulie.takin.adapter.cloud.convert;

import java.util.List;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp.SceneBusinessActivityRefResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author mubai
 * @date 2020-10-30 12:45
 */
@Mapper
public interface SceneBusinessActivityRefRespConvertor {

    SceneBusinessActivityRefRespConvertor INSTANCE = Mappers.getMapper(SceneBusinessActivityRefRespConvertor.class);

    /**
     * 转化
     *
     * @param output 原数据
     * @return 转换后的数据
     */
    SceneManageWrapperResponse.SceneBusinessActivityRefResponse of(SceneManageWrapperOutput.SceneBusinessActivityRefOutput output);

    /**
     * 批量转换
     *
     * @param list 原数据(批量)
     * @return 转换后的数据(批量)
     */
    List<SceneBusinessActivityRefResp> ofList(List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> list);

}
