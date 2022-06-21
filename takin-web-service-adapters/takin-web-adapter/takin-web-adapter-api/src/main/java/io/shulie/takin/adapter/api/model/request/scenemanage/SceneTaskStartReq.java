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
     * 资源Id
     */
    private String pressureResourceId;

}