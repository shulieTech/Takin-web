package io.shulie.takin.web.data.convert.performance;

import java.util.List;

import io.shulie.takin.web.data.model.mysql.PerformanceBaseDataEntity;
import io.shulie.takin.web.data.model.mysql.PerformanceThreadDataEntity;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceThreadDataParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
* @author qianshui
 * @date 2020/11/10 上午10:57
 */
@Mapper
public interface PerformanceBaseParamConvert {

    PerformanceBaseParamConvert INSTANCE = Mappers.getMapper(PerformanceBaseParamConvert.class);

    PerformanceThreadDataEntity paramToEntity(PerformanceThreadDataParam source);

    List<PerformanceThreadDataEntity> paramToEntityList(List<PerformanceThreadDataParam> sources);

    PerformanceBaseDataEntity paramToEntity(PerformanceBaseDataParam source);
}
