package com.pamirs.takin.entity.dao.common;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.entity.AbstractRelationLinkModel;
import com.pamirs.takin.entity.domain.vo.TLinkApplicationInterfaceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 说明: 公共dao层
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月26日
 */
@Mapper
public interface CommonDao {

    /**
     * 说明: 根据链路id查询链路应用服务信息插入到链路检测表中
     *
     * @param linkId 链路id
     * @return 链路应用服务信息
     * @author shulie
     */
    List<TLinkApplicationInterfaceVo> selectLinkWholeInfo(@Param("linkId") String linkId);

    /**
     * 说明: 新增链路时查询数据更新到数据构建表中
     *
     * @param linkId 链路id
     * @return 链路应用列表
     * @author shulie
     */
    List<TLinkApplicationInterfaceVo> selectLinkApplicationInfo(@Param("linkId") String linkId);

    /**
     * 说明: 插入关联关系链路表
     *
     * @param abstractRelationLinkModel 关联关系链路模型
     * @author shulie
     * @date 2018/12/26 17:57
     */
    void saveRelationLink(AbstractRelationLinkModel abstractRelationLinkModel);

    List<Map<String, Object>> queryRelationLinkRelationShip(@Param("objTable") String objTable,
        @Param("parentLinkId") String parentLinkId);
}
