package io.shulie.takin.web.data.dao.scenemanage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.GlobalSceneManageEntity;

import java.util.List;

public interface GlobalSceneManageDAO extends IService<GlobalSceneManageEntity> {
    List<GlobalSceneManageEntity> pageWithNoEnvCode(Page<GlobalSceneManageEntity> page, String wrapper);

    int countWithNoEnvCode(Page<GlobalSceneManageEntity> page, String name);
}
