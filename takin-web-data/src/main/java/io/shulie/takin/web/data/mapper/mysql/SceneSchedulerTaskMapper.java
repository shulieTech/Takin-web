package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.SceneSchedulerTaskEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author mubai
 * @date 2020-11-30 21:25
 */
public interface SceneSchedulerTaskMapper  extends BaseMapper<SceneSchedulerTaskEntity> {

    @Select("select * from t_scene_scheduler_task where scene_id =#{sceneId} and is_deleted =0 ")
    SceneSchedulerTaskEntity selectBySceneId(@Param("sceneId")Long sceneId) ;

    @Delete(" delete from t_scene_scheduler_task where scene_id =#{sceneId} ")
    void deleteBySceneId(@Param("sceneId") Long sceneId ) ;
}
