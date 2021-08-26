package io.shulie.takin.web.data.convert.performance;

import io.shulie.takin.web.data.model.mysql.TraceManageDeployEntity;
import io.shulie.takin.web.data.model.mysql.TraceManageEntity;
import io.shulie.takin.web.data.result.tracemanage.TraceManageDeployResult;
import io.shulie.takin.web.data.result.tracemanage.TraceManageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface TraceManageResultConvertor {

    TraceManageResultConvertor INSTANCE = Mappers.getMapper(TraceManageResultConvertor.class);


    TraceManageResult ofTraceManageResult(TraceManageEntity traceManageEntity);

    TraceManageDeployResult ofTraceManageDeployResult(TraceManageDeployEntity traceManageDeployEntity);
}
