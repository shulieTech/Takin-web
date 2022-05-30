package io.shulie.takin.cloud.biz.service.scene.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.biz.service.scene.ReportEventService;
import io.shulie.takin.cloud.common.bean.collector.Metrics;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
        List<Metrics> metricsList = influxWriter.query(sql.toString(), Metrics.class);
        if (null == metricsList) {
            return null;
        }
        List<String> percentDataList = metricsList.stream().map(Metrics::getPercentData).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(percentDataList)){
            return null;
        }
        Map<Integer, RtDataOutput> percentMap = calcRtDistribution(resolvingPercentData(percentDataList));
        if(Objects.isNull(percentMap)){
            return null;
        }

        Map<String, String> resultMap = Maps.newLinkedHashMap();
        INDEXS.forEach(percent -> {
            resultMap.put(percent + PERCENTAGE, percentMap.get(percent).getTime() + MS);
        });
        return resultMap;
    }

    /**
     * 解析percentData字符串
     * @param percentDataList
     * @return
     */
    public static List<RtDataOutput> resolvingPercentData(List<String> percentDataList) {
        List<RtDataOutput> rtDataOutputs = new ArrayList<>();
        for (String percentRecord : percentDataList) {
            String[] percents = percentRecord.split("\\|");
            for (String s : percents) {
                if (StringUtils.isBlank(s) || !s.contains(",")) {
                    continue;
                }
                String[] ss = s.split(",");
                if (ss.length < 3) {
                    continue;
                }
                Integer percent = NumberUtils.toInt(ss[0]);
                Integer hits = NumberUtils.toInt(ss[1]);
                Integer time = 0;
                if (StringUtils.isNotBlank(ss[2])) {
                    if (ss[2].contains(".")) {
                        ss[2] = ss[2].substring(0, ss[2].lastIndexOf("."));
                    }
                    time = NumberUtils.toInt(ss[2]);
                }
                RtDataOutput d = new RtDataOutput(hits, time);
                rtDataOutputs.add(d);
            }
        }
        return rtDataOutputs;
    }

    /**
     * 重新计算Rt分布
     * @param dataOutputs
     * @return
     */
    public static Map<Integer, RtDataOutput> calcRtDistribution(List<RtDataOutput> dataOutputs) {
        dataOutputs = dataOutputs.stream().sorted(Comparator.comparingInt(RtDataOutput::getTime)).collect(Collectors.toList());
        List<Integer> values = dataOutputs.stream().map(RtDataOutput::getTime).collect(Collectors.toList());
        List<Integer> hits = dataOutputs.stream().map(RtDataOutput::getHits).collect(Collectors.toList());
        if (null == values || values.size() <= 0) {
            return null;
        }

        Map<Integer, RtDataOutput> distributes = new HashMap<>();
        int lastIdx = 0;
        int count = 0;
        for (int i = 1; i <= 100; i++) {
            int idx = (int) Math.ceil(values.size() * i / 100d);
            idx = Math.max(idx, 1);
            idx = Math.min(idx, values.size());
            double v = values.get(idx - 1);
            for (int j = lastIdx; j < values.size(); j++) {
                lastIdx = j;
                if (values.get(j) > v) {
                    break;
                }
                count += hits.get(j);
            }

            distributes.put(i, new RtDataOutput(count, (int) v));

        }
        return distributes;
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
