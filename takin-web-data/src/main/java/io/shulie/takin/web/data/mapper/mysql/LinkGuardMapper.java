package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.LinkGuardEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LinkGuardMapper extends BaseMapper<LinkGuardEntity> {
    @InterceptorIgnore(tenantLine = "true")
    List<LinkGuardEntity> queryListByApplicationId(@Param("applicationId") Long applicationId);
}
