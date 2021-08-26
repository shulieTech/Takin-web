package com.pamirs.takin.entity.domain.vo.report;

import io.shulie.takin.web.common.domain.WebRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 莫问
 * @date 2020-04-21
 */
@Data
public class SceneActionParamNew extends WebRequest implements Serializable {

    private static final long serialVersionUID = 1513341649685896654L;

    private Long sceneId;

    private List<Long> enginePluginIds;

    private Boolean leakSqlEnable;

    private Boolean continueRead = false;
}
