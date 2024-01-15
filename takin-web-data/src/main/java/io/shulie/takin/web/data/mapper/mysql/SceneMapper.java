package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import org.springframework.data.repository.query.Param;

public interface SceneMapper extends BaseMapper<SceneEntity> {

    @InterceptorIgnore(tenantLine = "true")
    SceneEntity querySceneByScriptDeployId(@Param(value = "scriptDeployId") Long scriptDeployId);
}
