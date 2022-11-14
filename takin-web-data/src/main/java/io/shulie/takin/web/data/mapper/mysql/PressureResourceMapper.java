package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceAppDataSourceEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PressureResourceMapper
        extends BaseMapper<PressureResourceEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Select("select * from t_pressure_resource where is_delete=0")
    List<PressureResourceEntity> getAll();

    @InterceptorIgnore(tenantLine = "true")
    @Select("select * from t_pressure_resource where id = #{id} and is_delete=0")
    PressureResourceEntity queryByIdNoTenant(@Param("id") Long id);
}
