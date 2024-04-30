package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.AgentMockDataEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AgentMockDataMapper extends BaseMapper<AgentMockDataEntity> {
    @InterceptorIgnore(tenantLine = "true")
    int insertData(AgentMockDataEntity mockEntity);

    @InterceptorIgnore(tenantLine = "true")
    List<AgentMockDataEntity> selectListByReportId(@Param("reportId") Long reportId);
}
