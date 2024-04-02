package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName t_scene_base_line
 */
@TableName(value = "t_scene_base_line")
@Data
public class TSceneBaseLine {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long activityId;

    private String rpcId;

    private String rpcType;

    private Integer logType;


    private String serviceName;

    private String appName;

    private String methodName;

    private String middlewareName;

    private Long sceneId;

    private Long reportId;

    /**
     * 0：没有基线 、1：根据时间范围、2 ：根据过去的报告
     */
    private Integer lineType;

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
     * trace快照的id
     */
    private String traceId;

    /**
     * trace快照
     */
    private String traceSnapshot;

    private Integer samplingInterval;

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
}