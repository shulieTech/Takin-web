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

import io.shulie.amdb.common.request.AbstractAmdbBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@ApiModel("节点指标数据查询")
public class TraceMetricsRequest extends AbstractAmdbBaseRequest {


    @ApiModelProperty("开始时间,时间戳格式")
    long startMilli = 0L;
    @ApiModelProperty("结束时间,时间戳格式")
    long endMilli = 0L;
    @ApiModelProperty("真实时间间隔")
    long realSeconds = 0L;      //秒数
    @ApiModelProperty("是否压测流量")
    Boolean metricsType = null; //流量类型
    @ApiModelProperty("链路图唯一边ID")
    String eagleId = "";        //边ID
    @ApiModelProperty("链路图唯一边ID集合")
    List<String> eagleIds;        //边ID集合


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

    @ApiModelProperty("压测流量标识(1为压测流量,0为业务流量,混合则为-1)")
    int clusterTest;

    @ApiModelProperty("查询来源(tro/e2e)")
    String querySource;

    @ApiModelProperty("查询参数")
    List<E2ENodeMetricsRequest> e2eNodeRequestList;

    @ApiModelProperty("边ID,逗号分隔")
    String edgeIds;

    @ApiModelProperty("应用名称列表")
    List<String> appNames;

    @ApiModelProperty("linkIds")
    private List<String> linkIds;

    public List<E2ENodeMetricsRequest> getE2eNodeRequestList() {
        if (!CollectionUtils.isEmpty(e2eNodeRequestList)) {
            String tenantAppKey = getTenantAppKey();
            String envCode = getEnvCode();
            if (StringUtils.isNotBlank(tenantAppKey) && StringUtils.isNotBlank(envCode)) {
                e2eNodeRequestList.forEach(request -> {
                    request.setTenantAppKey(tenantAppKey);
                    request.setEnvCode(envCode);
                });
            }
        }
        return e2eNodeRequestList;
    }
}
