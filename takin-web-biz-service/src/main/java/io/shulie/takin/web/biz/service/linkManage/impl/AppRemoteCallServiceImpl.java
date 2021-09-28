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

package io.shulie.takin.web.biz.service.linkManage.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.dao.confcenter.TBListMntDao;
import com.pamirs.takin.entity.dao.confcenter.TBaseConfigDao;
import com.pamirs.takin.entity.domain.entity.TBList;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationRemoteCallQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.AppRemoteCallOutput;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallConfigRequest;
import io.shulie.takin.web.biz.service.linkManage.AppRemoteCallService;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.common.enums.application.AppRemoteCallTypeEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import io.shulie.takin.web.common.vo.agent.AgentBlacklistVO;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO.Blacklist;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO.RemoteCall;
import io.shulie.takin.web.common.vo.application.AppRemoteCallListVO;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.blacklist.BlackListDAO;
import io.shulie.takin.web.data.dao.dictionary.DictionaryDataDAO;
import io.shulie.takin.web.data.param.application.AppRemoteCallCreateParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.blacklist.BlacklistResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2021/5/29 12:31 上午
 */
@Slf4j
@Service
public class AppRemoteCallServiceImpl implements AppRemoteCallService {
    @Autowired
    private AppRemoteCallDAO appRemoteCallDAO;
    @Autowired
    private ApplicationDAO applicationDAO;
    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private DictionaryDataDAO dictionaryDataDAO;

    @Autowired
    private TBaseConfigDao tBaseConfigDao;

    @Autowired
    private TBListMntDao tbListMntDao;

    @Autowired
    private BlackListDAO blackListDAO;

    @Autowired
    private ConfigSyncService configSyncService;

    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @Value("${remote.call.auto.join.white: false}")
    private boolean autoJoinWhiteFlag;

    @Value("${query.async.critica.value:20000}")
    private int criticaValue;

    @Autowired
    private ThreadPoolExecutor queryAsyncThreadPool;


    private void checkInputData(AppRemoteCallUpdateInput input) {
        //if (input.getType().equals(AppRemoteCallConfigEnum.RETURN_MOCK.getType()) || input.getType().equals(
        //    AppRemoteCallConfigEnum.FORWARD_MOCK.getType())) {
        //    if (StringUtils.isBlank(input.getMockReturnValue())) {
        //        throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "mock必须填写mock数据");
        //    }
        //}

        //if (input.getId() != null && input.getId() != 0) {
        //    if (RemoteCallUtils.checkWhite(input.getInterfaceType(),input.getType()) && StringUtils.isBlank(input.getServerAppName())) {
        //        throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "危险异常：已加入白名单，服务端有异常,请处理~");
        //    }
        //}
        //
        //if (RemoteCallUtils.checkWhite(input.getInterfaceType(),input.getType())) {
        //    // 白名单必须有服务端应用名
        //    if (StringUtils.isBlank(input.getServerAppName())) {
        //        throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "加入白名单必须有服务端应用");
        //    }
        //}

    }

