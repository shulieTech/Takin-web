package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PressureResourceRelateTableMapper
        extends BaseMapper<PressureResourceRelateTableEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_relate_table(" +
            "resource_id,ds_key,status,business_table,shadow_table,join_flag,ext_info," +
            "type,remark,tenant_id,env_code,gmt_create)" +
            "values " +
            "<foreach collection='list' item='item' index='index' separator=','>" +
            "(#{item.resourceId},#{item.dsKey},#{item.status},#{item.businessTable},#{item.shadowTable},#{item.joinFlag}," +
            "#{item.extInfo},#{item.type},#{item.remark},#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            "</foreach>" +
            " ON DUPLICATE KEY UPDATE " +
            " shadow_table =values(shadow_table),gmt_modified=now()" +
            "</script>")
    void saveOrUpdate(@Param("list") List<PressureResourceRelateTableEntity> list);
}
