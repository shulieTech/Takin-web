package io.shulie.takin.web.data.dao.scenemanage;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.mapper.mysql.GlobalSceneManageMapper;
import io.shulie.takin.web.data.model.mysql.GlobalSceneManageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalSceneManageDAOImpl extends ServiceImpl<GlobalSceneManageMapper, GlobalSceneManageEntity>
        implements GlobalSceneManageDAO{
}
