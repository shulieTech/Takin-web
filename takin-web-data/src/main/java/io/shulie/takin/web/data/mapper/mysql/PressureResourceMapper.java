package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceAppDataSourceEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PressureResourceMapper
        extends BaseMapper<PressureResourceEntity> {
}
