package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceDetailEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PressureResourceDetailMapper
        extends BaseMapper<PressureResourceDetailEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_detail(" +
            "resource_id,app_name,entrance_url,entrance_name,method,rpc_type,extend,type," +
            "link_id,tenant_id,env_code,gmt_create)" +
            "values " +
            "<foreach collection='list' item='item' index='index' separator=','>" +
            "(#{item.resourceId},#{item.appName},#{item.entranceUrl},#{item.entranceName},#{item.method},#{item.rpcType},#{item.extend}," +
            "#{item.type},#{item.linkId},#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            "</foreach>" +
            " ON DUPLICATE KEY UPDATE gmt_modified=now()" +
            "</script>")
    void saveOrUpdate(@Param("list") List<PressureResourceDetailEntity> list);
}
