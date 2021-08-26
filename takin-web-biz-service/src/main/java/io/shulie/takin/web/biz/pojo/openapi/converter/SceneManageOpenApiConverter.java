package io.shulie.takin.web.biz.pojo.openapi.converter;

import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper(imports = {StringUtils.class})
public interface SceneManageOpenApiConverter {

    SceneManageOpenApiConverter INSTANCE = Mappers.getMapper(SceneManageOpenApiConverter.class);

}
