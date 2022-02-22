/*
 * Copyright (c) 2021. Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.param.application.AppRemoteCallCreateParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;

/**
 * @author 无涯
 * @date 2021/5/29 12:14 上午
 */
public interface AppRemoteCallDAO extends IService<AppRemoteCallEntity> {

    /**
     * 新增
     */
    void insert(AppRemoteCallCreateParam param);

    /**
     * 新增
     */
    void batchInsert(List<AppRemoteCallCreateParam> param);

    /**
     * 批量更新
     */
    void batchSaveOrUpdate(List<AppRemoteCallUpdateParam> param);

    /**
     * 更新
     */
    void update(AppRemoteCallUpdateParam param);

    /**
     * 根据id获取
     *
     * @return
     */
    AppRemoteCallResult getResultById(Long id);

    /**
     * 根据id删除
     */
    void deleteById(Long id);

    /**
     * 根据id删除
     */
    void deleteByApplicationIds(List<Long> applicationIds);

    /**
     * 查询数据
     *
     * @return
     */
    List<AppRemoteCallResult> getList(AppRemoteCallQueryParam param);

    /**
     * 查询数据 无租户
     *
     * @return
     */
    List<AppRemoteCallEntity> getListWithOutTenant();

    /**
     * 批量更新
     * @param entities
     */
    void updateWithOutTenant(List<AppRemoteCallEntity> entities);

    /**
     * 查询数据 md5
     * @return
     */
    List<String> getRemoteCallMd5(AppRemoteCallQueryParam param);

    /**
     * 查询数据
     *
     * @return
     */
    PagingList<AppRemoteCallResult> pagingList(AppRemoteCallQueryParam param);

    /**
     * 根据应用名和租户查询
     * @param appName
     * @return
     */
    List<AppRemoteCallResult> selectByAppNameUnderCurrentUser(String appName);

    /**
     * 根据应用名和租户查询
     * @param appName
     * @param type
     * @return
     */
    List<AppRemoteCallResult> selectByAppNameAndType(String appName,Integer type);

    /**
     * 更新应用名
     */
    void updateAppName(Long applicationId, String appName);

    /**
     * 查询总记录数
     *
     * @param param
     * @return
     */
    Long getRecordCount(AppRemoteCallQueryParam param);

    /**
     * 数据查询分片
     *
     * @param param
     * @param start
     * @param size
     * @return
     */
    List<AppRemoteCallResult> getPartRecord(AppRemoteCallQueryParam param, long start, int size);

    /**
     * 批量配置
     * @param type
     * @param appIdList
     * @param userIdList
     * @return
     */
    List<AppRemoteCallResult> updateListSelective(Short type, List<Long> appIdList, List<Long> userIdList);

    /**
     * 根据id 批量逻辑删除
     *
     * @param ids
     */
    void batchLogicDelByIds(List<Long> ids);

    /**
     * 批量保存
     *
     * @param list
     */
    void batchSave(List<AppRemoteCallResult> list);

    /**
     * 查询全部有效的记录
     *
     * @return
     */
    List<AppRemoteCallResult> getAllRecord();

    AppRemoteCallResult queryOne(String appName, Integer interfaceType, String interfaceName);
}
