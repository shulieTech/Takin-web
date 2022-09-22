package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface PressureResourceRelateRemoteCallMapper
        extends BaseMapper<PressureResourceRelateRemoteCallEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_relate_remote_call(" +
            "resource_id,detail_id,interface_name,interface_type,server_app_name,app_name,pass,type," +
            "mock_return_value,user_id,is_synchronize,interface_child_type,status,remark,manual_tag," +
            "tenant_id,env_code,md5,rpc_id,gmt_create)" +
            "values " +
            "(#{item.resourceId},#{item.detailId},#{item.interfaceName},#{item.interfaceType},#{item.serverAppName}," +
            "#{item.appName},#{item.pass},#{item.type},#{item.mockReturnValue},#{item.userId},#{item.isSynchronize}," +
            "#{item.interfaceChildType},#{item.status},#{item.remark},#{item.manualTag},#{item.tenantId},#{item.envCode}," +
            "#{item.md5},#{item.rpcId},#{item.gmtCreate})" +
            " ON DUPLICATE KEY UPDATE gmt_modified=now()" +
            "<if test=\"item.serverAppName !=null and item.serverAppName !=''\"> " +
            "   ,server_app_name =values(server_app_name)" +
            "</if>" +
            "<if test=\"item.type !=null\"> " +
            "   ,type =values(type)" +
            "</if>" +
            "<if test=\"item.pass !=null\"> " +
            "   ,pass =values(pass)" +
            "</if>" +
            "</script>")
    void saveOrUpdate(@Param("item") PressureResourceRelateRemoteCallEntity item);
}
