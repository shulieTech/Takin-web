package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntityV2;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface PressureResourceRelateRemoteCallMapperV2
        extends BaseMapper<PressureResourceRelateRemoteCallEntityV2> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_relate_remote_call_v2(" +
            "resource_id,detail_id,interface_name,interface_type,app_name,pass," +
            "interface_child_type,status,remark,manual_tag," +
            "tenant_id,env_code,rpc_id,gmt_create,md5)" +
            "values " +
            "(#{item.resourceId},#{item.detailId},#{item.interfaceName},#{item.interfaceType}," +
            "#{item.appName},#{item.pass},#{item.interfaceChildType},#{item.status}," +
            "#{item.remark},#{item.manualTag},#{item.tenantId},#{item.envCode}," +
            "#{item.rpcId},#{item.gmtCreate},#{item.md5})" +
            " ON DUPLICATE KEY UPDATE gmt_modified=now()" +
            "<if test=\"item.pass !=null\"> " +
            "   ,pass =values(pass)" +
            "</if>" +
            "</script>")
    void saveOrUpdate(@Param("item") PressureResourceRelateRemoteCallEntityV2 item);
}
