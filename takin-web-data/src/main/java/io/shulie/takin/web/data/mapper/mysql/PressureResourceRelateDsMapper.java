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
            "resource_id,detail_id,app_name,middleware_name,middleware_type,status,type,business_database,business_user_name," +
            "shadow_database,shadow_user_name,shadow_password,ext_info,unique_key,remark,tenant_id,env_code,gmt_create)" +
            "values " +
            "(#{item.resourceId},#{item.detailId},#{item.appName},#{item.middlewareName},#{item.middlewareType},#{item.status},#{item.type}," +
            "#{item.businessDatabase},#{item.businessUserName},#{item.shadowDatabase},#{item.shadowUserName}," +
            "#{item.shadowPassword},#{item.extInfo},#{item.uniqueKey},#{item.remark},#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            " ON DUPLICATE KEY UPDATE gmt_modified=now()" +
            "<if test=\"item.middlewareName !=null and item.middlewareName !=''\"> " +
            "   ,middleware_name =values(middleware_name)" +
            "</if>" +
            "<if test=\"item.middlewareType !=null and item.middlewareType !=''\"> " +
            "   ,middleware_type =values(middleware_type)" +
            "</if>" +
            "<if test=\"item.businessUserName !=null and item.businessUserName !=''\"> " +
            "   ,business_user_name =values(business_user_name)" +
            "</if>" +
            "</script>")
    void saveOrUpdate(@Param("item") PressureResourceRelateDsEntity item);
}
