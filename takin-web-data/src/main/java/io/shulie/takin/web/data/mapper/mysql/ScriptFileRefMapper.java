package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ScriptFileRefEntity;

public interface ScriptFileRefMapper extends BaseMapper<ScriptFileRefEntity> {

    /**
     * 脚本发布id查询文件路径
     *
     * @param scriptDeployId 脚本发布id
     * @return 文件路径列表
     */
    List<String> listUploadPathByScriptDeployId(Long scriptDeployId);

}