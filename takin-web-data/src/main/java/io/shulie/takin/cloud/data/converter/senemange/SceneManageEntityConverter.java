package io.shulie.takin.cloud.data.converter.senemange;

import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageListResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface SceneManageEntityConverter {

    SceneManageEntityConverter INSTANCE = Mappers.getMapper(SceneManageEntityConverter.class);

    /**
     * 单对象转换
     *
     * @param sceneManageEntity
     * @return -
     */
    SceneManageListResult ofSceneManageEntity(SceneManageEntity sceneManageEntity);
}
