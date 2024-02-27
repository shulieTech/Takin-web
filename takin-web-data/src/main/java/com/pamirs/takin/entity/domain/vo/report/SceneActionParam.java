package com.pamirs.takin.entity.domain.vo.report;

import io.shulie.takin.adapter.api.model.request.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 莫问
 * @date 2020-04-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneActionParam extends ContextExt {

    private Long sceneId;

    private List<Long> enginePluginIds;

    private List<ScenePluginParam> enginePlugins;

    private Boolean leakSqlEnable;

    private String continueRead;

    /**
     * 来源名称。压测报告取场景名称、流量验证取业务活动名称、脚本调试取脚本名称
     */
    private String resourceName;

    /**
     * 资源id
     */
    private String resourceId;

    /**
     * 选择的压力机集群
     */
    private String machineId;

    /**
     * 资源Id
     */
    private String pressureResourceId;

    /**
     * 文件是否继续压测
     */
    private List<SceneTaskStartReq.PressureFileInfo> pressureFileInfos;

    @Data
    public static class PressureFileInfo{
        private String fileName;
        private Boolean continueRead;
    }
}
