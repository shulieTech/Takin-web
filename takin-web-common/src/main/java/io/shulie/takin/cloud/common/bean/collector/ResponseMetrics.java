/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.cloud.common.bean.collector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import org.influxdb.annotation.Column;

/**
 * 成功的响应
 *
 * @author shiyajian
 * create: 2020-10-10
 */
@Data
public class ResponseMetrics extends AbstractMetrics {
    @Column(name = "time")
    private Long time;
    @Column(tag = true, name = "transaction")
    private String transaction;
    @Column(name = "test_name")
    private String testName;
    @Column(name = "count")
    private Integer count;
    @Column(name = "fail_count")
    private Integer failCount;
    @Column(name = "sent_bytes")
    private Long sentBytes;
    @Column(name = "received_bytes")
    private Long receivedBytes;
    @Column(name = "rt")
    private Double rt;
    /**
     * 原始rt值
     */
    @Column(name = "sum_rt")
    private Long sumRt;
    @Column(name = "sa_count")
    private Integer saCount;
    @Column(name = "max_rt")
    private Double maxRt;
    @Column(name = "min_rt")
    private Double minRt;
    @Column(name = "real_time")
    @JSONField(name = "real_time")
    private long timestamp;
    @Column(name = "active_threads")
    private Integer activeThreads;
    private Map<String, String> tags = new HashMap<>();
    private Set<ErrorInfo> errorInfos;
    @Column(name = "percent_data")
    private String percentData;

    public ResponseMetrics() {
        super(Constants.METRICS_TYPE_RESPONSE);
    }

    public String getPodNum() {
        if (null != getPodNo()) {
            return String.valueOf(getPodNo());
        }
        // 与压测引擎统一
        return tags.get(CollectorConstant.POD_NUM);
    }

    @Data
    class ErrorInfo {
        private String responseMessage;
        private String responseCode;
    }
}
