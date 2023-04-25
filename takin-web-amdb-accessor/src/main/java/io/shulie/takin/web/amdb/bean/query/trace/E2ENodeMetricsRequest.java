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

package io.shulie.takin.web.amdb.bean.query.trace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("节点指标数据查询")
public class E2ENodeMetricsRequest extends E2EBaseRequest {

    /**
     * 起始时间
     */
    @ApiModelProperty("起始时间")
    long startTime;
    /**
     * 截止时间
     */
    @ApiModelProperty("截止时间")
    long endTime;

    @Override
    public String toString() {
        return "E2ENodeMetricsRequest{" +
                "tenant='" + tenant + '\'' +
                ", appName='" + appName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", rpcType='" + rpcType + '\'' +
                ", edgeId='" + edgeId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
