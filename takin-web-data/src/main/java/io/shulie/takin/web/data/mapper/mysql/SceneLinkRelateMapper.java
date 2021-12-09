package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowTree;
import io.shulie.takin.web.data.model.mysql.SceneLinkRelateEntity;
import org.apache.ibatis.annotations.Param;

public interface SceneLinkRelateMapper extends BaseMapper<SceneLinkRelateEntity> {

    /**
     * 查询链路个数
     * @param techLinkIds
     * @return
     */
    int countByTechLinkIds(@Param("list") List<String> techLinkIds);

    /**
     * 业务id, frontUuidKey, flowId
     *
     * @param flowId 业务流程id
     * @param tenantId 租户id
     * @param envCode 环境表示
     * @return tree列表
     */
    List<BusinessFlowTree> listRecursion(@Param("flowId") Long flowId, @Param("tenantId") Long tenantId, @Param("envCode") String envCode);

}
