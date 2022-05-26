package io.shulie.takin.cloud.biz.service.scene.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.biz.service.scene.ReportEventService;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.common.bean.collector.Metrics;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/7/20 下午4:23
 */
@Service
@Slf4j
public class ReportEventServiceImpl implements ReportEventService {

    private static final List<Integer> INDEXS = Arrays.asList(99, 95, 90, 75, 50);
    private static final String PERCENTAGE = "%";
    private static final String MS = "ms";
    @Autowired
    private InfluxWriter influxWriter;

    @Override
    public Map<String, String> queryAndCalcRtDistribute(String tableName, String bindRef) {
        StringBuffer sql = new StringBuffer();
        sql.append("select sa_percent as percentData from ")
            .append(tableName)
            .append(" where transaction=")
            .append("'")
            .append(bindRef)
            .append("'");
        Metrics metrics = influxWriter.querySingle(sql.toString(), Metrics.class);
        if (null == metrics) {
            return null;
        }
        log.debug("报告生成，saPercent：{}", metrics.getPercentData());
        Map<Integer, RtDataOutput> percentMap = DataUtils.parseToPercentMap(metrics.getPercentData());

        Map<String, String> resultMap = Maps.newLinkedHashMap();
        INDEXS.forEach(percent -> {
            resultMap.put(percent + PERCENTAGE, percentMap.get(percent).getTime() + MS);
        });
        return resultMap;
    }

    private int calcIndex(int size, int percentage) {
        if (percentage <= 0) {
            return 0;
        }
        if (percentage >= 100) {
            return size - 1;
        }
        BigDecimal b1 = new BigDecimal(size);
        BigDecimal b2 = new BigDecimal(percentage);
        int index = b1.multiply(b2.divide(new BigDecimal(100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        if (index >= size) {
            index = size - 1;
        }
        return index;
    }
}
