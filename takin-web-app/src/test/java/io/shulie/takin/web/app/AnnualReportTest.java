package io.shulie.takin.web.app;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    @Test
    public void test2() {

        System.out.println(LocalDate.now().minusDays(80));
        System.out.println(LocalDate.now().minusDays(132));
        System.out.println(LocalDate.now().minusDays(77));
        System.out.println(LocalDate.now().minusDays(499));
        System.out.println(LocalDate.now().minusDays(541));
        System.out.println(LocalDate.now().minusDays(499));
        System.out.println(LocalDate.now().minusDays(191));

    }

    @Test
    public void test3() {

        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.now().minusDays(80);
        System.out.println(start.until(end, ChronoUnit.DAYS));
    }

}
