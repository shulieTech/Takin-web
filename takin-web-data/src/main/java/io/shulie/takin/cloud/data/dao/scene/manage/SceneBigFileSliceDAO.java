package io.shulie.takin.cloud.data.dao.scene.manage;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneScriptRef;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;

/**
 * @author moriarty
 */
public interface SceneBigFileSliceDAO extends IService<SceneBigFileSliceEntity> {

    /**
     * 创建
     *
     * @param param 入参
     * @return 创建结果
     */
    int create(SceneBigFileSliceParam param);

    /**
     * 判断文件是否是切片的
     *
     * @param param 入参
     * @return 判断结果
     */
    int isFileSliced(SceneBigFileSliceParam param);

    /**
     * 更新信息
     *
     * @param param 入参
     * @return 更新结果
     */
    int update(SceneBigFileSliceParam param);

    /**
     * 查询关联项
     *
     * @param param 入参
     * @return 关联项
     */
    SceneScriptRefEntity selectRef(SceneBigFileSliceParam param);

    /**
     * 更新关联项
     *
     * @param entity 入参
     * @return 更新结果
     */
    int updateRef(SceneScriptRefEntity entity);

    /**
     * 查询一个匹配项
     *
     * @param param 入参
     * @return 匹配项
     */
    SceneBigFileSliceEntity selectOne(SceneBigFileSliceParam param);

    /**
     * 创建关联项
     *
     * @param ref 关联项
     * @return 创建结果
     */
    Long createRef(SceneScriptRef ref);
}
