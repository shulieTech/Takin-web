package io.shulie.takin.adapter.api.model.request.engine;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取引擎插件详情入参
 *
 * @author lipeng
 * @date 2021-01-20 3:49 下午
 */
@Data
@ApiModel("获取引擎插件详情入参")
@EqualsAndHashCode(callSuper = true)
public class EnginePluginDetailsWrapperReq extends ContextExt {

    private Long pluginId;
}
