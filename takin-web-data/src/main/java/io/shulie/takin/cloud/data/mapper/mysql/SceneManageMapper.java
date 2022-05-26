package io.shulie.takin.cloud.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;

/**
 * @author -
 */
public interface SceneManageMapper extends BaseMapper<SceneManageEntity> {

    @InterceptorIgnore(tenantLine = "true")
    SceneManageEntity selectById(Long id);
}