/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pamirs.takin.entity.dao.confcenter;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.pamirs.takin.entity.domain.entity.TWList;
import com.pamirs.takin.entity.domain.query.TWListVo;
import com.pamirs.takin.entity.domain.entity.TPradaHttpData;
import com.pamirs.takin.entity.domain.vo.TApplicationInterface;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceDataVo;
import com.pamirs.takin.entity.domain.vo.TLinkApplicationInterface;

/**
 * 说明: 白名单管理dao层
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月26日
 */
@Mapper
public interface TWhiteListMntDao {

    /**
     * 说明: 根据应用id删除应用信息接口(删除应用下的服务)
     *
     * @param applicationIdLists 应用id集合
     * @author shulie
     */
    void deleteApplicationInfoRelatedInterfaceByIds(
        @Param("applicationRelatedInterfaceLists") List<String> applicationIdLists);

    /**
     * 说明: 判断该白名单接口是否已经存在
     *
     * @param interfaceName 接口名称
     * @return 大于0表示已经存在, 反之不存在
     * @author shulie
     */
    int whiteListExist(@Param("appId") String appId,
        @Param("interfaceName") String interfaceName,
        @Param("useYn") String useYn
    );

    /**
     * 说明: 批量加入白名单状态
     *
     * @param whiteListIdList 白名单ID列表
     * @return -
     */
    int batchEnableWhiteList(@Param("wlistIdList") List<Long> whiteListIdList);

    /**
     * 说明: 批量移出白名单状态
     *
     * @param whiteListIdList 白名单ID列表
     * @return -
     */
    int batchDisableWhiteList(@Param("wlistIdList") List<Long> whiteListIdList);

    /**
     * 说明:  添加白名单接口信息
     *
     * @param tWhiteList 白名单实体类
     * @author shulie
     */
    public void addWhiteList(TWList tWhiteList);

    /**
     * 说明: 查询白名单列表
     *
     * @param linkName        链路名称
     * @param applicationName 应用名称
     * @param principalNo     负责人工号
     * @param type            白名单类型
     * @return 链路应用服务列表
     * @author shulie
     */
    List<TLinkApplicationInterface> queryWhiteListInfo(
        @Param("linkName") String linkName,
        @Param("applicationName") String applicationName,
        @Param("principalNo") String principalNo,
        @Param("type") String type);

    /**
     * 说明: 当链路为空查询不到查询白名单列表时，根据应用名称,负责人和白名单类型查询应用信息
     *
     * @param applicationName 应用名称
     * @param principalNo     负责人
     * @param type            白名单类型
     * @return 应用服务列表
     * @author shulie
     */
    List<TApplicationInterface> queryWhiteList(
        @Param("applicationName") String applicationName,
        @Param("principalNo") String principalNo,
        @Param("type") String type);

    /**
     * 说明: 根据id查询单个白名单信息
     *
     * @param wlistId 白名单id
     * @return 单条白名单信息
     * @author shulie
     */
    TWList querySingleWhiteListById(@Param("wlistId") String wlistId);

    /**
     * 说明:  根据id更新白名单信息
     *
     * @param tWhiteList 白名单实体类
     * @author shulie
     */
    void updateWhiteListById(TWList tWhiteList);

    void updateSelective(TWList tWhiteList);

    /**
     * 说明: 删除白名单接口
     *
     * @param whitelistIds 白名单ids
     * @author shulie
     */
    public void deleteWhiteListByIds(@Param("wlistIds") List<String> whitelistIds);

    /**
     * 说明: 删除白名单接口
     *
     * @param whiteListIds 白名单ids
     * @author shulie
     */
    void deleteByIds(@Param("wlistIds") List<Long> whiteListIds);

    /**
     * 说明: 根据id列表批量查询白名单信息
     *
     * @param whiteListIds 白名单ids
     * @return 白名单集合
     * @author shulie
     * @date 2018/11/5 15:13
     */
    List<TWList> queryWhiteListByIds(@Param("wlistIds") List<String> whiteListIds);

    /**
     * 说明: 根据id列表批量查询白名单信息
     *
     * @date 2018/11/5 15:13
     */
    List<TWList> getWhiteListByIds(@Param("wlistIds") List<Long> whiteListIds);

