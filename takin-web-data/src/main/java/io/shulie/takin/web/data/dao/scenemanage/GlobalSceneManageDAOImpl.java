package io.shulie.takin.web.data.dao.scenemanage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.mapper.mysql.GlobalSceneManageMapper;
import io.shulie.takin.web.data.model.mysql.GlobalSceneManageEntity;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class GlobalSceneManageDAOImpl extends ServiceImpl<GlobalSceneManageMapper, GlobalSceneManageEntity>
        implements GlobalSceneManageDAO{

    @Resource
    private GlobalSceneManageMapper globalSceneManageMapper;

    @Override
    public List<GlobalSceneManageEntity> pageWithNoEnvCode(Page<GlobalSceneManageEntity> page, String name) {
        return globalSceneManageMapper.pageWithNoEnvCode(page.getCurrent() - 1,page.getSize(),name, WebPluginUtils.traceTenantId());
    }

    @Override
    public int countWithNoEnvCode(Page<GlobalSceneManageEntity> page, String name) {
        return globalSceneManageMapper.countWithNoEnvCode(name,WebPluginUtils.traceTenantId());
    }
}
