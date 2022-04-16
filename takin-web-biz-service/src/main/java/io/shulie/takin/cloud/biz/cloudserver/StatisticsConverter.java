package io.shulie.takin.cloud.biz.cloudserver;

import java.util.List;

import io.shulie.takin.cloud.biz.output.statistics.PressureListTotalOutput;
import io.shulie.takin.cloud.data.result.statistics.PressureListTotalResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author 无涯
 * @date 2020/12/1 6:57 下午
 */
@Mapper
public interface StatisticsConverter {
    StatisticsConverter INSTANCE = Mappers.getMapper(StatisticsConverter.class);

    /**
     * 转换
     *
     * @param results -
     * @return -
     */
    List<PressureListTotalOutput> ofResult(List<PressureListTotalResult> results);
}