    /**
     * 说明: 查询白名单列表
     *
     * @param applicationName 应用名称，可选
     * @return 白名单列表
     * @author shulie
     */
    List<Map<String, Object>> queryWhiteListList(
        @Param("applicationName") String applicationName);

    /**
     * 说明: 当链路应用服务查询不到时,查询白名单列表接口 默认时间倒序
     *
     * @param applicationName 应用名称
     * @param principalNo     负责人工号
     * @param type            白名单类型
     * @return 白名单列表
     * @author shulie
     */
    List<TApplicationInterface> queryOnlyWhiteList(
        @Param("applicationName") String applicationName,
        @Param("principalNo") String principalNo,
        @Param("type") String type,
        @Param("whiteListUrl") String whiteListUrl,
        @Param("wlistIds") List<String> wlistIds,
        @Param("applicationId") Long applicationId);

    List<TWList> queryWhiteListTotalByApplicationId(@Param("applicationId") Long applicationId);

    //@DataAuth

    /**
     * 去除用户隔离级别权限
     *
     * @return -
     */
    List<TWList> queryDistinctWhiteListTotalByApplicationId(
        @Param("applicationId") Long applicationId,
        @Param("customerId") Long customerId);

    /**
     * 说明: 根据白名单id查询是否在基础链路中使用
     *
     * @param wListId 白名单id
     * @return 存在的基础链路个数和基础链路名称
     * @author shulie
     * @date 2018/7/10 11:07
     */
    Map<String, Object> queryWhiteListRelationBasicLinkByWhiteListId(
        @Param("wListId") String wListId);

    /**
     * 说明: prada数据同步到mysql
     *
     * @param list 带插入数据
     * @author shulie
     * @date 2019/3/4 20:15
     */
    void pradaDataInsertToMysql(List<TPradaHttpData> list);

    /**
     * 说明: 清除同步数据
     *
     * @param tableName 表名
     * @author shulie
     * @date 2019/3/4 20:37
     */
    void truncateTable(@Param("tableName") String tableName);

    /**
     * 说明: 根据应用和接口名称查询应用下http接口数据
     *
     * @param applicationName 表名
     * @param interfaceName   接口名称
     * @author shulie
     * @date 2019/3/4 20:37
     */
    List<TPradaHttpData> queryInterfaceByAppNameByTPHD(
        @Param("applicationName") String applicationName,
        @Param("type") String type,
        @Param("interfaceName") String interfaceName);

    /**
     * 说明: 根据应用和接口名称查询应用下dubbo/job接口数据
     *
     * @param applicationName 表名
     * @param interfaceName   接口名称
     * @author shulie
     * @date 2019/3/4 20:37
     */
    List<TUploadInterfaceDataVo> queryInterfaceByAppNameFromTUID(
        @Param("applicationName") String applicationName,
        @Param("type") String type,
        @Param("interfaceName") String interfaceName);

    /**
     * 说明: 批量增加白名单
     *
     * @author shulie
     * @date 2019/4/3 20:01
     */
    void batchAddWhiteList(@Param("twLists") List<TWList> twLists);

    /**
     * 说明: 根据MQ信息查询白名单数量
     *
     * @return int
     * @author shulie
     * @date 2019/4/3 20:02
     */
    int queryWhiteListCountByMqInfo(TWListVo twListVo);

    /**
     * 给链路提供白名单列表，增加到链路服务中
     *
     * @return -
     */
    List<Map<String, String>> getWhiteListForLink();

    /**
     * 查询白名单列表
     *
     * @param applicationId 应用ID
     * @return -
     */
    List<Map<String, Object>> queryWhiteListByAppId(@Param("applicationId") String applicationId);

    /**
     * 查询白名单
     *
     * @return -
     */
    TWList getWhiteListByParam(Map<String, String> queryMap);

    List<Map<String, Object>> getWhiteListByAppIds(@Param("ids") List<String> ids);

    List<TWList> getWhiteListByApplicationId(Long applicationId);

    List<TWList> getAllEnableWhitelists(@Param("applicationId") String applicationId);
}

