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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.influxdb.annotation.Column;

/**
 * @author shiyajian
 * create: 2020-10-10
 */
@Data
@NoArgsConstructor
public class AbstractMetrics {
    /**
     * 类型：response上报的统计数据，events事件数据
     */
    private String type;
    /**
     * pod的编号
     */
    @Column(tag = true, name = "pod_no")
    private String podNo;

    public AbstractMetrics(String type) {
        this.type = type;
    }
}
