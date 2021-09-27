/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: opensource@shulie.io
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

package io.shulie.takin.web.amdb.bean.query.application;

import java.util.List;

import io.shulie.takin.web.amdb.bean.common.PagingDevice;
import lombok.Data;

@Data
public class ApplicationQueryDTO extends PagingDevice {

    /**
     * 应用名称
     */
    private List<String> appNames;

    /**
     * 租户标识
     */
    private String userAppKey;
    /**
     * 环境编码
     */
    private String envCode;

    /**
     * 拓展信息
     */
    private List<String> fields;
}
