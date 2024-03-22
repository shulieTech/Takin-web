package io.shulie.takin.web.biz.pojo.output.scene;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangz
 * Created on 2024/3/16 17:39
 * Email: zz052831@163.com
 */

@Data
public class TReportBaseLinkProblemOutput {
    private Long activityId;
    private String activityName;
    private String traceId;
    private String traceSnapshot;
    private Integer totalRequest;
    private BigDecimal maxOptimizableRt;
    private List<BaseLineProblemNode> baseLineProblemNodes;

    @Data
    public static class BaseLineProblemNode {
        /**
         *
         */
        private Long id;

        /**
         *
         */
        private Long sceneId;

        /**
         *
         */
        private Long reportId;

        /**
         *
         */
        private Integer lineType;

        private String rpcId;

        private String rpcType;

        private Integer logType;

        /**
         *
         */
        private String binRef;

        /**
         *
         */
        private Long activityId;

        private String activityName;

        /**
         * 节点服务名
         */
        private String serviceName;

        /**
         * 节点appName
         */
        private String appName;
        /**
         * 节点方法
         */
        private String methodName;

        private String middlewareName;
        /**
         * 节点问题原因
         */
        private String reason;

        /**
         * 基线rt
         */
        private BigDecimal baseRt;

        /**
         * 基线成功率
         */
        private BigDecimal baseSuccessRate;

        /**
         * 当前报告的rt
         */
        private BigDecimal rt;

        /**
         * 当前报告的成功率
         */
        private BigDecimal successRate;

        private BigDecimal totalRequest;

        private BigDecimal totalOptimizableRt;
        /**
         * trace快照
         */
        private String traceSnapshot;

        private String traceId;

        private Integer samplingInterval;
    }

}
