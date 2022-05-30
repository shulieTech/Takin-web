/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.cloud.biz.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.common.bean.collector.ResponseMetrics;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 压测数据处理工具
 *
 * @author -
 */
public class DataUtils {

    /**
     * 将时间转化成时分秒
     */
    public static String formatTime(long time) {
        return formatTime(time, "%dh%d'%d\"");
    }

    /**
     * 将时间转化成时分秒
     */
    public static String formatTime(long time, String format) {
        long h = TimeUnit.HOURS.convert(time, TimeUnit.SECONDS);
        long m = (time % 3600) / 60;
        long s = time % 60;
        return String.format(format, h, m, s);
    }

    /**
     * 拼装url
     */
    public static String mergeUrl(String domain, String path) {
        return mergePath(domain, path, "/");
    }

    /**
     * 拼装文件目录路径
     */
    public static String mergeDirPath(String dir, String path) {
        return mergePath(dir, path, File.separator);
    }

    public static String mergePath(String path1, String path2, String split) {
        if (path1.endsWith(split)) {
            path1 = path1.substring(0, path1.length() - 1);
        }
        if (!path2.startsWith(split)) {
            path2 = split + path2;
        }
        return path1 + path2;
    }

    /**
     * 最大rt和百分比中最大rt比较，取最大的
     */
    public static Double getMaxRt(ResponseMetrics metric) {
        if (null == metric) {
            return null;
        }
        Double maxRt = metric.getMaxRt();
        if (null == maxRt) {
            maxRt = 0d;
        }
        Map<Integer, RtDataOutput> percentMap = parseToPercentMap(metric.getPercentData());
        int percentMaxRt = 0;
        if (null != percentMap) {
            percentMaxRt = percentMap.values().stream().filter(Objects::nonNull)
                .mapToInt(RtDataOutput::getTime)
                .filter(Objects::nonNull)
                .max()
                .orElse(0);

        }
        return Math.max(percentMaxRt, maxRt);
    }

    /**
     * 1-100%的sa数据 各个百分点位的hits数据去掉前面百分比的部分
     */
    public static void percentMapRemoveDuplicateHits(Map<Integer, RtDataOutput> map) {
        for (int i = 100; i > 1; i--) {
            RtDataOutput d = map.get(i);
            RtDataOutput next = null;
            int j = 1;
            while (null == next && j < i) {
                next = map.get(i - j++);
            }
            if (null != next) {
                d.setHits(d.getHits() - next.getHits());
            }
        }
    }

    /**
     * 解析1-100%的sa数据
     */
    public static Map<Integer, RtDataOutput> parseToPercentMap(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        Map<Integer, RtDataOutput> percentMap = new HashMap<>(0);
        String[] percentDatas = text.split("\\|");
        for (String s : percentDatas) {
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
            percentMap.put(percent, d);
        }
        return percentMap;
    }

    /**
     * 1-100%的sa数据序列化
     */
    public static String percentMapToString(Map<Integer, RtDataOutput> map) {
        if (null == map || map.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, RtDataOutput> e : map.entrySet()) {
            RtDataOutput d = e.getValue();
            Integer hits = d.getHits();
            Integer rt = d.getTime();
            sb.append(e.getKey()).append(",").append(hits).append(",").append(rt).append("|");
        }
        return sb.toString();
    }
}
