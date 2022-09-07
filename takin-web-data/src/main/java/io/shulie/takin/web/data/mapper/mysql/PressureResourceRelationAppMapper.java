package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateAppEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PressureResourceRelationAppMapper
        extends BaseMapper<PressureResourceRelateAppEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_relate_app(" +
            "resource_id,detail_id,app_name,status,node_num,join_pressure,type," +
            "tenant_id,env_code,gmt_create)" +
            "values " +
            "<foreach collection='list' item='item' index='index' separator=','>" +
            "(#{item.resourceId},#{item.detailId},#{item.appName},#{item.status},#{item.nodeNum},#{item.joinPressure}," +
            "#{item.type},#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            "</foreach>" +
            " ON DUPLICATE KEY UPDATE gmt_modified=now()" +
            "</script>")
    void saveOrUpdate(@Param("list") List<PressureResourceRelateAppEntity> list);
}
