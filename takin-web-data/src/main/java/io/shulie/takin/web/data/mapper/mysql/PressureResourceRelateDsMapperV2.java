package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntityV2;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface PressureResourceRelateDsMapperV2
        extends BaseMapper<PressureResourceRelateDsEntityV2> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert ignore into t_pressure_resource_relate_ds_v2(" +
            "resource_id,detail_id,app_name,business_database,relate_id,type,tenant_id,env_code,gmt_create)" +
            "values " +
            "(#{item.resourceId},#{item.detailId},#{item.appName},#{item.businessDatabase},#{item.relateId},#{item.type}," +
            "#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            "</script>")
    void saveOrUpdate(@Param("item") PressureResourceRelateDsEntityV2 item);
}
