package io.shulie.takin.web.biz.convert.performace;

import java.math.BigDecimal;
import java.util.List;

import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineStatisticsResponse;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsResult;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * @author mubai
 * @date 2020-11-13 18:07
 */

@Mapper
public interface PressureMachineStatisticsRespConvert {

    PressureMachineStatisticsRespConvert INSTANCE = Mappers.getMapper(PressureMachineStatisticsRespConvert.class);

    PressureMachineStatisticsResponse of(PressureMachineStatisticsResult result);

    List<PressureMachineStatisticsResponse> ofs(List<PressureMachineStatisticsResult> results);

    @AfterMapping
    default void fillRespData(PressureMachineStatisticsResult source, @MappingTarget PressureMachineStatisticsResponse response) {
        Integer machineTotal = source.getMachineTotal();
        if (machineTotal == 0) {
            BigDecimal decimal = new BigDecimal(0);
            response.setOfflinePercent(decimal);
            response.setPressuredPercent(decimal);
            response.setFreePercent(decimal);
        } else {

            BigDecimal totalNum = new BigDecimal(source.getMachineTotal());
            BigDecimal freePercent = new BigDecimal(source.getMachineFree()).divide(totalNum, 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal presssuredPercent = new BigDecimal(source.getMachinePressured()).divide(totalNum, 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal offlinePercent = new BigDecimal(source.getMachineOffline()).divide(totalNum, 4, BigDecimal.ROUND_HALF_UP);
            response.setFreePercent(freePercent);
            response.setPressuredPercent(presssuredPercent);
            response.setOfflinePercent(offlinePercent);
        }
    }
}
