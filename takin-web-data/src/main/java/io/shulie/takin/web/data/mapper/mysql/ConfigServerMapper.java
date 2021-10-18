package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ConfigServerEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 配置表-服务的配置(ConfigServer)表数据库 mapper
 *
 * @author liuchuan
 * @date 2021-10-12 11:17:15
 */
public interface ConfigServerMapper extends BaseMapper<ConfigServerEntity> {

    /**
     * 根据 key 筛选用户, 环境下的 value
     * 同样的 key, 全局的也取出来
     *
     * @param key 配置 key
     * @param tenantAppKey 租户 key
     * @param envCode 环境
     * @return 配置值
     */
    List<String> selectTenantEnvValueListByKey(@Param("key") String key, @Param("tenantAppKey") String tenantAppKey, @Param("envCode") String envCode);

}

