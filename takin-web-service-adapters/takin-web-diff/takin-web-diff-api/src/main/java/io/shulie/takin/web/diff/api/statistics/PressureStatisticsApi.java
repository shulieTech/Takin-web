package io.shulie.takin.web.diff.api.statistics;

import java.util.List;

import io.shulie.takin.adapter.api.model.request.statistics.PressureTotalReq;
import io.shulie.takin.adapter.api.model.response.statistics.PressureListTotalResp;
import io.shulie.takin.adapter.api.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.adapter.api.model.response.statistics.ReportTotalResp;

/**
 * @author 无涯
 * @date 2020/11/30 9:53 下午
 */
public interface PressureStatisticsApi {
    /**
     * 统计场景分类，返回饼状图数据
     *
     * @return
     */
    PressurePieTotalResp getPressurePieTotal(PressureTotalReq req);

    /**
     * 统计报告通过/未通过
     *
     * @return
     */
    ReportTotalResp getReportTotal(PressureTotalReq req);

    /**
     * 压测场景次数统计 && 压测脚本次数统计
     *
     * @return
     */
    List<PressureListTotalResp> getPressureListTotal(PressureTotalReq req);

}
