package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import io.shulie.takin.web.data.model.mysql.ClusterNacosConfiguration;

import java.util.List;

/**
 * Ecs中心的nacos服务信息(EcsCenterNacosEntity)表数据库 mapper
 */
public interface ClusterNacosConfigurationMapper {

    /**
     * 查询所有
     *
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ClusterNacosConfiguration> selectAll();

}
