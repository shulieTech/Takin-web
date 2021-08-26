package io.shulie.takin.web.biz.service.statistics;

import java.util.List;

import io.shulie.takin.web.biz.pojo.input.statistics.PressureTotalInput;
import io.shulie.takin.web.biz.pojo.output.statistics.PressureListTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.ReportTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.ScriptLabelListTotalOutput;

/**
 * @author 无涯
 * @date 2020/11/30 9:35 下午
 */
public interface PressureStatisticsService {
    /**
     * 统计场景分类，脚本类型，返回饼状图数据
     *
     * @return
     */
    PressurePieTotalOutput getPressurePieTotal(PressureTotalInput input);

    /**
     * 统计报告通过/未通过
     *
     * @return
     */
    ReportTotalOutput getReportTotal(PressureTotalInput input);

    /**
     * 压测场景次数统计 && 压测脚本次数统计
     *
     * @return
     */
    List<PressureListTotalOutput> getPressureListTotal(PressureTotalInput input);

    /**
     * 统计脚本标签
     *
     * @return
     */
    List<ScriptLabelListTotalOutput> getScriptLabelListTotal(PressureTotalInput input);
}
