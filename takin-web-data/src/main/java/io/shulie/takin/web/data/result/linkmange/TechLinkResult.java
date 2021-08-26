package io.shulie.takin.web.data.result.linkmange;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.dto.linkmanage.TopologicalGraphEntity;
import com.pamirs.takin.entity.domain.dto.linkmanage.UnKnowNode;
import com.pamirs.takin.entity.domain.entity.linkmanage.figure.LinkEdge;
import com.pamirs.takin.entity.domain.entity.linkmanage.figure.LinkVertex;
import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import lombok.Data;

/**
 * 技术链路出参
 *
 * @author vernon
 * @date 2019/11/29 15:40
 */
@Data
public class TechLinkResult {

    private Long linkId;
    private String techLinkName;
    private String techLinkCount;
    private String isChange;
    private String changeRemark;
    private String bodyBefore;
    private String bodyAfter;
    private String linkNode;
    private String changeType;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private String entrance;
    private String candelete;
    private List<MiddleWareEntity> middleWareEntities;
    private TopologicalGraphEntity topologicalGraphEntity;
    private String requestParam;
    private List<UnKnowNode> unKnowNodeList;
    private Map<Object, LinkVertex> linkVertexMap;
    private List<LinkEdge> edges;

}
