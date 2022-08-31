package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.GlobalSceneManageEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GlobalSceneManageMapper extends BaseMapper<GlobalSceneManageEntity> {
    List<GlobalSceneManageEntity> pageWithNoEnvCode(@Param("current") long current, @Param("size") long size, @Param("name") String name,@Param("tenantId")  Long tenantId);

    int countWithNoEnvCode(String name);
}
