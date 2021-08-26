package io.shulie.takin.web.data.convert.tracenode;

import java.util.List;

import io.shulie.takin.web.data.model.mysql.TraceNodeInfoEntity;
import io.shulie.takin.web.data.param.tracenode.TraceNodeInfoParam;
import io.shulie.takin.web.data.result.tracenode.TraceNodeInfoResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author 无涯
 * @date 2020/12/29 1:39 下午
 */
@Mapper
public interface TraceNodeInfoConvert {
    TraceNodeInfoConvert INSTANCE = Mappers.getMapper(TraceNodeInfoConvert.class);

    /**
     * 转换
     * @param params
     * @return
     */
    List<TraceNodeInfoEntity> ofList(List<TraceNodeInfoParam> params);

    /**
     * 转换
     * @param entity
     * @return
     */
    TraceNodeInfoResult of (TraceNodeInfoEntity entity);

    /**
     * 转换
     * @param entity
     * @return
     */
    List<TraceNodeInfoResult> ofListResult (List<TraceNodeInfoEntity> entity);
}
