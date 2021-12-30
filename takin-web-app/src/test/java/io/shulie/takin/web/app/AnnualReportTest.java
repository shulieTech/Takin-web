package io.shulie.takin.web.app;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import io.shulie.takin.web.biz.pojo.vo.AnnualReportContentVO;
import org.junit.Test;

/**
 * @author liuchuan
 * @date 2021/11/23 2:09 下午
 */
public class AnnualReportTest {

    @Test
    public void test() {

        AnnualReportContentVO annualReportContentVO = new AnnualReportContentVO();
        annualReportContentVO.setTotalPressureTime(5625);
        annualReportContentVO.setMaxPressureTime(324);
        // 压测比例
        Integer maxPressureTime = annualReportContentVO.getMaxPressureTime();
        Integer totalPressureTime = annualReportContentVO.getTotalPressureTime();
        annualReportContentVO.setPressureProportion(BigDecimal.valueOf(maxPressureTime).
                divide(BigDecimal.valueOf(totalPressureTime), MathContext.DECIMAL32)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP));

        System.out.println(annualReportContentVO.getPressureProportion());

    }

}
