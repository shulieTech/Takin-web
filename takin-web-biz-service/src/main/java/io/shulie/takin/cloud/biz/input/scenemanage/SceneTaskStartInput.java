package io.shulie.takin.cloud.biz.input.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.ext.content.enums.AssetTypeEnum;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

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
     * 流量类型
     *
     * @see AssetTypeEnum
     */
    private Integer assetType;

    private Long resourceId;

    private String resourceName;

    private Long operateId;

    private String operateName;

    /**
     * 是否定时
     */
    private Boolean isTiming = false;
}
