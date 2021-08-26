package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.MiddlewareJarEntity;
import io.shulie.takin.web.data.model.mysql.MiddlewareSummaryEntity;

public interface MiddlewareJarMapper extends BaseMapper<MiddlewareJarEntity> {
    MiddlewareSummaryEntity computeNum(MiddlewareSummaryEntity middlewareSummaryEntity);
}