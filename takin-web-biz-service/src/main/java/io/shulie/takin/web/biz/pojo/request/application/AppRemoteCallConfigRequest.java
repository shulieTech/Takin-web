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

package io.shulie.takin.web.biz.pojo.request.application;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 远程调用配置入参
 *
 * @author liuxuewu
 * @2021/9/15
 */
@ApiModel("入参类-远程调用配置入参")
@Data
public class AppRemoteCallConfigRequest {
    /** 应用ID列表 */
    @ApiModelProperty(name = "appIds", value = "应用ID列表")
    @NotEmpty(message = "应用ID列表" + AppConstants.MUST_BE_NOT_NULL)
    private List<Long> appIds;

    @ApiModelProperty(name = "type", value = "0/未配置, 1/配置成白名单")
    private Short type = AppRemoteCallConfigEnum.OPEN_WHITELIST.getType().shortValue();
}
