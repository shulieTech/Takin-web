package com.pamirs.takin.cloud.entity.dao.scene.manage;

import java.util.List;

import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneManage;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 场景 mapper
 *
 * @author -
 */
@Mapper
@Deprecated
public interface TSceneManageMapper {

    /**
     * 依据主键更新
     *
     * @param record 数据内容(包括主键)
     * @return -
     */
    int updateByPrimaryKeySelective(SceneManage record);

    /**
     * 更新状态
     *
     * @param record 入参
     * @return -
     */
    int updateStatus(UpdateStatusBean record);

    /**
     * 查询所有场景信息
     *
     * @return -
     */
    List<SceneManage> selectAllSceneManageList();

    /**
     * 依据主键集合查询
     *
     * @param ids 主键集合
     * @return 查询结果
     */
    List<SceneManage> getByIds(@Param("ids") List<Long> ids);

}
