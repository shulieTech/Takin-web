package io.shulie.takin.adapter.api.model.request.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskStartRequest extends ContextExt {

    private Long sceneId;

    private List<Long> enginePluginIds;

    private List<EnginePluginInput> enginePlugins;

    private Boolean continueRead;

    @Data
    public static class EnginePluginInput {
        /**
         * 插件标识
         */
        private Long pluginId;
        /**
         * 插件版本
         */
        private String pluginVersion;
    }
}
