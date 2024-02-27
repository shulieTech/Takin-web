package io.shulie.takin.cloud.biz.input.scenemanage;

import io.shulie.takin.cloud.ext.content.enums.AssetTypeEnum;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

import java.util.List;

/**
 * @author qianshui
 * @date 2020/11/4 下午4:49
 */
@Data
public class SceneTaskStartInput extends ContextExt {

    private Long sceneId;

    private List<EnginePluginInput> enginePlugins;

    private SceneInspectInput sceneInspectInput;

    private SceneTryRunInput sceneTryRunInput;

    private Boolean continueRead;

    /**
     * 文件是否继续压测
     */
    private List<PressureFileInfo> pressureFileInfos;
    /**
     * 流量类型
     *
     * @see AssetTypeEnum
     */
    private Integer assetType;

    private Long resourceId;

    private String resourceName;

    private Long operateId;

    private String operateName;

    @Data
    public static class PressureFileInfo{
        private String fileName;
        private Boolean continueRead;
    }
}
