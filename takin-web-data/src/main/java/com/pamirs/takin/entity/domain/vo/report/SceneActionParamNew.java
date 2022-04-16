package com.pamirs.takin.entity.domain.vo.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * @author 莫问
 * @date 2020-04-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneActionParamNew extends ContextExt {

    private Long sceneId;

    /**
     * cloud的统一接收参数
     */
    private String resourceName;

    private List<Long> enginePluginIds;

    private List<ScenePluginParam> enginePlugins;

    private Boolean leakSqlEnable;

    private Boolean continueRead = false;

    /**
     * 资源Id
     */
    private String pressureResourceId;
}
