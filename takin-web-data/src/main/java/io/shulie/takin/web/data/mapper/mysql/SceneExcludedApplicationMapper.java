package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.SceneExcludedApplicationEntity;
import io.shulie.takin.web.data.param.CreateSceneExcludedApplicationParam;
import org.apache.ibatis.annotations.Param;

/**
 * 探针包表(SceneExcludedApplication)表数据库 mapper
 *
 * @author liuchuan
 * @date 2021-10-28 16:21:53
 */
public interface SceneExcludedApplicationMapper extends BaseMapper<SceneExcludedApplicationEntity> {

    /**
     * 批量创建
     *
     * @param createParams 入参
     * @return 影响行数
     */
    int insertBatch(@Param("createParams") List<CreateSceneExcludedApplicationParam> createParams);

}

