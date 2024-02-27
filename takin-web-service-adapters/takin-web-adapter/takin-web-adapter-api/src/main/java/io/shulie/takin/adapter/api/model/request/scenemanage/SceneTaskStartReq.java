package io.shulie.takin.adapter.api.model.request.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskStartReq extends ContextExt {

    private Long sceneId;

    /**
     * cloud的统一接收参数
     */
    private String resourceName;

    /**
     * 使用下面enginePlugins，包含id和版本号
     */
    @Deprecated
    private List<Long> enginePluginIds;

    private List<SceneTaskStartRequest.EnginePluginInput> enginePlugins;

    private Boolean leakSqlEnable;

    private Boolean continueRead = false;

    /**
     * 文件是否继续压测
     */
    private List<PressureFileInfo> pressureFileInfos;

    /**
     * 资源Id
     */
    private String pressureResourceId;


    @Data
    public static class PressureFileInfo{
        private String fileName;
        private Boolean continueRead;
    }
}