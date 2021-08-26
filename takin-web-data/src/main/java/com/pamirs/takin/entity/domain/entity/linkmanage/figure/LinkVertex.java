package com.pamirs.takin.entity.domain.entity.linkmanage.figure;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.linkmanage.VertexOpData;
import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import lombok.Data;

/**
 * vernon
 */
@Data
public class LinkVertex {
    List<MiddleWareEntity> middleWare;
    private Object id;
    private String applicationName;
    private Integer rpcType;
    private Long updateTime;
    private VertexOpData vertexOpData;

    public LinkVertex(Object id, String applicationName, Integer rpcType, Long updateTime,
        List<MiddleWareEntity> middleWare, VertexOpData vertexOpData) {
        this.id = id;
        this.applicationName = applicationName;
        this.rpcType = rpcType;
        this.updateTime = updateTime;
        this.middleWare = middleWare;
        this.vertexOpData = vertexOpData;
    }

}
