package io.shulie.takin.adapter.api.model.request.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/10/22 8:16 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptCheckAndUpdateReq extends ContextExt {

    /**
     * 业务请求
     */
    private List<String> request;

    /**
     * 脚本路径
     */
    private String uploadPath;

    private boolean absolutePath;

    /**
     * 是否更新脚本
     */
    private boolean update;
    /**
     * 脚本版本(管理版本)
     * <p>混合压测前是0</p>
     * <p>混合压测后是1</p>
     * <p><strong>默认是混合压测后的版本</strong></p>
     */
    private Integer version = 1;

    private boolean isPressure;
    private Long sceneId;
    // 主要是巡检使用，巡检关联的版本的业务活动
    private String sceneName;
    // 脚本文件路径
    private String scriptPath;
    // 数据文件路径
    private List<String> csvPaths;
    // 附件文件路径
    private List<String> attachments;
    // 插件
    private List<EnginePlugin> plugins;

    @Data
    public static class EnginePlugin {
        private Long pluginId;
        private String pluginVersion;

        EnginePlugin(Long pluginId, String pluginVersion) {
            this.pluginId = pluginId;
            this.pluginVersion = pluginVersion;
        }

        public static EnginePlugin of(Long pluginId, String pluginVersion) {
            return new EnginePlugin(pluginId, pluginVersion);
        }
    }
}
