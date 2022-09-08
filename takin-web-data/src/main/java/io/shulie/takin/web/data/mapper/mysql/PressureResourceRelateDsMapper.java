package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateAppEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PressureResourceRelateDsMapper
        extends BaseMapper<PressureResourceRelateDsEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_relate_ds(" +
            "resource_id,detail_id,app_name,middleware_name,status,type,business_database,business_user_name," +
            "shadow_database,shadow_user_name,shadow_password,ext_info,unique_key,remark,tenant_id,env_code,gmt_create)" +
            "values " +
            "<foreach collection='list' item='item' index='index' separator=','>" +
            "(#{item.resourceId},#{item.detailId},#{item.appName},#{item.middlewareName},#{item.status},#{item.type}," +
            "#{item.businessDatabase},#{item.businessUserName},#{item.shadowDatabase},#{item.shadowUserName}," +
            "#{item.shadowPassword},#{item.extInfo},#{item.uniqueKey},#{item.remark},#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            "</foreach>" +
            " ON DUPLICATE KEY UPDATE gmt_modified=now()" +
            "</script>")
    void saveOrUpdate(@Param("list") List<PressureResourceRelateDsEntity> list);
}
