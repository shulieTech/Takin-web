package io.shulie.takin.adapter.cloud.convert;

import java.util.List;

import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author mubai
 * @date 2020-11-09 18:50
 */

@Mapper
public interface WarnDetailRespConvertor {
    WarnDetailRespConvertor INSTANCE = Mappers.getMapper(WarnDetailRespConvertor.class);

    /**
     * 转换
     *
     * @param output 入参
     * @return 出参
     */
    WarnDetailResponse of(WarnDetailOutput output);

    /**
     * 批量转换
     *
     * @param warnDetailOutputs 入参
     * @return 出参
     */
    List<WarnDetailResponse> ofList(List<WarnDetailOutput> warnDetailOutputs);
}
