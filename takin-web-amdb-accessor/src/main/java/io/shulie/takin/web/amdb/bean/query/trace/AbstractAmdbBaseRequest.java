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

import lombok.Data;

@Data
public abstract class AbstractAmdbBaseRequest {
    /**
     * 租户标识
     */
    String tenant = "DEFAULT";

    /**
     * 用户ID
     */
    String userId = "SYSTEM";

    /**
     * 用户名称
     */
    String userName = "SYSTEM";
    /**
     * 租户标识
     */
    private String tenantAppKey;
    /**
     * 环境标识
     */
    private String envCode;

    public static final String DEFAULT_TENANT_KEY = "default";

    public static final String DEFAULT_ENV_CODE = "test";

    public static final String DEFAULT_USER_ID = "-1";
}
