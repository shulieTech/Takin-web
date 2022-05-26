package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.TraceManageDeployEntity;
import org.apache.ibatis.annotations.Select;

public interface TraceManageDeployMapper extends BaseMapper<TraceManageDeployEntity> {

    @InterceptorIgnore(tenantLine = "true")
    @Select("select * from t_trace_manage_deploy where `status` = 1")
    List<TraceManageDeployEntity> queryRunningTraceManageDeploy();
}