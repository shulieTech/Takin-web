package com.pamirs.takin.cloud.entity.dao.scene.manage;

import java.util.List;

import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.cloud.entity.domain.query.SceneScriptRefQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 场景&脚本关联 mapper
 *
 * @author -
 */
@Mapper
public interface TSceneScriptRefMapper {

    /**
     * 依据数据主键删除
     *
     * @param id 数据主键
     * @return -
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 依据数据主键集合删除
     *
     * @param ids 主键集合
     * @return -
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 插入
     *
     * @param record -
     * @return -
     */
    Long insertSelective(SceneScriptRef record);

    /**
     * 批量插入
     *
     * @param records -
     */
    void batchInsert(@Param("items") List<SceneScriptRef> records);

    /**
     * 依据数据主键查询
     *
     * @param id 数据主键
     * @return -
     */
    SceneScriptRef selectByPrimaryKey(Long id);

    /**
     * 依据场景主键和脚本类型查询
     *
     * @param sceneId    场景主键
     * @param scriptType 脚本类型
     * @return -
     */
    List<SceneScriptRef> selectBySceneIdAndScriptType(@Param("sceneId") Long sceneId,
        @Param("scriptType") Integer scriptType);

    /**
     * 依据主键更新
     *
     * @param record 数据内容(包括主键)
     * @return -
     */
    int updateByPrimaryKeySelective(SceneScriptRef record);

    /**
     * 依据条件查询
     *
     * @param param 条件
     * @return -
     */
    SceneScriptRef selectByExample(SceneScriptRefQueryParam param);

    /**
     * 依据场景主键删除
     *
     * @param id 场景主键
     */
    void deleteBySceneId(@Param("id") Long id);

}
