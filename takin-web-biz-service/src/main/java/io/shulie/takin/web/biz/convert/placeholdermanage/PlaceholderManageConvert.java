package io.shulie.takin.web.biz.convert.placeholdermanage;

import io.shulie.takin.web.biz.pojo.response.placeholdermanage.PlaceholderManageResponse;
import io.shulie.takin.web.data.model.mysql.PlaceholderManageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlaceholderManageConvert {
    PlaceholderManageConvert INSTANCE = Mappers.getMapper(PlaceholderManageConvert.class);

    List<PlaceholderManageResponse> ofPlaceholderManageResponse(List<PlaceholderManageEntity> records);
}
