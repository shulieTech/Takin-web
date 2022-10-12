package com.pamirs.takin.entity.dao.confcenter;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.entity.TBList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 说明: 黑名单管理dao层
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月26日
 */
@Mapper
public interface TBListMntDao {

    /**
     * 说明: 判断该黑名单redisKey是否已经存在
     *
     * @param redisKey redis的key
     * @return 大于0表示已经存在, 反之不存在
     * @author shulie
     */
    int bListExist(@Param("redisKey") String redisKey);

    /**
     * 说明:  添加黑名单接口信息
     *
     * @param tBList 黑名单实体类
     * @author shulie
     */
    void addBList(TBList tBList);

    /**
     * 说明: 查询黑名单列表
     *
     * @param redisKey    redis的key
     * @param principalNo 负责人工号
     * @return 黑名单列表
     * @author shulie
     */
    List<TBList> queryBList(@Param("redisKey") String redisKey,
        @Param("principalNo") String principalNo,@Param("userIds") List<Long> userIds, @Param("deptIds") List<Long> deptIds);

    /**
     * 说明: 根据id查询黑名单信息
     *
     * @param blistId 黑名单id
     * @return 单个黑名单详情
     * @author shulie
     */
    TBList querySingleBListById(@Param("blistId") String blistId);

    /**
     * 说明: 根据id更新黑名单信息
     *
     * @param tBList 黑名单实体类
     * @author shulie
     */
    void updateBListById(TBList tBList);

    /**
     * 说明: 批量删除黑名单信息
     *
     * @param blistIds 黑名单id(多个id以逗号拼接)
     * @author shulie
     */
    void deleteBListByIds(@Param("blistIds") List<String> blistIds);

    /**
     * 说明: 根据id列表批量查询黑名单信息
     *
     * @param blistIds 黑名单id(多个id以逗号拼接)
     * @author shulie
     */
    List<TBList> queryBListByIds(@Param("blistIds") List<String> blistIds);

    /**
     * 说明: 无参数查询黑名单列表
     *
     * @return 黑名单列表
     * @author shulie
     */
    List<Map<String, Object>> queryBListList();

    /**
     * 说明: 查询所有启用的黑名单列表
     *
     * @return 黑名单列表
     * @author shulie
     */
    List<TBList> getAllEnabledBlockList(@Param("tenantId") Long tenantId,@Param("envCode") String envCode);

}
