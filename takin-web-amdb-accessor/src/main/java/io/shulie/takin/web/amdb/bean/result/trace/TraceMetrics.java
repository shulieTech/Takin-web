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

package io.shulie.takin.web.amdb.bean.result.trace;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TraceMetrics implements Serializable {

    private String time;
    private String appName;
    private int avgRt;
    private int avgTps;
    private Boolean clusterTest;
    private int e2eErrorCount;
    private int e2eSuccessCount;
    private String edgeId;
    //private String entranceId;
    private int errorCount;
    private int hitCount;
    //private int logType;
    private String log_time;
    private int maxRt;
    private String method;
    private String middlewareName;
    private int rpcType;
    //private String serverAppName;
    private String service;
    private String sqlStatement;
    //private String sqlStatementMd5;
    private int successCount;
    private int total;
    private int totalCount;
    //private int totalQps;
    private int totalRt;
    private int totalTps;
    private String traceId;

}