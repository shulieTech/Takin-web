package io.shulie.takin.cloud.data.mapper.mysql;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author -
 */
public interface ReportMapper extends BaseMapper<ReportEntity> {

    @InterceptorIgnore(tenantLine = "true")
    List<ReportEntity> selectList(@Param(Constants.WRAPPER) Wrapper<ReportEntity> queryWrapper);

    @InterceptorIgnore(tenantLine = "true")
    ReportEntity selectById(Serializable id);

    @InterceptorIgnore(tenantLine = "true")
    int updateById(@Param(Constants.ENTITY) ReportEntity entity);

    List<ReportEntity> queryBySceneIds(@Param("ids") List<Long> ids);
}