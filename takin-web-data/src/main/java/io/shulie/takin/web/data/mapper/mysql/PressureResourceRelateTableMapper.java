package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntityV2;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface PressureResourceRelateTableMapper
        extends BaseMapper<PressureResourceRelateTableEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_relate_table(" +
            "resource_id,ds_key,status,business_table,shadow_table,join_flag,ext_info," +
            "type,remark,tenant_id,env_code,gmt_create)" +
            "values " +
            "(#{item.resourceId},#{item.dsKey},#{item.status},#{item.businessTable},#{item.shadowTable},#{item.joinFlag}," +
            "#{item.extInfo},#{item.type},#{item.remark},#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            " ON DUPLICATE KEY UPDATE gmt_modified=now()" +
            "<if test=\"item.shadowTable!=null and item.shadowTable !=''\"> " +
            "   ,shadow_table =values(shadow_table)" +
            "</if>" +
            "</script>")
    void saveOrUpdate(@Param("item") PressureResourceRelateTableEntity item);
}
