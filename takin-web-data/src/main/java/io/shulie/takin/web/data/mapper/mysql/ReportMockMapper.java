package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ReportMockEntity;

public interface ReportMockMapper extends BaseMapper<ReportMockEntity> {
    @InterceptorIgnore(tenantLine = "true")
    int updateReportMockEntity(ReportMockEntity mockEntity);
}
