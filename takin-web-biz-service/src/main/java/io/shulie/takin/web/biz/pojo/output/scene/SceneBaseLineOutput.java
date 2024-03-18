package io.shulie.takin.web.biz.pojo.output.scene;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zhangz
 * Created on 2024/3/14 19:19
 * Email: zz052831@163.com
 */

@Data
public class SceneBaseLineOutput {
    private Long activityId;
    private String activityName;
    private BigDecimal rt;
    private BigDecimal tps;
    private BigDecimal successRate;
    private List<SceneBaseLineNode> nodeList;

    @Data
    public static class SceneBaseLineNode {
        private Long id;
        private Long activityId;
        private Long sceneId;
        private Long reportId;
        private Integer lineType;
        private String rpcId;
        private Integer logType;
        private String serviceName;
        private String appName;
        private String methodName;
        private String rpcType;
        private Date startTime;
        private Date endTime;
        private String binRef;
        private BigDecimal rt;
        private BigDecimal tps;
        private BigDecimal successRate;
        private BigDecimal totalRequest;
        private String traceSnapshot;
    }

}
