package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntityV2;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface PressureResourceRelateTableMapperV2
        extends BaseMapper<PressureResourceRelateTableEntityV2> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert ignore into t_pressure_resource_relate_table_v2(" +
            "resource_id,type,ds_key,relate_id,tenant_id,env_code,gmt_create)" +
            "values " +
            "(#{item.resourceId},#{item.type},#{item.dsKey},#{item.relateId}," +
            "#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            "</script>")
    void saveOrUpdate(@Param("item") PressureResourceRelateTableEntityV2 item);
}
