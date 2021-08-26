//package io.shulie.takin.web.app.trace;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import com.google.common.collect.Lists;
//import io.shulie.takin.web.biz.service.perfomanceanaly.impl.TraceManageServiceImpl;
//import org.junit.Assert;
//import org.junit.Test;
//
///**
// * @author 无涯
//// * @date 2021/1/28 9:00 下午
// */
//
//public class TraceManageServiceImplTest {
//    /**
//     * 分位数
//     */
//    @Test
//    public void getPercentileTest() {
//        TraceManageServiceImpl impl = new TraceManageServiceImpl();
//        List<Long> costs = Lists.newArrayList();
//        costs.add(3009593880L);
//        costs.add(2009593880L);
//        costs.add(4009593880L);
//        costs.add(1009593880L);
//        Assert.assertEquals(BigDecimal.valueOf(2509.593880),impl.getPercentile(costs, 0.50));
//        Assert.assertEquals(BigDecimal.valueOf(3709.593880),impl.getPercentile(costs, 0.90));
//        Assert.assertEquals(BigDecimal.valueOf(3859.593880),impl.getPercentile(costs, 0.95));
//        Assert.assertEquals(BigDecimal.valueOf(3979.593880),impl.getPercentile(costs, 0.99));
//        Assert.assertEquals(BigDecimal.valueOf(2509.593880),impl.getAvgCost(costs));
//        Assert.assertEquals(BigDecimal.valueOf(1009.593880),impl.getMaxMinCost(costs,"min"));
//        Assert.assertEquals(BigDecimal.valueOf(4009.593880),impl.getMaxMinCost(costs,"max"));
//
//    }
//}