    @Override
    public void update(AppRemoteCallUpdateInput input) {
        checkInputData(input);
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(input.getApplicationId());
        if (detailResult == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "应用不存在");
        }
        // 补充插件内容
        WebPluginUtils.fillUserData(input);
        if (input.getId() != null && input.getId() != 0) {
            getCallResult(input.getId());
            AppRemoteCallUpdateParam param = new AppRemoteCallUpdateParam();
            BeanUtils.copyProperties(input, param);

            param.setTenantId(detailResult.getCustomerId());
            param.setAppName(detailResult.getApplicationName());
            appRemoteCallDAO.update(param);
        } else {
            AppRemoteCallCreateParam param = new AppRemoteCallCreateParam();
            BeanUtils.copyProperties(input, param);
            param.setTenantId(detailResult.getCustomerId());
            param.setAppName(detailResult.getApplicationName());
            appRemoteCallDAO.insert(param);
        }
        agentConfigCacheManager.evictRecallCalls(detailResult.getApplicationName());

    }

    @Override
    public void batchConfig(AppRemoteCallConfigRequest request) {
        if(WebPluginUtils.validateSuperAdmin()) {
            appRemoteCallDAO.updateListSelective(request.getType(), request.getAppIds(), null);
        } else {
            appRemoteCallDAO.updateListSelective(request.getType(), request.getAppIds(), WebPluginUtils.getUpdateAllowUserIdList());
        }
    }

    @Override
    public AppRemoteCallOutput getById(Long id) {
        AppRemoteCallResult result = getCallResult(id);
        AppRemoteCallOutput output = new AppRemoteCallOutput();
        BeanUtils.copyProperties(result, output);
        AppRemoteCallConfigEnum callConfigEnum = AppRemoteCallConfigEnum.getEnum(output.getType());
        output.setTypeSelectVO(new SelectVO(callConfigEnum.getConfigName(), String.valueOf(callConfigEnum.getType())));
        // 支持类型
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        output.setInterfaceTypeSelectVO(getSelectVO(output.getInterfaceType(), voList));
        return output;
    }

    private SelectVO getSelectVO(Integer interfaceType, List<TDictionaryVo> voList) {
        AppRemoteCallTypeEnum callTypeEnum = AppRemoteCallTypeEnum.getEnum(interfaceType);
        if (callTypeEnum == null) {
            if (CollectionUtils.isEmpty(voList)) {
                return new SelectVO("数据字典未找到类型", String.valueOf(interfaceType));
            }
            List<TDictionaryVo> dictionaryVoList = voList.stream().filter(t -> interfaceType.equals(t.getValueCode())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(dictionaryVoList)) {
                return new SelectVO("数据字典未找到类型", String.valueOf(interfaceType));
            }
            TDictionaryVo vos = dictionaryVoList.get(0);
            return new SelectVO(vos.getValueName(), String.valueOf(interfaceType));
        } else {
            return new SelectVO(callTypeEnum.getDesc(), String.valueOf(callTypeEnum.getType()));
        }
    }

    private AppRemoteCallResult getCallResult(Long id) {
        AppRemoteCallResult result = appRemoteCallDAO.getResultById(id);
        if (result == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_GET_ERROR, "没有找到远程调用数据");
        }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        AppRemoteCallResult result = getCallResult(id);
        OperationLogContextHolder.addVars(Vars.INTERFACE, result.getInterfaceName());
        OperationLogContextHolder.addVars(Vars.INTERFACE_TYPE, AppRemoteCallTypeEnum.getEnum(result.getInterfaceType()).getDesc());
        appRemoteCallDAO.deleteById(id);
    }

    @Override
    public PagingList<AppRemoteCallListVO> pagingList(AppRemoteCallQueryInput input) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(input.getApplicationId());
        if (detailResult == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_GET_ERROR, "未找到应用");
        }
        // 查一次数据字典
        // 数据字段增加后，也在枚举中增加下
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        // 从mysql查出数据
        PagingList<AppRemoteCallListVO> dbPagingList = getDbPagingList(input, detailResult);
        // 根据应用的租户查询
        input.setTenantId(detailResult.getCustomerId());
        if (dbPagingList.getList().size() < input.getPageSize()) {
            // amdb查询 页码是第一页
            input.setCurrent(input.getCurrent() - (int)(dbPagingList.getTotal() / input.getPageSize()) - 1);
        }
        PagingList<AppRemoteCallListVO> amdbPagingList = getAmdbPagingList(input, detailResult, voList);
        List<AppRemoteCallListVO> results = Lists.newArrayList();
        if ((long)(input.getPageSize() * (input.getCurrent() + 1)) >= dbPagingList.getTotal()
            || dbPagingList.getList().size() < input.getPageSize()) {
            // 大于 数据库页数
            results.addAll(dbPagingList.getList());
            results.addAll(amdbPagingList.getList());
        } else {
            results.addAll(dbPagingList.getList());
        }
        // 所有服务端应用
        Map<String, List<ApplicationRemoteCallDTO>> serverAppNamesMap = this.getServerAppListMap(detailResult.getApplicationName());

        results.forEach(result -> {
            String appNameRemoteCallId = RemoteCallUtils.buildRemoteCallName(detailResult.getApplicationName(), result.getInterfaceName(),
                result.getInterfaceType());
            List<ApplicationRemoteCallDTO> callDtoList = serverAppNamesMap.get(appNameRemoteCallId);
            if (CollectionUtils.isNotEmpty(callDtoList)) {
                result.setServerAppNames(callDtoList.stream().map(ApplicationRemoteCallDTO::getAppName).collect(Collectors.toList()));
                result.setCount(result.getServerAppNames().size());
                result.setServerAppName(String.join(",", result.getServerAppNames()));
            } else {
                result.setServerAppName("");
            }
            // 白名单需要校验服务端应用
            result.setIsException(false);
            if (RemoteCallUtils.checkWhite(result.getInterfaceType(), result.getType()) && CollectionUtils.isEmpty(result.getServerAppNames())) {
                result.setIsException(true);
                result.setSort(0);
            }
            AppRemoteCallConfigEnum callConfigEnum = AppRemoteCallConfigEnum.getEnum(result.getType());
            result.setTypeSelectVO(new SelectVO(callConfigEnum.getConfigName(), String.valueOf(callConfigEnum.getType())));
            // 支持类型
            result.setInterfaceTypeSelectVO(getSelectVO(result.getInterfaceType(), voList));
        });
        return PagingList.of(results.stream().sorted(Comparator.comparing(AppRemoteCallListVO::getSort)).collect(Collectors.toList()),
            dbPagingList.getTotal() + amdbPagingList.getTotal());
    }

    private PagingList<AppRemoteCallListVO> getAmdbPagingList(AppRemoteCallQueryInput input, ApplicationDetailResult detailResult,
        List<TDictionaryVo> voList) {
        if (input.getType() != null && !input.getType().equals(AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType())) {
            return PagingList.empty();
        }
        ApplicationRemoteCallQueryDTO query = new ApplicationRemoteCallQueryDTO();

        if (detailResult != null && StringUtils.isNotBlank(detailResult.getApplicationName())) {
            query.setAppName(detailResult.getApplicationName());
        }
        query.setMiddlewareName(getMiddlewareName(voList));
        if (StringUtils.isNotBlank(input.getInterfaceName())) {
            query.setServiceName(input.getInterfaceName());
        }
        if (CollectionUtils.isNotEmpty(input.getAppNames())) {
            query.setAppName(String.join(",", input.getAppNames()));
        }

        // 出口
        query.setQueryTye("1");

        // 租户传参
        query.setUserAppKey(input.getUserAppKey());
        query.setEnvCode(input.getEnvCode());

        // 直接取全部数据
        PagingList<ApplicationRemoteCallDTO> calls = applicationClient.listApplicationRemoteCalls(query);
        if (calls.isEmpty()) {
            return PagingList.empty();
        }
        // 获取本地远程调用 数据
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        if (detailResult != null) {
            param.setApplicationId(detailResult.getApplicationId());
        }
        // 租户传参
        WebPluginUtils.transferTenantParam(input,param);

        List<AppRemoteCallResult> results = this.queryAsyncIfNecessary(param);
        List<String> appNameRemoteCallIds = results.stream().map(result -> {
            // 应用名
            return RemoteCallUtils.buildRemoteCallName(result.getAppName(), result.getInterfaceName(), result.getInterfaceType());
        }).collect(Collectors.toList());

        List<AppRemoteCallListVO> outputs = calls.getList().stream()
            .filter(call -> {
                String appNameRemoteCallId = RemoteCallUtils.buildRemoteCallName(call.getAppName(),
                    RemoteCallUtils.getInterfaceNameByRpcName(call.getMiddlewareName(), call.getServiceName(), call.getMethodName()),
                    getInterfaceType(call.getMiddlewareName(), voList));
                // 从AMDB加载过来的白名单，过滤掉类型不支持的远程调用--20210303 CYF
                Boolean flag = (appNameRemoteCallIds.stream().noneMatch(e -> e.equals(appNameRemoteCallId))) && (!"-1".equals(call.getRpcType()));
                return flag;
            })
            .map(call -> {
                AppRemoteCallListVO listVO = new AppRemoteCallListVO();
                listVO.setInterfaceName(
                    RemoteCallUtils.getInterfaceNameByRpcName(call.getMiddlewareName(), call.getServiceName(), call.getMethodName()));
                // 接口类型
                listVO.setInterfaceType(getInterfaceType(call.getMiddlewareName(), voList));
                listVO.setType(AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType());
                listVO.setSort(2);
                listVO.setAppName(call.getAppName());
                // 权限问题
                fillInPermissions(listVO, detailResult);
                return listVO;
            }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(outputs)) {
            return PagingList.empty();
        }
        if (input.getIsAll()) {
            // 返回全部
            return PagingList.of(outputs, outputs.size());
        }
        //进行物理分页
        return PagingList.of(outputs.size() < input.getPageSize() ?
                outputs : outputs.subList(input.getCurrent() * input.getPageSize(),
                ((input.getCurrent() + 1) * input.getPageSize() > outputs.size() ? (outputs.size() - 1)
                    : ((input.getCurrent() + 1) * input.getPageSize()))),
            outputs.size());
    }

    private String getMiddlewareName(List<TDictionaryVo> voList) {
        Set<String> middlewareNames = Sets.newSet();
        for (AppRemoteCallTypeEnum typeEnum : AppRemoteCallTypeEnum.values()) {
            middlewareNames.add(typeEnum.getDesc().toUpperCase());
        }
        voList.forEach(vo -> {
            middlewareNames.add(vo.getValueName().toUpperCase());
        });
        return String.join(",", middlewareNames);
    }

    private Integer getInterfaceType(String methodName, List<TDictionaryVo> voList) {
        AppRemoteCallTypeEnum typeEnum = AppRemoteCallTypeEnum.getEnumByDesc(methodName.toUpperCase());
        if (typeEnum == null) {
            if (CollectionUtils.isEmpty(voList)) {
                return -1;
            }
            List<TDictionaryVo> dictionaryVoList = voList.stream().filter(t -> methodName.equalsIgnoreCase(t.getValueName())).collect(
                Collectors.toList());
            if (CollectionUtils.isEmpty(dictionaryVoList)) {
                return -1;
            }
            return Integer.valueOf(dictionaryVoList.get(0).getValueCode());
        }

        return typeEnum.getType();

    }

    private PagingList<AppRemoteCallListVO> getDbPagingList(AppRemoteCallQueryInput input, ApplicationDetailResult detailResult) {
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        BeanUtils.copyProperties(input, param);
        PagingList<AppRemoteCallResult> pagingList = appRemoteCallDAO.pagingList(param);
        if (CollectionUtils.isEmpty(pagingList.getList())) {
            return PagingList.of(Lists.newArrayList(),pagingList.getTotal());
        }

        List<AppRemoteCallListVO> appRemoteCallVoList = pagingList.getList().stream().map(result -> {
            AppRemoteCallListVO listVO = new AppRemoteCallListVO();
            BeanUtils.copyProperties(result, listVO);
            listVO.setSort(1);
            // 权限问题
            fillInPermissions(listVO, detailResult);
            return listVO;
        }).collect(Collectors.toList());
        return PagingList.of(appRemoteCallVoList, pagingList.getTotal());
    }

    /**
     * 权限填充 用应用权限来控制
     */
    private void fillInPermissions(AppRemoteCallListVO listVO, ApplicationDetailResult detailResult) {
        List<Long> allowUpdateUserIdList = WebPluginUtils.getUpdateAllowUserIdList();
        if (CollectionUtils.isEmpty(allowUpdateUserIdList)) {
            //管理员
            listVO.setCanEdit(true);
        } else {
            //普通用户
            listVO.setCanEdit(allowUpdateUserIdList.contains(detailResult.getUserId()));
        }
        List<Long> allowDeleteUserIdList = WebPluginUtils.getDeleteAllowUserIdList();
        if (CollectionUtils.isEmpty(allowDeleteUserIdList)) {
            listVO.setCanRemove(true);
        } else {
            listVO.setCanRemove(allowDeleteUserIdList.contains(detailResult.getUserId()));
        }
        List<Long> allowEnableDisableUserIdList = WebPluginUtils.getEnableDisableAllowUserIdList();
        if (CollectionUtils.isNotEmpty(allowEnableDisableUserIdList)) {
            listVO.setCanEnableDisable(allowEnableDisableUserIdList.contains(detailResult.getUserId()));
        }
    }

    @Override
    public String getException(Long applicationId) {
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        param.setApplicationId(applicationId);
        List<AppRemoteCallResult> results = appRemoteCallDAO.getList(param);
        return String.valueOf(results.stream()
            .filter(result -> RemoteCallUtils.checkWhite(result.getInterfaceType(), result.getType())
                && StringUtils.isEmpty(result.getServerAppName())).count());
    }

    @Override
    public List<SelectVO> getConfigSelect(Integer interfaceType, String serverAppName) {
        // 数据字段增加后，也在枚举中增加下
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        List<SelectVO> vos = Arrays.stream(AppRemoteCallConfigEnum.values())
            .map(t -> new SelectVO(t.getConfigName(), String.valueOf(t.getType()))).collect(Collectors.toList());

        TBaseConfig tBaseConfig = tBaseConfigDao.selectByPrimaryKey("REMOTE_CALL_ABLE_CONFIG");
        if (tBaseConfig != null) {
            JSONObject jsonObject = JSONUtil.toBean(tBaseConfig.getConfigValue(), JSONObject.class);
            SelectVO selectVO = getSelectVO(interfaceType, voList);
            jsonObject.keySet().forEach(key -> {
                String value = jsonObject.getString(key);
                if (!value.contains(selectVO.getLabel())) {
                    AppRemoteCallConfigEnum configEnum = AppRemoteCallConfigEnum.getEnum(Integer.valueOf(key));
                    for (SelectVO vo : vos) {
                        if (vo.getValue().equals(String.valueOf(configEnum.getType()))) {
                            vo.setDisable(false);
                            break;
                        }
                    }
                }
            });
        }
        // 没有白名单 不能配置白名单，FEIGN 除外
        //if (RemoteCallUtils.checkWhite(interfaceType,null) && StringUtils.isEmpty(serverAppName)) {
        //    for (SelectVO vo : vos) {
        //        if (vo.getValue().equals(String.valueOf(AppRemoteCallConfigEnum.OPEN_WHITELIST.getType()))) {
        //            vo.setDisable(false);
        //            break;
        //        }
        //    }
        //}
        return vos;
    }

    @Override
    public AgentRemoteCallVO agentSelect(String appName) {
        AgentRemoteCallVO callVO = new AgentRemoteCallVO();
        List<AppRemoteCallResult> results = appRemoteCallDAO.selectByAppNameUnderCurrentUser(appName);
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");

        callVO.setWLists(results.stream().map(result -> {
            RemoteCall remoteCall = new RemoteCall();
            remoteCall.setINTERFACE_NAME(result.getInterfaceName());
            remoteCall.setTYPE(getSelectVO(result.getInterfaceType(), voList).getLabel().toLowerCase());
            remoteCall.setCheckType(AppRemoteCallConfigEnum.getEnum(result.getType()).getAgentCheckType());
            remoteCall.setContent(result.getMockReturnValue());
            remoteCall.setForwardUrl(result.getMockReturnValue());
            return remoteCall;
        }).collect(Collectors.toList()));
        // 老版黑名单
        callVO.setBLists(getBlackList());
        // 新版黑名单
        callVO.setNewBlists(getNewBlackList(appName));
        return callVO;
    }

    private List<Blacklist> getBlackList() {
        List<TBList> tbLists = tbListMntDao.getAllEnabledBlockList();
        if (CollectionUtils.isEmpty(tbLists)) {
            return Lists.newArrayList();
        }
        return tbLists.stream().map(tbList -> {
            Blacklist blacklist = new Blacklist();
            blacklist.setREDIS_KEY(tbList.getRedisKey());
            return blacklist;
        }).collect(Collectors.toList());
    }

    private List<AgentBlacklistVO> getNewBlackList(String appName) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationByCustomerIdAndName(appName);
        if (detailResult == null) {
            return Lists.newArrayList();
        }
        List<BlacklistResult> results = blackListDAO.getAllEnabledBlockList(detailResult.getApplicationId());
        AgentBlacklistVO vo = new AgentBlacklistVO();
        vo.setAppName(detailResult.getApplicationName());
        if (CollectionUtils.isNotEmpty(results)) {
            vo.setBlacklists(results.stream().map(BlacklistResult::getRedisKey).collect(Collectors.toList()));
        } else {
            vo.setBlacklists(Lists.newArrayList());
        }
        List<AgentBlacklistVO> vos = Lists.newArrayList();
        vos.add(vo);
        return vos;
    }

    @Override
    public void syncAmdb() {
        // 查询应用
        ApplicationQueryParam queryParam = new ApplicationQueryParam();
        List<ApplicationDetailResult> results = applicationDAO.getApplicationList(queryParam);
        if (CollectionUtils.isEmpty(results)) {
            return;
        }
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");

        AppRemoteCallQueryInput input = new AppRemoteCallQueryInput();

        int size = 50;
        // size个轮询一次
        if (results.size() > size) {
            int i = 1;
            boolean loop = true;
            do {
                List<ApplicationDetailResult> subList;
                //批量处理
                if (results.size() > i * size) {
                    subList = results.subList((i - 1) * size, i * size);
                } else {
                    subList = results.subList((i - 1) * size, results.size());
                    loop = false;
                }
                i++;
                saveRemoteCall(subList, voList,input);
            } while (loop);
        } else {
            saveRemoteCall(results, voList,input);
        }
    }

    @Override
    public void syncAmdb(Long tenantId, String userAppKey, String envCode) {
        // 查询应用
        ApplicationQueryParam queryParam = new ApplicationQueryParam();
        queryParam.setTenantId(tenantId);
        queryParam.setEnvCode(envCode);
        List<ApplicationDetailResult> results = applicationDAO.getApplicationList(queryParam);
        if (CollectionUtils.isEmpty(results)) {
            return;
        }
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE",tenantId,envCode);

        //租户传参
        AppRemoteCallQueryInput input = new AppRemoteCallQueryInput();
        input.setUserAppKey(userAppKey);
        input.setTenantId(tenantId);
        input.setEnvCode(envCode);

        int size = 50;
        // size个轮询一次
        if (results.size() > size) {
            int i = 1;
            boolean loop = true;
            do {
                List<ApplicationDetailResult> subList;
                //批量处理
                if (results.size() > i * size) {
                    subList = results.subList((i - 1) * size, i * size);
                } else {
                    subList = results.subList((i - 1) * size, results.size());
                    loop = false;
                }
                i++;
                saveRemoteCall(subList, voList,input);
            } while (loop);
        } else {
            saveRemoteCall(results, voList,input);
        }

    }

    private void saveRemoteCall(List<ApplicationDetailResult> apps, List<TDictionaryVo> voList, AppRemoteCallQueryInput input) {
        input.setStatus(0);
        input.setAppNames(apps.stream().map(ApplicationDetailResult::getApplicationName).collect(Collectors.toList()));
        // 获取所有未存入数据的数据
        input.setIsAll(true);

        PagingList<AppRemoteCallListVO> amdbPagingList = getAmdbPagingList(input, null, voList);
        if (amdbPagingList.isEmpty()) {
            return;
        }
        Map<String, List<ApplicationDetailResult>> appMap = apps.stream().collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationName));

        Map<String, List<ApplicationRemoteCallDTO>> serverAppNamesMap =
            getServerAppListMap(amdbPagingList.getList().stream().map(AppRemoteCallListVO::getAppName).collect(Collectors.joining(",")));
        List<AppRemoteCallCreateParam> params = amdbPagingList.getList().stream().map(vo -> {
            AppRemoteCallCreateParam param = new AppRemoteCallCreateParam();
            BeanUtils.copyProperties(vo, param);
            List<ApplicationDetailResult> results = appMap.get(vo.getAppName());
            if (CollectionUtils.isNotEmpty(results)) {
                // todo 存在多个
                ApplicationDetailResult result = results.get(0);
                param.setApplicationId(result.getApplicationId());
                param.setTenantId(result.getCustomerId());
                param.setAppName(result.getApplicationName());
                param.setUserId(result.getUserId());
                // 补充服务应用
                String appNameRemoteCallId = RemoteCallUtils.buildRemoteCallName(result.getApplicationName(), vo.getInterfaceName(),
                    vo.getInterfaceType());
                List<ApplicationRemoteCallDTO> callDtoList = serverAppNamesMap.get(appNameRemoteCallId);
                if (CollectionUtils.isNotEmpty(callDtoList)) {
                    param.setServerAppName(callDtoList.stream().map(ApplicationRemoteCallDTO::getAppName).collect(Collectors.joining(",")));
                }
                autoJoinWhite(param);
            }
            return param;
        }).collect(Collectors.toList());
        appRemoteCallDAO.batchInsert(params);
    }

    @Override
    public Map<String, List<ApplicationRemoteCallDTO>> getServerAppListMap(String upNames) {
        // 所有服务端应用
        ApplicationRemoteCallQueryDTO serverAppNameQuery = new ApplicationRemoteCallQueryDTO();
        serverAppNameQuery.setUpAppName(upNames);
        serverAppNameQuery.setQueryTye("0");
        serverAppNameQuery.setQueryTye("0");
        PagingList<ApplicationRemoteCallDTO> serverAppNamePage = applicationClient.listApplicationRemoteCalls(serverAppNameQuery);
        List<ApplicationRemoteCallDTO> serverAppNames = serverAppNamePage.getList();
        Map<String, List<ApplicationRemoteCallDTO>> serverAppNamesMap = serverAppNames.stream()
            .collect(Collectors.groupingBy(call -> RemoteCallUtils.buildRemoteCallName(call.getUpAppName(),
                RemoteCallUtils.getInterfaceNameByRpcName(call.getMiddlewareName(), call.getServiceName(), call.getMethodName()),
                call.getRpcType())));
        return serverAppNamesMap;
    }

    // 自动加入白名单 操作
    public void autoJoinWhite(AppRemoteCallCreateParam param) {
        param.setType(AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType());
        if (autoJoinWhiteFlag) {
            if (StringUtils.isNotBlank(param.getServerAppName())) {
                param.setType(AppRemoteCallConfigEnum.OPEN_WHITELIST.getType());
            }
        }
    }

    @Override
    public void deleteByApplicationIds(List<Long> applicationIds) {
        appRemoteCallDAO.deleteByApplicationIds(applicationIds);
    }

    private List<AppRemoteCallResult> queryAsyncIfNecessary(AppRemoteCallQueryParam param) {
        Long recordCount = appRemoteCallDAO.getRecordCount(param);
        if (recordCount > criticaValue) {
            return this.queryAsync(param, recordCount);
        }
        return appRemoteCallDAO.getList(param);
    }

    private List<AppRemoteCallResult> queryAsync(AppRemoteCallQueryParam param, Long totalCount) {
        //初始化
        List<AppRemoteCallResult> resultList = Lists.newArrayList();
        //计算实际需开启的线程数
        Long threadRealNum = totalCount / criticaValue + (totalCount % criticaValue > 0 ? 1 : 0);
        //初始化
        List<Future<List<AppRemoteCallResult>>> futureList = Lists.newArrayListWithCapacity(threadRealNum.intValue());
        //线程执行
        for (Long index = 0L; index < threadRealNum; index++) {
            Long startPageIndex = index * criticaValue + 1;
            Future<List<AppRemoteCallResult>> future = queryAsyncThreadPool.submit(() -> appRemoteCallDAO.getPartRecord(param, startPageIndex, criticaValue));
            futureList.add(future);
        }

        for (Future<List<AppRemoteCallResult>> future : futureList) {
            try {
                resultList.addAll(future.get());
            } catch (InterruptedException e) {
                log.error("线程池【queryAsyncThreadPool】出现中断异常",e);
            } catch (ExecutionException e) {
                log.error("线程池【queryAsyncThreadPool】出现未知异常",e);
            }
        }

        return resultList;
    }

}
