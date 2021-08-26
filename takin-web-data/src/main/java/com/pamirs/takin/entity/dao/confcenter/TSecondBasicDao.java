package com.pamirs.takin.entity.dao.confcenter;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.entity.TLinkMnt;
import com.pamirs.takin.entity.domain.entity.TSecondBasic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author shulie
 * @description 二级/基础链路关联关系Dao层
 * @create 2018-06-19 10:33:21
 */
@Mapper
public interface TSecondBasicDao {

    /**
     * @param secondBasic 　二级基础链路实体
     * @description 保存二级链路和基础链路关系
     * @author shulie
     * @create 2018/6/19 10:39
     */
    void saveSecondBasicLink(TSecondBasic secondBasic);

    /**
     * @param linkIdLists 二级链路id列表
     * @description 删除二级/基础链路关系
     * @author shulie
     * @create 2018/6/19 15:56
     */
    void deleteRefBySecondLinkId(@Param("linkIdLists") List<String> linkIdLists);

    /**
     * 说明: 根据id列表批量查询二级链路关联映射关系数据
     *
     * @param secondLinkIds 二级链路id列表
     * @return 二级链路关联映射关系数据集合
     * @author shulie
     * @date 2018/11/5 16:43
     */
    List<Map<String, Object>> querySecondBasicLinkListByIds(@Param("secondLinkIds") List<String> secondLinkIds);

    /**
     * @param secondLinkId 二级链路id
     * @return java.util.List<com.pamirs.takin.entity.domain.TSecondBasic>
     * @description 根据二级链路id查询二级/基础关系列表
     * @author shulie
     * @create 2018/6/19 20:25
     */
    List<TSecondBasic> querySecondBasicLinkBySecondLinkId(@Param("secondLinkId") String secondLinkId);

    /**
     * @param secondLinkId 二级链路id
     * @return java.util.List<com.pamirs.takin.entity.domain.TSecondBasic>
     * @description 根据二级链路id查询二级/基础关系列表
     * @author shulie
     * @create 2018/6/19 20:25
     */
    List<Map<String, Object>> querySecondBasicLinkBySecondLinkIdModify(@Param("secondLinkId") String secondLinkId);

    /**
     * @param linkId 二级链路id
     * @return 二级链路下的基础链路列表个数
     * @description
     * @author shulie
     * @create 2018/6/27 21:46
     */
    int getBasicLinkBankCount(@Param("secondLinkId") String linkId);

    /**
     * @param secondLinkId 二级链路id
     * @return
     * @description 查询二级/基础链路关系
     * @author shulie
     * @create 2018/6/28 0:13
     */
    List<TLinkMnt> querySecondBasicLinkRelationBySecondLinkId(@Param("secondLinkId") String secondLinkId);

    /**
     * @param secondLinkId 二级链路id
     * @return java.util.List<java.lang.Integer>
     * @description
     * @author shulie
     * @create 2018/6/28 17:32
     */
    List<Integer> queryBasicLinkBank(@Param("secondLinkId") String secondLinkId);
}
