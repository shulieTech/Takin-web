package io.shulie.takin.adapter.api.model.request.pressure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.shulie.takin.adapter.api.constant.FormulaSymbol;
import io.shulie.takin.adapter.api.constant.FormulaTarget;
import io.shulie.takin.adapter.api.constant.JobType;
import io.shulie.takin.adapter.api.constant.ThreadGroupType;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.model.request.StartRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PressureTaskStartReq extends ContextExt {

    /**
     * 状态接口回调地址
     */
    private String callbackUrl;
    /**
     * 资源Id
     */
    private Long resourceId;
    /**
     * jvm参数
     */
    private String jvmOptions;
    /**
     * 采样率
     */
    private Integer sampling;
    /**
     * 任务类型
     * 0-常规模式
     * 3-流量模式
     * 4-巡检模式
     * 5-试跑模式
     */
    private JobType type;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 脚本文件(jmx)
     */
    private StartRequest.FileInfo scriptFile;
    /**
     * 运行时依赖文件(插件)
     */
    private List<StartRequest.FileInfo> dependencyFile = new ArrayList<>();
    /**
     * 数据文件(csv)
     */
    private List<StartRequest.FileInfo> dataFile = new ArrayList<>();
    /**
     * 线程配置
     */
    private List<ThreadConfigInfo> threadConfig;
    /**
     * sla配置
     */
    private List<SlaInfo> slaConfig;
    private List<StartRequest.MetricsInfo> metricsConfig;
    private Boolean bindByXpathMd5; // 标识新旧数据
    /**
     * 调试模式下脚本调试配置
     */
    private Map<String, String> ext;

    @Data
    public static class ThreadConfigInfo {
        private String ref;
        private ThreadGroupType type;
        private Integer duration;
        private Integer number;
        private Integer tps;
        private Integer growthTime;
        private Integer growthStep;
    }

    @Data
    public static class SlaInfo {
        private String ref;
        private String attach;
        private FormulaTarget formulaTarget;
        private FormulaSymbol formulaSymbol;
        private Double formulaNumber;
    }
}


