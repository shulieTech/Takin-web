package io.shulie.takin.adapter.api.model.request.engine;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取引擎插件列表入参
 *
 * @author lipeng
 * @date 2021-01-20 3:45 下午
 */
@Data
@ApiModel("获取引擎插件列表入参")
@EqualsAndHashCode(callSuper = true)
public class EnginePluginFetchWrapperReq extends ContextExt {

    private List<String> pluginTypes;

}