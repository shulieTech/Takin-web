package com.pamirs.takin.entity.dao.pressurecontrol;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.TLinkMnt;
import com.pamirs.takin.entity.domain.query.TechLink;
import com.pamirs.takin.entity.domain.vo.TSecondBasicLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 说明: 压测控制dao
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/6/18 11:49
 */
@Mapper
public interface TPMOverviewMapDao {

    /**
     * 说明: 根据二级链路id,二级链路名称,基础链路名称查询二级链路名称和二级链路id集合
     *
     * @param secondLinkName 二级链路名称
     * @param basicLinkName  基础链路名称
     * @return 二级链路id和二级链路名称集合
     * @author shulie
     * @date 2018/6/18 13:14
     */
    List<TSecondBasicLink> querySecondLinkName(@Param("secondLinkId") String secondLinkId,
        @Param("secondLinkName") String secondLinkName,
        @Param("basicLinkName") String basicLinkName);

    /**
     * 说明: 根据二级链路id查询该二级链路下的所有基础链路信息
     *
     * @param secondLinkId 二级链路id
     * @return 二级链路下的所有基础链路信息集合
     * @author shulie
     * @date 2018/6/18 17:04
     */
    List<TLinkMnt> queryBasicLinkInfo(@Param("secondLinkId") String secondLinkId);

    /**
     * 说明: 根据业务链路id查询关联的技术链路信息
     *
     * @param bLinkId 业务链路id
     * @return 业务链路关联的技术链路信息集合
     * @author shulie
     * @date 2019/1/2 15:27
     */
    List<TechLink> queryTechLinkInfo(@Param("bLinkId") String bLinkId);
}
