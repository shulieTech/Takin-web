package io.shulie.takin.web.biz.convert.performace;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TraceManageDeployResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TraceManageResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployResponse;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageDeployResult;
import io.shulie.takin.web.data.result.tracemanage.TraceManageDeployResult;
import io.shulie.takin.web.data.result.tracemanage.TraceManageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface TraceManageResponseConvertor {
    TraceManageResponseConvertor INSTANCE = Mappers.getMapper(TraceManageResponseConvertor.class);

    TraceManageResponse ofTraceManageResponse(TraceManageResult traceManageResult);

    TraceManageDeployResponse ofTraceManageDeployResponse(TraceManageDeployResult traceManageDeployResult);

    List<ScriptManageDeployResponse> ofListScriptManageDeployResponse(List<ScriptManageDeployResult> scriptManageDeployResults);
}
