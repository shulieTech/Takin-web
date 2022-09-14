package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PressureResourceRelateRemoteCallMapper
        extends BaseMapper<PressureResourceRelateRemoteCallEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_relate_remote_call(" +
            "resource_id,detail_id,interface_name,interface_type,server_app_name,app_name,type," +
            "mock_return_value,user_id,is_synchronize,md5,interface_child_type,remark,manual_tag," +
            "tenant_id,env_code,gmt_create)" +
            "values " +
            "<foreach collection='list' item='item' index='index' separator=','>" +
            "(#{item.resourceId},#{item.detailId},#{item.interfaceName},#{item.interfaceType},#{item.serverAppName}," +
            "#{item.appName},#{item.type},#{item.mockReturnValue},#{item.userId},#{item.isSynchronize},#{item.md5}," +
            "#{item.interfaceChildType},#{item.remark},#{item.manualTag},#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            "</foreach>" +
            " ON DUPLICATE KEY UPDATE " +
            " shadow_table =values(shadow_table),gmt_modified=now()" +
            "</script>")
    void saveOrUpdate(@Param("list") List<PressureResourceRelateRemoteCallEntity> list);
}
