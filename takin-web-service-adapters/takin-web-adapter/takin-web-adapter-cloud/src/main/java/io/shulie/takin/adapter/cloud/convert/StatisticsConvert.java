package io.shulie.takin.adapter.cloud.convert;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.cloud.biz.output.statistics.PressureListTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.cloud.biz.output.statistics.ReportTotalOutput;
import io.shulie.takin.adapter.api.model.response.statistics.PressureListTotalResp;
import io.shulie.takin.adapter.api.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.adapter.api.model.response.statistics.ReportTotalResp;

/**
 * @author 无涯
 * @date 2020/12/1 7:14 下午
 */
public class StatisticsConvert {
    public static PressurePieTotalResp of(PressurePieTotalOutput output) {
        return BeanUtil.copyProperties(output, PressurePieTotalResp.class);
    }

    public static ReportTotalResp of(ReportTotalOutput output) {
        return BeanUtil.copyProperties(output, ReportTotalResp.class);
    }

    public static List<PressureListTotalResp> of(List<PressureListTotalOutput> output) {
        return output.stream()
            .map(out -> BeanUtil.copyProperties(out, PressureListTotalResp.class))
            .collect(Collectors.toList());
    }
}
