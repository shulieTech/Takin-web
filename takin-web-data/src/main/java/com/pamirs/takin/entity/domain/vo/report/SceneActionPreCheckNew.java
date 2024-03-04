package com.pamirs.takin.entity.domain.vo.report;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

import java.util.List;

/**
 * @author zhangz
 * Created on 2024/3/4 10:32
 * Email: zz052831@163.com
 */

@Data
public class SceneActionPreCheckNew extends ContextExt {
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
    private List<PressureFileInfo> pressureFileInfos;

    @Data
    public static class PressureFileInfo {
        private String fileName;
        private String continueRead;
    }

}
