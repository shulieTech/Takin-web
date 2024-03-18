package io.shulie.takin.web.biz.pojo.request.scene;

import io.shulie.takin.web.biz.pojo.output.scene.EntryTraceAvgCostOutput;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

/**
 * @author zhangz
 * Created on 2024/3/12 15:53
 * Email: zz052831@163.com
 */

@Data
public class SceneBaseLineInsertDto {
    /**
     *
     */
    private Long id;

    /**
     *
     */
    private Long activityId;

    private Long reportId;

    private Long sceneId;

    /**
     * 0：没有基线 、1：根据时间范围、2 ：根据过去的报告
     */
    private Integer lineType;

    private String rpcId;

    private Integer logType;

    private String rpcType;

    private String appName;
    private String serviceName;
    private String methodName;
    /**
     *
     */
    private Date startTime;

    /**
     *
     */
    private Date endTime;

    /**
     *
     */
    private String binRef;

    /**
     *
     */
    private BigDecimal rt;

    /**
     *
     */
    private BigDecimal tps;

    /**
     *
     */
    private BigDecimal successRate;

    private BigDecimal totalRequest;

    /**
     * trace快照
     */
    private String traceSnapshot;

    /**
     *
     */
    private Integer isDelete;

    /**
     *
     */
    private String envCode;

    /**
     *
     */
    private Long tenantId;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private String createName;

    /**
     *
     */
    private Date updateTime;

    /**
     *
     */
    private String updateName;

    public static SceneBaseLineInsertDto genOb(EntryTraceAvgCostOutput dto, BaseLineQueryReq baseLineQueryReq) {
        SceneBaseLineInsertDto base = new SceneBaseLineInsertDto();
        base.setActivityId(dto.getActivityId());
        base.setReportId(Optional.ofNullable(baseLineQueryReq.getReportId()).orElse(null));
        base.setSceneId(Optional.ofNullable(baseLineQueryReq.getSceneId()).orElse(null));
        base.setLineType(baseLineQueryReq.getLineTypeEnum());
        base.setRpcId(dto.getRpcId());
        base.setLogType(dto.getLogType());
        base.setRpcType(dto.getRpcType());
        base.setAppName(dto.getAppName());
        base.setServiceName(dto.getServiceName());
        base.setMethodName(dto.getMethodName());
        base.setStartTime(new Date(baseLineQueryReq.getBaseLineStartTime()));
        base.setEndTime(new Date(baseLineQueryReq.getBaseLineEndTime()));
        base.setRt(dto.getAvgCost());
        base.setSuccessRate(dto.getSuccessRate());
        base.setTotalRequest(dto.getTotalCount());
        return base;
    }
}
