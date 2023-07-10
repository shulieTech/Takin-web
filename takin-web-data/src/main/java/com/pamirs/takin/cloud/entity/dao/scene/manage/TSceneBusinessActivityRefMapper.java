package com.pamirs.takin.cloud.entity.dao.scene.manage;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneBusinessActivityRef;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author -
 */
public interface TSceneBusinessActivityRefMapper extends BaseMapper<SceneBusinessActivityRefEntity> {

    /**
     * 依据主键删除
     *
     * @param id 数据主键
     * @return -
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 依据场景主键删除
     *
     * @param sceneId 场景主键
     * @return -
     */
    int deleteBySceneId(Long sceneId);

    /**
     * 插入
     *
     * @param record 入参
     * @return -
     */
    Long insertSelective(SceneBusinessActivityRef record);

    /**
     * 批量插入
     *
     * @param records 入参
     */
    void batchInsert(@Param("items") List<SceneBusinessActivityRef> records);

    /**
     * 依据主键查询
     *
     * @param id 数据主键
     * @return -
     */
    SceneBusinessActivityRef selectByPrimaryKey(Long id);

    /**
     * 依据场景主键查询
     *
     * @param sceneId 场景主键
     * @return -
     */
    List<SceneBusinessActivityRef> selectBySceneId(Long sceneId);

    /**
     * 根据主键更新
     *
     * @param record 新的数据内容(包含主键)
     * @return -
     */
    int updateByPrimaryKeySelective(SceneBusinessActivityRef record);

    /**
     * 查询场景关联的业务活动
     *
     * @param sceneId            场景主键
     * @param businessActivityId 业务活动主键
     * @return 关联关系
     */
    SceneBusinessActivityRef querySceneBusinessActivityRefByActivityId(@Param("sceneId") Long sceneId,
                                                                       @Param("businessActivityId") Long businessActivityId);

    SceneBusinessActivityRef selectByBindRef(@Param("bindRef") String bindRef, @Param("sceneId") Long sceneId);
}
