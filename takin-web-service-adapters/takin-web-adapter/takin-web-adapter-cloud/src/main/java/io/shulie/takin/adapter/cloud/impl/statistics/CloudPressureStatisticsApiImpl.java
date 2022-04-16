package io.shulie.takin.adapter.cloud.impl.statistics;

import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.adapter.cloud.convert.StatisticsConvert;
import io.shulie.takin.cloud.biz.input.statistics.PressureTotalInput;
import io.shulie.takin.cloud.biz.output.statistics.PressureListTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.ReportTotalOutput;
import io.shulie.takin.cloud.biz.service.statistics.CloudPressureStatisticsService;
import io.shulie.takin.adapter.api.entrypoint.statistics.CloudPressureStatisticsApi;
import io.shulie.takin.adapter.api.model.request.statistics.PressureTotalReq;
import io.shulie.takin.adapter.api.model.response.statistics.PressureListTotalResp;
import io.shulie.takin.adapter.api.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.adapter.api.model.response.statistics.ReportTotalResp;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2020/11/30 9:53 下午
 */
@Service
public class CloudPressureStatisticsApiImpl implements CloudPressureStatisticsApi {

    @Resource
    private CloudPressureStatisticsService cloudPressureStatisticsService;

    @Override
    public PressurePieTotalResp getPressurePieTotal(PressureTotalReq req) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(req.getStartTime());
        input.setEndTime(req.getEndTime());
        PressurePieTotalOutput output = cloudPressureStatisticsService.getPressurePieTotal(input);
        return StatisticsConvert.of(output);
    }

    @Override
    public ReportTotalResp getReportTotal(PressureTotalReq req) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(req.getStartTime());
        input.setEndTime(req.getEndTime());
        ReportTotalOutput output = cloudPressureStatisticsService.getReportTotal(input);
        return StatisticsConvert.of(output);
    }

    @Override
    public List<PressureListTotalResp> getPressureListTotal(PressureTotalReq req) {
        PressureTotalInput input = new PressureTotalInput();
        input.setStartTime(req.getStartTime());
        input.setEndTime(req.getEndTime());
        input.setScriptIds(req.getScriptIds());
        input.setType(req.getType());
        List<PressureListTotalOutput> output = cloudPressureStatisticsService.getPressureListTotal(input);
        return StatisticsConvert.of(output);
    }

}
