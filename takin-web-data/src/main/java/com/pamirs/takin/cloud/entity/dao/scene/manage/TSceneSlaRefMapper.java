package com.pamirs.takin.cloud.entity.dao.scene.manage;

import java.util.List;

import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneSlaRef;
import org.apache.ibatis.annotations.Param;

/**
 * 场景&SLA关联 mapper
 *
 * @author -
 */
public interface TSceneSlaRefMapper {

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
     * @param record -
     * @return -
     */
    Long insertSelective(SceneSlaRef record);

    /**
     * 批量插入
     *
     * @param records -
     */
    void batchInsert(@Param("items") List<SceneSlaRef> records);

    /**
     * 依据主键查询
     *
     * @param id 数据主键
     * @return -
     */
    SceneSlaRef selectByPrimaryKey(Long id);

    /**
     * 依据场景主键查询
     *
     * @param sceneId 场景主键
     * @return -
     */
    List<SceneSlaRef> selectBySceneId(Long sceneId);

    /**
     * 依据主键更新
     *
     * @param record 数据内容(包括主键)
     * @return-
     */
    int updateByPrimaryKeySelective(SceneSlaRef record);

}
