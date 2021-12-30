package io.shulie.takin.web.app;

import java.time.LocalDateTime;

import cn.hutool.core.date.LocalDateTimeUtil;
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
        annualReportContentVO.setDay(356);
        annualReportContentVO.setCountActivity(1000);
        annualReportContentVO.setCountOver(124);
        annualReportContentVO.setLongestActivity("链路最长的业务活动");
        annualReportContentVO.setCountScene(500);
        annualReportContentVO.setTotalPressureTime(2000);
        annualReportContentVO.setMaxPressureTime(300);
        annualReportContentVO.setMaxTimePressure("压测最长的场景");
        annualReportContentVO.setLastDateTime(LocalDateTime.now());
        annualReportContentVO.setOptimizedActivity("优化最厉害的业务活动");
        annualReportContentVO.setBeforeAvgRt(500);
        annualReportContentVO.setAfterAvgRt(10);
        annualReportContentVO.setMinAvgRtActivity("最小平均rt业务活动");
        annualReportContentVO.setMinAvgRt(1);

        LocalDateTime now = LocalDateTime.now();
        System.out.println(LocalDateTimeUtil.format(now, "MM月dd日"));
        System.out.println(LocalDateTimeUtil.format(now, "HH:mm"));

    }

}
