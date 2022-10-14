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

package io.shulie.takin.cloud.biz.output.statistics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.influxdb.annotation.Column;

import io.shulie.takin.cloud.common.pojo.AbstractEntry;

/**
 * @author liyuanba
 * @date 2021/9/23 9:20 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PressureOutput extends AbstractEntry {
    @Column(tag = true, name = "transaction")
    private String transaction;
    @Column(name = "test_name")
    private String testName;
    @Column(name = "time")
    private long time;
    @Column(name = "count")
    private Integer count;
    @Column(name = "fail_count")
    private Integer failCount;
    @Column(name = "sa_count")
    private Integer saCount;
    @Column(name = "sa")
    private Double sa;
    @Column(name = "success_rate")
    private Double successRate;
    @Column(name = "sent_bytes")
    private Long sentBytes;
    @Column(name = "received_bytes")
    private Long receivedBytes;
    @Column(name = "sum_rt")
    private Long sumRt;
    @Column(name = "avg_rt")
    private Double avgRt;
    @Column(name = "max_rt")
    private Double maxRt;
    @Column(name = "min_rt")
    private Double minRt;
    /**
     * 活跃线程数（通过计算所得）
     */
    @Column(name = "active_threads")
    private Integer activeThreads;
    @Column(name = "avg_tps")
    private Double avgTps;
    @Column(name = "sa_percent")
    private String saPercent;
    /**
     * 来源数据量，正常每个时间窗口都有pod数量个数据量
     */
    @Column(name = "data_num")
    private Integer dataNum;
    /**
     * 数据收集率
     */
    @Column(name = "data_rate")
    private Double dataRate;
    /**
     * 状态：0统计不完整（数据有缺失），1完成
     */
    @Column(name = "status")
    private Integer status;
}
