/*
 * Copyright (c) 2021. Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.shulie.takin.web.biz.pojo.response.application;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 远程调用配置出参
 * @author liuxuewu
 * @2021/9/15
 */
@ApiModel("出参类-远程调用配置出参")
@Data
public class AppRemoteCallConfigResponse {
    private String content;

    public AppRemoteCallConfigResponse(String content) {
        this.content = content;
    }
}
