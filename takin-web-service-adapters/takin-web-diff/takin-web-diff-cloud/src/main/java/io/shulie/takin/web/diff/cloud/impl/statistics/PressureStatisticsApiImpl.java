package io.shulie.takin.web.diff.cloud.impl.statistics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.cloud.entrypoint.statistics.CloudPressureStatisticsApi;
import io.shulie.takin.cloud.sdk.model.request.statistics.FullRequest;
import io.shulie.takin.cloud.sdk.model.request.statistics.PressureTotalReq;
import io.shulie.takin.cloud.sdk.model.response.statistics.FullResponse;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.ReportTotalResp;
import io.shulie.takin.web.diff.api.statistics.PressureStatisticsApi;
import org.springframework.beans.BeanUtils;
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
        FullRequest f = new FullRequest();
        BeanUtils.copyProperties(req,f);
        f.setEnvCode(req.getEnvCode());
        f.setTenantId(req.getTenantId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            f.setEndTime(sdf.parse(req.getEndTime()).getTime());
            f.setStartTime(sdf.parse(req.getStartTime()).getTime());
        }catch (Exception e) {
            e.printStackTrace();
        }
        FullResponse response = cloudPressureStatisticsApi.full(f);
        int runnableCount = response.getScene().getRunableCount();
        int runningCount = response.getScene().getRunningCount();
        int count = response.getScene().getCount();
        PressurePieTotalResp resp = new PressurePieTotalResp();
        resp.setTotal(count);
        List<PressurePieTotalResp.PressurePieTotal> ps = new ArrayList();
        PressurePieTotalResp.PressurePieTotal t = new PressurePieTotalResp.PressurePieTotal();
        t.setType("待启动");
        t.setValue(runnableCount);
        ps.add(t);
        PressurePieTotalResp.PressurePieTotal t1 = new PressurePieTotalResp.PressurePieTotal();
        t1.setType("压测中");
        t1.setValue(runningCount);
        ps.add(t1);
        return resp;
    }

    @Override
    public ReportTotalResp getReportTotal(PressureTotalReq req) {
        FullRequest f = new FullRequest();
        BeanUtils.copyProperties(req,f);
        f.setEnvCode(req.getEnvCode());
        f.setTenantId(req.getTenantId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            f.setEndTime(sdf.parse(req.getEndTime()).getTime());
            f.setStartTime(sdf.parse(req.getStartTime()).getTime());
        }catch (Exception e) {
            e.printStackTrace();
        }
        FullResponse response = cloudPressureStatisticsApi.full(f);
        int success = response.getReport().getConclusionTrueCount();
        int fail = response.getReport().getConclusionFalseCount();
        int count = response.getReport().getCount();
        ReportTotalResp reportTotalResp = new ReportTotalResp();
        reportTotalResp.setCount(count);
        reportTotalResp.setFail(fail);
        reportTotalResp.setSuccess(success);
        return reportTotalResp;
    }

    @Override
    public List<PressureListTotalResp> getPressureListTotal(PressureTotalReq req) {
        return cloudPressureStatisticsApi.getPressureListTotal(req);
    }
}
