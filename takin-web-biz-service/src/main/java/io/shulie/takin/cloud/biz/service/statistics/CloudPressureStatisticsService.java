package io.shulie.takin.cloud.biz.service.statistics;

import java.util.List;

import io.shulie.takin.cloud.biz.input.statistics.PressureTotalInput;
import io.shulie.takin.cloud.biz.output.statistics.PressureListTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.ReportTotalOutput;

/**
 * @author 无涯
 * @date 2020/11/30 9:35 下午
 */
public interface CloudPressureStatisticsService {
    /**
     * 统计场景分类，脚本类型，返回饼状图数据
     *
     * @param input 入参
     * @return -
     */
    PressurePieTotalOutput getPressurePieTotal(PressureTotalInput input);

    /**
     * 统计报告通过/未通过
     *
     * @param input 入参
     * @return -
     */
    ReportTotalOutput getReportTotal(PressureTotalInput input);

    /**
     * 压测场景次数统计 && 压测脚本次数统计
     *
     * @param input 入参
     * @return -
     */
    List<PressureListTotalOutput> getPressureListTotal(PressureTotalInput input);

}


