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
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class E2EBaseRequest extends AbstractAmdbBaseRequest {
    /**
     * 租户标识
     */
    @ApiModelProperty("租户标识")
    String tenant;
    /**
     * 应用名称
     */
    @ApiModelProperty("应用名称")
    String appName;
    /**
     * 服务名称
     */
    @ApiModelProperty("服务名称")
    String serviceName;
    /**
     * 方法名
     */
    @ApiModelProperty("方法名")
    String methodName;
    /**
     * RPC类型
     */
    @ApiModelProperty("RPC类型")
    String rpcType;
    /**
     * 边ID
     */
    @ApiModelProperty("链路图唯一边ID,支持多个边ID,逗号分割")
    String edgeId;
}
