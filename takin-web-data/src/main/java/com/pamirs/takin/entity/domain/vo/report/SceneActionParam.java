package com.pamirs.takin.entity.domain.vo.report;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.web.common.domain.WebRequest;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-21
 */
@Data
public class SceneActionParam extends WebRequest implements Serializable {

    private static final long serialVersionUID = 1513341649685896654L;

    private Long sceneId;

    private List<Long> enginePluginIds;

    private List<ScenePluginParam> enginePlugins;

    private Boolean leakSqlEnable;

    private String continueRead;

    /**
     *  来源名称。压测报告取场景名称、流量验证取业务活动名称、脚本调试取脚本名称
     */
    private String resourceName;

    private Long creatorId;
}
