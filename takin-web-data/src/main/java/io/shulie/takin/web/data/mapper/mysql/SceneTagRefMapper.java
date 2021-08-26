package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.SceneTagRefEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * @author mubai
 * @date 2020-11-30 11:51
 */
public interface SceneTagRefMapper extends BaseMapper<SceneTagRefEntity> {

    @Delete(" delete from t_scene_tag_ref where scene_id = #{sceneId} ")
    void deleteBySceneId(@Param("sceneId") Long sceneId);
}
