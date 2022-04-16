package io.shulie.takin.web.diff.cloud.impl.statistics;

import java.util.List;

import io.shulie.takin.adapter.api.entrypoint.statistics.CloudPressureStatisticsApi;
import io.shulie.takin.adapter.api.model.request.statistics.PressureTotalReq;
import io.shulie.takin.adapter.api.model.response.statistics.PressureListTotalResp;
import io.shulie.takin.adapter.api.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.adapter.api.model.response.statistics.ReportTotalResp;
import io.shulie.takin.web.diff.api.statistics.PressureStatisticsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2020/11/30 9:50 下午
 */
@Service
public class PressureStatisticsApiImpl implements PressureStatisticsApi {

    @Autowired
    private CloudPressureStatisticsApi cloudPressureStatisticsApi;

    @Override
    public PressurePieTotalResp getPressurePieTotal(PressureTotalReq req) {
        return cloudPressureStatisticsApi.getPressurePieTotal(req);
    }

    @Override
    public ReportTotalResp getReportTotal(PressureTotalReq req) {
        return cloudPressureStatisticsApi.getReportTotal(req);
    }

    @Override
    public List<PressureListTotalResp> getPressureListTotal(PressureTotalReq req) {
        return cloudPressureStatisticsApi.getPressureListTotal(req);
    }
}
