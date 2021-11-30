package com.pamirs.takin.entity.domain.vo.report;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;

/**
 * @author 莫问
 * @date 2020-04-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneActionParamNew extends WebRequest implements Serializable {

    private static final long serialVersionUID = 1513341649685896654L;

    private Long sceneId;

    /**
     * cloud的统一接收参数
     */
    private String resourceName;

    private List<Long> enginePluginIds;

    private List<ScenePluginParam> enginePlugins;

    private Boolean leakSqlEnable;

    private Boolean continueRead = false;
}
