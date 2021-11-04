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

package io.shulie.takin.web.biz.service.linkmanage;

import java.util.List;
import java.util.Map;

import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.AppRemoteCallOutput;
import io.shulie.takin.web.biz.pojo.output.application.AppRemoteCallOutputV2;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallCreateV2Request;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallUpdateV2Request;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallConfigRequest;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.common.vo.application.AppRemoteCallListVO;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;

/**
 * @author 无涯
 * @date 2021/5/29 12:31 上午
 */
public interface AppRemoteCallService {

    /**
     * 更新
     */
    void update(AppRemoteCallUpdateInput input);

    /**
     * 根据id获取
     *
     * @return
     */
    AppRemoteCallOutput getById(Long id);

    /**
     * 根据id删除
     */
    void deleteById(Long id);

    /**
     * 分页查询
     *
     * @return
     */
    PagingList<AppRemoteCallListVO> pagingList(AppRemoteCallQueryInput input);

    /**
     * 获取异常远程配置
     *
     * @return
     */
    String getException(Long applicationId);

    /**
     * 根据配置类型筛选数据
     *
     * @return
     */
    List<SelectVO> getConfigSelect(Integer interfaceType, String serverAppName);

    /**
     * agent获取数据
     *
     * @return
     */
    AgentRemoteCallVO agentSelect(String appName);

    /**
     * 同步amdb数据
     */
    void syncAmdb();

    /**
     * 获取服务端应用
     *
     * @return
     */
    Map<String, List<ApplicationRemoteCallDTO>> getServerAppListMap(String upNames);

    /**
     * 根据应用删除
     */
    void deleteByApplicationIds(List<Long> applicationIds);

    /**
     * 批量配置白名单
     * @param request
     */
    void batchConfig(AppRemoteCallConfigRequest request);


    /**
     * 根据配置类型筛选数据
     *
     * @return
     */
    List<SelectVO> getConfigSelectV2(String interfaceType);

    /**
     * 根据接口类型筛选数据
     *
     * @return
     */
    List<SelectVO> getInterfaceTypeSelect();

    /**
     * 获取服务端应用的接口
     * @return
     */
    Map<Long,List<AppRemoteCallResult>> getListGroupByAppId();

    /**
     * 根据id批量逻辑删除
     * @param ids
     */
    void batchLogicDelByIds(List<Long> ids);

    /**
     * 批量保存
     * @param list
     */
    void batchSave(List<AppRemoteCallResult> list);

    void create(AppRemoteCallCreateV2Request request);

    /**
     * 更新
     */
    void updateV2(AppRemoteCallUpdateV2Request request);

    /**
     * 根据id获取
     *
     * @return
     */
    AppRemoteCallOutputV2 getByIdV2(Long id);
}
