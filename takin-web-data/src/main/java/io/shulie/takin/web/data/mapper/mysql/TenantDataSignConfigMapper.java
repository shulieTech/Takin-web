package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import io.shulie.takin.web.data.model.mysql.TenantDataSignConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * 租户数据存储签名配置(TenantDataSignConfig)表数据库 mapper
 *
 * @author 南风
 * @date 2022-05-23 15:45:59
 */
public interface TenantDataSignConfigMapper extends BaseMapper<TenantDataSignConfigEntity> {

    Integer clearSign(@Param("tenantIds") List<Long> tenantIds, @Param("envCode")String envCode);

    @InterceptorIgnore(tenantLine = "true")
    List<TenantDataSignConfigEntity> selectList(@Param(Constants.WRAPPER) Wrapper<TenantDataSignConfigEntity> queryWrapper);


}

