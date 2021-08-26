package com.pamirs.takin.entity.domain.entity.linkmanage.figure;

import lombok.Data;

/**
 * 封装返回边数据
 *
 * @author guohz
 * @date 2019/12/5 4:27 下午
 */
@Data
public class LinkEdge {
    private String secondIndex;
    private Object id;
    private Object inVid;
    private Object outVid;
    private String edgeId;
    private String tradeAppName;
    private String rpcId;
    private Integer rpcType;
    private String serviceName;
    private String applicationName;
    private Long invokeCount;
    private Long createTime;
    private Long updateTime;
    private String requestParam;
    private String event;

    public LinkEdge(String secondIndex, Object id, Object inVid, Object outVid, String edgeId, String tradeAppName,
        String applicationName, String rpcId, Integer rpcType, String serviceName, Long invokeCount, Long createTime,
        Long updateTime, String requestParam, String event) {
        this.id = id;
        this.inVid = inVid;
        this.outVid = outVid;
        this.edgeId = edgeId;
        this.tradeAppName = tradeAppName;
        this.applicationName = applicationName;
        this.rpcId = rpcId;
        this.rpcType = rpcType;
        this.serviceName = serviceName;
        this.invokeCount = invokeCount;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.secondIndex = secondIndex;
        this.requestParam = requestParam;
        this.event = event;
    }
}
