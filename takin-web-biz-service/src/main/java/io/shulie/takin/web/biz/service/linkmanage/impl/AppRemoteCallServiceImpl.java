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

package io.shulie.takin.web.biz.service.linkmanage.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.dao.confcenter.TBListMntDao;
import com.pamirs.takin.entity.domain.entity.TBList;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationRemoteCallQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.AppRemoteCallOutput;
import io.shulie.takin.web.biz.pojo.output.application.AppRemoteCallOutputV2;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallBatchUpdateV2Request;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallConfigRequest;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallCreateV2Request;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallUpdateV2Request;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import io.shulie.takin.web.common.vo.agent.AgentBlacklistVO;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO.Blacklist;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO.RemoteCall;
import io.shulie.takin.web.common.vo.application.AppRemoteCallListVO;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.InterfaceTypeChildDAO;
import io.shulie.takin.web.data.dao.application.InterfaceTypeConfigDAO;
import io.shulie.takin.web.data.dao.application.InterfaceTypeMainDAO;
import io.shulie.takin.web.data.dao.application.RemoteCallConfigDAO;
import io.shulie.takin.web.data.dao.blacklist.BlackListDAO;
import io.shulie.takin.web.data.dao.dictionary.DictionaryDataDAO;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeChildEntity;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeMainEntity;
import io.shulie.takin.web.data.model.mysql.RemoteCallConfigEntity;
import io.shulie.takin.web.data.param.application.AppRemoteCallCreateParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.blacklist.BlacklistResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author ??????
 * @date 2021/5/29 12:31 ??????
 */
@Slf4j
@Service
public class AppRemoteCallServiceImpl implements AppRemoteCallService {
    @Resource
    private AppRemoteCallDAO appRemoteCallDAO;
    @Resource
    private ApplicationDAO applicationDAO;
    @Resource
    private ApplicationClient applicationClient;

    @Resource
    private DictionaryDataDAO dictionaryDataDAO;

    @Resource
    private BaseConfigService baseConfigService;

    @Resource
    private TBListMntDao tbListMntDao;

    @Resource
    private BlackListDAO blackListDAO;

    @Resource
    private AgentConfigCacheManager agentConfigCacheManager;

    private Integer criticaValue;

    @Resource
    private ThreadPoolExecutor queryAsyncThreadPool;

    @PostConstruct
    public void init() {
        criticaValue = ConfigServerHelper.getWrapperIntegerValueByKey(
            ConfigServerKeyEnum.TAKIN_QUERY_ASYNC_CRITICA_VALUE);
    }

    @Resource
    private InterfaceTypeMainDAO interfaceTypeMainDAO;

    @Resource
    private InterfaceTypeChildDAO interfaceTypeChildDAO;

    @Resource
    private InterfaceTypeConfigDAO interfaceTypeConfigDAO;

    @Resource
    private RemoteCallConfigDAO remoteCallConfigDAO;

    private void checkInputData(AppRemoteCallUpdateInput input) {
        //if (input.getType().equals(AppRemoteCallConfigEnum.RETURN_MOCK.getType()) || input.getType().equals(
        //    AppRemoteCallConfigEnum.FORWARD_MOCK.getType())) {
        //    if (StringUtils.isBlank(input.getMockReturnValue())) {
        //        throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "mock????????????mock??????");
        //    }
        //}

        //if (input.getId() != null && input.getId() != 0) {
        //    if (RemoteCallUtils.checkWhite(input.getInterfaceType(),input.getType()) && StringUtils.isBlank(input.getServerAppName())) {
        //        throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "??????????????????????????????????????????????????????,?????????~");
        //    }
        //}
        //
        //if (RemoteCallUtils.checkWhite(input.getInterfaceType(),input.getType())) {
        //    // ????????????????????????????????????
        //    if (StringUtils.isBlank(input.getServerAppName())) {
        //        throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "???????????????????????????????????????");
        //    }
        //}

    }

    @Override
    public void update(AppRemoteCallUpdateInput input) {
        checkInputData(input);
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(input.getApplicationId());
        if (detailResult == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "???????????????");
        }
        // ??????????????????
        WebPluginUtils.fillUserData(input);
        if (input.getId() != null && input.getId() != 0) {
            OperationLogContextHolder.operationType(OpTypes.UPDATE);
            getCallResult(input.getId());
            AppRemoteCallUpdateParam param = new AppRemoteCallUpdateParam();
            BeanUtils.copyProperties(input, param);

            param.setTenantId(detailResult.getTenantId());
            param.setAppName(detailResult.getApplicationName());
            appRemoteCallDAO.update(param);
        } else {
            OperationLogContextHolder.operationType(OpTypes.CREATE);
            AppRemoteCallCreateParam param = new AppRemoteCallCreateParam();
            BeanUtils.copyProperties(input, param);
            param.setTenantId(detailResult.getTenantId());
            param.setAppName(detailResult.getApplicationName());
            appRemoteCallDAO.insert(param);
        }

        agentConfigCacheManager.evictRecallCalls(detailResult.getApplicationName());

    }

    @Override
    public void batchConfig(AppRemoteCallConfigRequest request) {
        if (WebPluginUtils.validateAdmin()) {
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
        RemoteCallConfigEntity configEntity = remoteCallConfigDAO.selectByOrder(output.getType());
        output.setTypeSelectVO(new SelectVO(configEntity.getName(), String.valueOf(configEntity.getValueOrder())));
        // ????????????
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        output.setInterfaceTypeSelectVO(getSelectVO(output.getInterfaceType(), voList));
        return output;
    }

    private SelectVO getSelectVO(Integer interfaceType, List<TDictionaryVo> voList) {
        InterfaceTypeMainEntity mainEntity = interfaceTypeMainDAO.selectByOrder(interfaceType);
        if (mainEntity == null) {
            String type = String.valueOf(interfaceType);
            if (CollectionUtils.isEmpty(voList)) {
                return new SelectVO("???????????????????????????", type);
            }
            List<TDictionaryVo> dictionaryVoList = voList.stream().filter(t -> type.equals(t.getValueCode())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(dictionaryVoList)) {
                return new SelectVO("???????????????????????????", type);
            }
            TDictionaryVo vos = dictionaryVoList.get(0);
            return new SelectVO(vos.getValueName(), type);
        } else {
            return new SelectVO(mainEntity.getName(), String.valueOf(mainEntity.getValueOrder()));
        }
    }

    private AppRemoteCallResult getCallResult(Long id) {
        AppRemoteCallResult result = appRemoteCallDAO.getResultById(id);
        if (result == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_GET_ERROR, "??????????????????????????????");
        }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        AppRemoteCallResult result = getCallResult(id);
        OperationLogContextHolder.addVars(Vars.INTERFACE, result.getInterfaceName());
        appRemoteCallDAO.deleteById(id);
        agentConfigCacheManager.evictRecallCalls(result.getAppName());
    }

    @Override
    public PagingList<AppRemoteCallListVO> pagingList(AppRemoteCallQueryInput input) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(input.getApplicationId());
        if (detailResult == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_GET_ERROR, "???????????????");
        }
        // ?????????????????????
        // ????????????????????????????????????????????????
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        // ???mysql????????????
        PagingList<AppRemoteCallListVO> dbPagingList = this.getDbPagingList(input, detailResult);
        // ???????????????????????????
        input.setTenantId(detailResult.getTenantId());
        input.setEnvCode(detailResult.getEnvCode());
        // ?????? ?????????????????????
        if (dbPagingList.getList().size() < input.getPageSize()) {
            // ?????? amdb???????????? ????????????
            int current = input.getCurrent() - (int)(dbPagingList.getTotal() / input.getPageSize()) - 1;
            if (current <= 0) {
                // ??????????????????????????? ?????????
                current = 1;
            }
            input.setCurrent(current);
        }
        PagingList<AppRemoteCallListVO> amdbPagingList = this.getAmdbPagingList(input, detailResult, voList);
        List<AppRemoteCallListVO> results = Lists.newArrayList();
        if ((long)(input.getPageSize() * (input.getCurrent() + 1)) >= dbPagingList.getTotal()
            || dbPagingList.getList().size() < input.getPageSize()) {
            // ?????? ???????????????
            results.addAll(dbPagingList.getList());
            results.addAll(amdbPagingList.getList());
        } else {
            results.addAll(dbPagingList.getList());
        }
        // ?????????????????????
        Map<String, List<ApplicationRemoteCallDTO>> serverAppNamesMap = this.getServerAppListMap(detailResult.getApplicationName());
        Map<Integer, RemoteCallConfigEntity> entityMap = remoteCallConfigDAO.selectToMapWithOrderKey();
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
            // ????????????????????????????????????
            result.setIsException(false);
            if (RemoteCallUtils.checkWhite(result.getType()) && CollectionUtils.isEmpty(result.getServerAppNames())) {
                result.setIsException(true);
                result.setSort(0);
            }
            RemoteCallConfigEntity entity = entityMap.get(result.getType());
            result.setTypeSelectVO(new SelectVO(entity.getName(), String.valueOf(entity.getValueOrder())));
            // ????????????
            result.setInterfaceTypeSelectVO(getSelectVO(result.getInterfaceType(), voList));
            // ????????????
            WebPluginUtils.fillQueryResponse(result);
        });
        return PagingList.of(results.stream().sorted(Comparator.comparing(AppRemoteCallListVO::getSort)).collect(Collectors.toList()),
            dbPagingList.getTotal() + amdbPagingList.getTotal());
    }

    private String getMiddlewareName(List<TDictionaryVo> voList) {
        Set<String> middlewareNames = Sets.newSet();
        List<InterfaceTypeMainEntity> mainList = interfaceTypeMainDAO.selectList();
        mainList.forEach(main -> {
            middlewareNames.add(main.getEngName().toUpperCase());
        });
        voList.forEach(vo -> {
            middlewareNames.add(vo.getValueName().toUpperCase());
        });
        return String.join(",", middlewareNames);
    }

    private Integer getInterfaceType(String methodName, List<TDictionaryVo> voList) {
        InterfaceTypeMainEntity mainEntity = interfaceTypeMainDAO.selectByName(methodName);
        if (mainEntity == null) {
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

        return mainEntity.getValueOrder();

    }

    private PagingList<AppRemoteCallListVO> getDbPagingList(AppRemoteCallQueryInput input, ApplicationDetailResult detailResult) {
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        BeanUtils.copyProperties(input, param);
        // ????????????????????????
        if (!WebPluginUtils.validateAdmin()) {
            if (detailResult != null) {
                param.setTenantId(detailResult.getTenantId());
            }
        }
        PagingList<AppRemoteCallResult> pagingList = appRemoteCallDAO.pagingList(param);
        if (CollectionUtils.isEmpty(pagingList.getList())) {
            return PagingList.of(Lists.newArrayList(), pagingList.getTotal());
        }
        Map<Integer, InterfaceTypeMainEntity> entityMap = interfaceTypeMainDAO.selectToMapWithOrderKey();
        List<AppRemoteCallListVO> appRemoteCallVoList = pagingList.getList().stream().map(result -> {
            AppRemoteCallListVO listVO = new AppRemoteCallListVO();
            BeanUtils.copyProperties(result, listVO);
            listVO.setSort(1);
            listVO.setMockValue(result.getMockReturnValue());
            InterfaceTypeMainEntity mainEntity = entityMap.get(result.getInterfaceType());
            listVO.setInterfaceChildType(StringUtils.defaultIfBlank(result.getInterfaceChildType(), mainEntity.getEngName()));
            //????????????????????????convert??????????????????rpcType
            listVO.setInterfaceType(mainEntity.getRpcType());
            // ??????????????????
            listVO.setIsManual(result.getIsManual());
            // ??????????????????
            listVO.setUserId(detailResult.getUserId());
            // ???????????? ??????????????????????????????????????????
            fillInPermissions(listVO, detailResult);
            return listVO;
        }).collect(Collectors.toList());
        return PagingList.of(appRemoteCallVoList, pagingList.getTotal());
    }

    /**
     * ???????????? ????????????????????????
     */
    private void fillInPermissions(AppRemoteCallListVO listVO, ApplicationDetailResult detailResult) {
        List<Long> allowUpdateUserIdList = WebPluginUtils.getUpdateAllowUserIdList();
        if (CollectionUtils.isEmpty(allowUpdateUserIdList)) {
            //?????????
            listVO.setCanEdit(true);
        } else {
            //????????????
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
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(applicationId);
        if (detailResult == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_GET_ERROR, "???????????????");
        }
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        param.setApplicationId(applicationId);
        List<AppRemoteCallResult> results = appRemoteCallDAO.getList(param);
        // ???????????????????????????
        Map<String, List<ApplicationRemoteCallDTO>> serverAppNamesMap = this.getServerAppListMap(detailResult.getApplicationName());
        return String.valueOf(results.stream().filter(result -> RemoteCallUtils.checkWhite(result.getType())
            && this.checkServerAppName(detailResult,serverAppNamesMap,result)).count());
    }

    /**
     * ?????????????????????
     * @param serverAppNamesMap
     * @param result
     * @return
     */
    private boolean checkServerAppName(ApplicationDetailResult detailResult,Map<String, List<ApplicationRemoteCallDTO>> serverAppNamesMap, AppRemoteCallResult result) {
        // ????????? amdb
        InterfaceTypeMainEntity mainEntity = interfaceTypeMainDAO.selectByOrder(result.getInterfaceType());
        String appNameRemoteCallId = RemoteCallUtils.buildRemoteCallName(detailResult.getApplicationName(), result.getInterfaceName(), mainEntity.getRpcType());
        List<ApplicationRemoteCallDTO> callDtoList = serverAppNamesMap.get(appNameRemoteCallId);
        if(CollectionUtils.isNotEmpty(callDtoList)) {
            return false;
        }
        return true;
    }

    @Override
    public List<SelectVO> getConfigSelect(Integer interfaceType, String serverAppName) {
        // ????????????????????????????????????????????????
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        List<RemoteCallConfigEntity> entities = remoteCallConfigDAO.selectList();
        List<SelectVO> vos = entities.stream()
            .map(t -> new SelectVO(t.getName(), String.valueOf(t.getValueOrder()))).collect(Collectors.toList());

        TBaseConfig tBaseConfig = baseConfigService.selectByPrimaryKey("REMOTE_CALL_ABLE_CONFIG");
        Map<Integer, RemoteCallConfigEntity> entityMap = remoteCallConfigDAO.selectToMapWithOrderKey();
        if (tBaseConfig != null) {
            JSONObject jsonObject = JSONUtil.toBean(tBaseConfig.getConfigValue(), JSONObject.class);
            SelectVO selectVO = getSelectVO(interfaceType, voList);
            jsonObject.keySet().forEach(key -> {
                String value = jsonObject.getString(key);
                if (!value.contains(selectVO.getLabel())) {
                    RemoteCallConfigEntity entity = entityMap.get(Integer.valueOf(key));
                    for (SelectVO vo : vos) {
                        if (vo.getValue().equals(String.valueOf(entity.getValueOrder()))) {
                            vo.setDisable(false);
                            break;
                        }
                    }
                }
            });
        }
        // ??????????????? ????????????????????????FEIGN ??????
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
        Map<Integer, RemoteCallConfigEntity> entityMap = remoteCallConfigDAO.selectToMapWithOrderKey();
        callVO.setWLists(results.stream().map(result -> {
            RemoteCall remoteCall = new RemoteCall();
            remoteCall.setINTERFACE_NAME(result.getInterfaceName());
            remoteCall.setTYPE(getSelectVO(result.getInterfaceType(), voList).getLabel().toLowerCase());
            remoteCall.setCheckType(entityMap.get(result.getType()).getCheckType());
            remoteCall.setContent(result.getMockReturnValue());
            remoteCall.setForwardUrl(result.getMockReturnValue());
            return remoteCall;
        }).collect(Collectors.toList()));
        // ???????????????
        callVO.setBLists(getBlackList());
        // ???????????????
        callVO.setNewBlists(getNewBlackList(appName));
        return callVO;
    }

    private List<Blacklist> getBlackList() {
        TenantCommonExt commonExt = WebPluginUtils.traceTenantCommonExt();
        List<TBList> tbLists = tbListMntDao.getAllEnabledBlockList(commonExt.getTenantId(), commonExt.getEnvCode());
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
        ApplicationDetailResult detailResult = applicationDAO.getApplicationByTenantIdAndName(appName);
        if (detailResult == null) {
            return Lists.newArrayList();
        }
        List<BlacklistResult> results = blackListDAO.getAllEnabledBlockList(detailResult.getApplicationId(), WebPluginUtils.traceTenantCommonExt());
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
        // ????????????
        ApplicationQueryParam queryParam = new ApplicationQueryParam();
        List<ApplicationDetailResult> results = applicationDAO.getApplicationList(queryParam);
        if (CollectionUtils.isEmpty(results)) {
            return;
        }
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");

        int size = 50;
        // size???????????????
        if (results.size() > size) {
            int i = 1;
            boolean loop = true;
            do {
                List<ApplicationDetailResult> subList;
                //????????????
                if (results.size() > i * size) {
                    subList = results.subList((i - 1) * size, i * size);
                } else {
                    subList = results.subList((i - 1) * size, results.size());
                    loop = false;
                }
                i++;
                this.saveRemoteCall(subList, voList);
            } while (loop);
        } else {
            this.saveRemoteCall(results, voList);
        }
    }

    @Override
    public Map<String, List<ApplicationRemoteCallDTO>> getServerAppListMap(String upNames) {
        // ?????????????????????
        ApplicationRemoteCallQueryDTO serverAppNameQuery = new ApplicationRemoteCallQueryDTO();
        serverAppNameQuery.setUpAppName(upNames);
        serverAppNameQuery.setQueryTye("0");
        PagingList<ApplicationRemoteCallDTO> serverAppNamePage = applicationClient.listApplicationRemoteCalls(serverAppNameQuery);
        List<ApplicationRemoteCallDTO> serverAppNames = serverAppNamePage.getList();
        // ????????????
        return serverAppNames.stream().collect(Collectors.groupingBy(call -> RemoteCallUtils.buildRemoteCallName(call.getUpAppName(),
            RemoteCallUtils.getInterfaceNameByRpcName(call.getMiddlewareName(), call.getServiceName(), call.getMethodName()),
            call.getRpcType())));
    }

    /**
     * ????????????????????? ??????
     *
     * @param param ??????
     */
    private void autoJoinWhite(AppRemoteCallCreateParam param) {
        param.setType(AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType());
        if (ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REMOTE_CALL_AUTO_JOIN_WHITE)
            && StringUtils.isNotBlank(param.getServerAppName())) {
            param.setType(AppRemoteCallConfigEnum.OPEN_WHITELIST.getType());
        }
    }

    @Override
    public void deleteByApplicationIds(List<Long> applicationIds) {
        appRemoteCallDAO.deleteByApplicationIds(applicationIds);
    }

    /**
     * ??????md5???
     * @param param
     * @return
     */
    private List<String> queryRemoteAppMd5(AppRemoteCallQueryParam param) {
        List<Long> applicationIds = param.getApplicationIds();
        // size???????????????
        int size = 10;
        if (Objects.nonNull(applicationIds) && applicationIds.size() > size) {
            List<String> list = new ArrayList<>();
            List<List<Long>> splitList = ListUtil.split(applicationIds, size);
            splitList.forEach(x ->{
                param.setApplicationIds(x);
                List<String> returns = appRemoteCallDAO.getRemoteCallMd5(param);
                list.addAll(returns);
            });
            return list;
        } else {
            return appRemoteCallDAO.getRemoteCallMd5(param);
        }
    }

    private List<AppRemoteCallResult> queryAsyncIfNecessary(AppRemoteCallQueryParam param) {
        Long recordCount = appRemoteCallDAO.getRecordCount(param);
        if (recordCount > criticaValue) {
            return this.queryAsync(param, recordCount);
        }
        return appRemoteCallDAO.getList(param);
    }

    private List<AppRemoteCallResult> queryAsync(AppRemoteCallQueryParam param, Long totalCount) {
        //?????????
        List<AppRemoteCallResult> resultList = Lists.newArrayList();
        //?????????????????????????????????
        long threadRealNum = totalCount / criticaValue + (totalCount % criticaValue > 0 ? 1 : 0);
        //?????????
        List<Future<List<AppRemoteCallResult>>> futureList = Lists.newArrayListWithCapacity((int)threadRealNum);
        //????????????
        for (long index = 0L; index < threadRealNum; index++) {
            long startPageIndex = index * criticaValue + 1;
            Future<List<AppRemoteCallResult>> future = queryAsyncThreadPool.submit(() -> appRemoteCallDAO.getPartRecord(param, startPageIndex, criticaValue));
            futureList.add(future);
        }

        for (Future<List<AppRemoteCallResult>> future : futureList) {
            try {
                resultList.addAll(future.get());
            } catch (InterruptedException e) {
                log.error("????????????queryAsyncThreadPool?????????????????????", e);
            } catch (ExecutionException e) {
                log.error("????????????queryAsyncThreadPool?????????????????????", e);
            }
        }

        return resultList;
    }

    /**
     * ??????amdb??????
     *
     * @param input
     * @param voList
     * @return
     */
    private List<AppRemoteCallListVO> collectAmdbRemoteCall(AppRemoteCallQueryInput input, List<TDictionaryVo> voList) {
        ApplicationRemoteCallQueryDTO query = new ApplicationRemoteCallQueryDTO();
        query.setMiddlewareName(getMiddlewareName(voList));
        if (CollectionUtils.isNotEmpty(input.getAppNames())) {
            query.setAppName(String.join(",", input.getAppNames()));
        }
        // ??????
        query.setQueryTye("1");
        // ?????????????????????
        PagingList<ApplicationRemoteCallDTO> calls = applicationClient.listApplicationRemoteCalls(query);
        if (calls.isEmpty()) {
            return Lists.newArrayList();
        }
        Map<String, InterfaceTypeChildEntity> childEntityMap = interfaceTypeChildDAO.selectToMapWithNameKey();
        return calls.getList().stream().map(call -> {
            AppRemoteCallListVO listVO = new AppRemoteCallListVO();
            listVO.setInterfaceName(RemoteCallUtils.getInterfaceNameByRpcName(call.getMiddlewareName(), call.getServiceName(), call.getMethodName()));
            // ????????????
            listVO.setInterfaceType(getInterfaceType(call.getMiddlewareName(), voList));
            listVO.setType(AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType());
            listVO.setAppName(call.getAppName());
            listVO.setIsManual(false);
            listVO.setDefaultWhiteInfo(StringUtils.defaultIfBlank(call.getDefaultWhiteInfo(), ""));
            if (!childEntityMap.containsKey(call.getMiddlewareDetail())) {
                listVO.setInterfaceChildType(call.getMiddlewareName());
            } else {
                listVO.setInterfaceChildType(call.getMiddlewareDetail());
            }
            // ???md5
            listVO.setMd5(RemoteCallUtils.buildRemoteCallName(listVO.getAppName(),listVO.getInterfaceName(),listVO.getInterfaceType()));
            return listVO;
        }).collect(Collectors.toList());
    }

    private void saveRemoteCall(List<ApplicationDetailResult> apps, List<TDictionaryVo> voList) {
        AppRemoteCallQueryInput input = new AppRemoteCallQueryInput();
        input.setStatus(0);
        input.setAppNames(apps.stream().map(ApplicationDetailResult::getApplicationName).collect(Collectors.toList()));
        List<AppRemoteCallListVO> amdbRemoteCallData = this.collectAmdbRemoteCall(input, voList);
        if (CollectionUtils.isEmpty(amdbRemoteCallData)) {
            return;
        }
        // ???????????????????????? ??????
        AppRemoteCallQueryParam queryParam = new AppRemoteCallQueryParam();
        queryParam.setApplicationIds(apps.stream().map(ApplicationDetailResult::getApplicationId).collect(Collectors.toList()));
        List<String> appNameRemoteCallMd5s = this.queryRemoteAppMd5(queryParam);

        Map<String, List<ApplicationDetailResult>> appMap = apps.stream().collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationName));

        Map<String, List<ApplicationRemoteCallDTO>> serverAppNamesMap =
            getServerAppListMap(amdbRemoteCallData.stream().map(AppRemoteCallListVO::getAppName).collect(Collectors.joining(",")));

        List<AppRemoteCallCreateParam> params = Lists.newArrayList();

        for (AppRemoteCallListVO vo : amdbRemoteCallData) {
            AppRemoteCallCreateParam param = new AppRemoteCallCreateParam();
            BeanUtils.copyProperties(vo, param);
            List<ApplicationDetailResult> results = appMap.get(vo.getAppName());
            if (CollectionUtils.isNotEmpty(results)) {
                ApplicationDetailResult result = results.get(0);
                param.setApplicationId(result.getApplicationId());
                param.setTenantId(result.getTenantId());
                param.setAppName(result.getApplicationName());
                param.setUserId(result.getUserId());
                param.setMd5(vo.getMd5());
                // ??????????????????
                if(appNameRemoteCallMd5s.contains(param.getMd5())) {
                    continue;
                }
                List<ApplicationRemoteCallDTO> callDTOS = serverAppNamesMap.get(param.getMd5());
                if (CollectionUtils.isNotEmpty(callDTOS)) {
                    param.setServerAppName(callDTOS.stream().map(ApplicationRemoteCallDTO::getAppName).collect(Collectors.joining(",")));
                }
                autoJoinWhite(param);
                params.add(param);
            }
        }
        appRemoteCallDAO.batchInsert(params);
        // ????????????
        List<String> appNames = params.stream().filter(t -> !AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType().equals(t.getType()))
            .map(AppRemoteCallCreateParam::getAppName).distinct().collect(Collectors.toList());
        appNames.forEach(appName ->  agentConfigCacheManager.evictRecallCalls(appName));
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
        // ??????
        query.setQueryTye("1");

        // ????????????
        query.setTenantAppKey(input.getTenantAppKey());
        query.setEnvCode(input.getEnvCode());

        // ?????????????????????
        PagingList<ApplicationRemoteCallDTO> calls = applicationClient.listApplicationRemoteCalls(query);
        if (calls.isEmpty()) {
            return PagingList.empty();
        }
        // ???????????????????????? ??????
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        if (input.getTenantId() != null) {
            param.setTenantId(input.getTenantId());
        }
        if (StringUtils.isNotBlank(input.getEnvCode())) {
            param.setEnvCode(input.getEnvCode());
        }
        if (detailResult != null) {
            param.setApplicationId(detailResult.getApplicationId());
        }
        // ????????????
        WebPluginUtils.transferTenantParam(input, param);

        List<String> appNameRemoteCallMd5s = this.queryRemoteAppMd5(param);
        Map<String, InterfaceTypeChildEntity> childEntityMap = interfaceTypeChildDAO.selectToMapWithNameKey();
        List<AppRemoteCallListVO> outputs = calls.getList().stream()
            .filter(call -> {
                String appNameRemoteCallMd5 = RemoteCallUtils.buildRemoteCallName(call.getAppName(),
                    RemoteCallUtils.getInterfaceNameByRpcName(call.getMiddlewareName(), call.getServiceName(), call.getMethodName()),
                    getInterfaceType(call.getMiddlewareName(), voList));
                // ???AMDB??????????????????????????????????????????????????????????????????--20210303 CYF
                return (appNameRemoteCallMd5s.stream().noneMatch(e -> e.equals(appNameRemoteCallMd5))) && (!"-1".equals(call.getRpcType()));
            })
            .map(call -> {
                AppRemoteCallListVO listVO = new AppRemoteCallListVO();
                listVO.setInterfaceName(
                    RemoteCallUtils.getInterfaceNameByRpcName(call.getMiddlewareName(), call.getServiceName(), call.getMethodName()));
                // ????????????
                listVO.setInterfaceType(getInterfaceType(call.getMiddlewareName(), voList));
                listVO.setType(AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType());
                listVO.setSort(2);
                listVO.setAppName(call.getAppName());
                listVO.setIsManual(false);
                listVO.setDefaultWhiteInfo(StringUtils.defaultIfBlank(call.getDefaultWhiteInfo(), ""));
                if (!childEntityMap.containsKey(call.getMiddlewareDetail())) {
                    listVO.setInterfaceChildType(call.getMiddlewareName());
                } else {
                    listVO.setInterfaceChildType(call.getMiddlewareDetail());
                }
                listVO.setMd5(RemoteCallUtils.buildRemoteCallName(listVO.getAppName(),listVO.getInterfaceName(),listVO.getInterfaceType()));
                // ????????????
                fillInPermissions(listVO, detailResult);
                return listVO;
            }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(outputs)) {
            return PagingList.empty();
        }
        //??????????????????
        return PagingList.of(outputs.size() < input.getPageSize() ?
                outputs : outputs.subList(input.getCurrent() * input.getPageSize(),
                ((input.getCurrent() + 1) * input.getPageSize() > outputs.size() ? (outputs.size() - 1) : ((input.getCurrent() + 1) * input.getPageSize()))),
            outputs.size());
    }

    @Override
    public List<SelectVO> getConfigSelectV2(String interfaceType) {
        // ???????????????????????????????????????
        InterfaceTypeChildEntity templateEntity = interfaceTypeChildDAO.selectByName(interfaceType);
        if (Objects.isNull(templateEntity)) {
            return Collections.emptyList();
        }
        return this.buildSelectVos(interfaceTypeConfigDAO.selectByTypeId(templateEntity.getId()));
    }

    private List<SelectVO> buildSelectVos(List<InterfaceTypeConfigEntity> configEntityList) {
        if (CollectionUtils.isEmpty(configEntityList)) {
            return Collections.emptyList();
        }
        // ?????????????????????????????????
        Map<Long, RemoteCallConfigEntity> configMap = remoteCallConfigDAO.selectToMap();
        return configEntityList.stream().map(config -> {
            RemoteCallConfigEntity configEntity = configMap.get(config.getConfigId());
            if (configEntity != null) {
                SelectVO vo = new SelectVO();
                vo.setLabel(configEntity.getName());
                vo.setValue(String.valueOf(configEntity.getValueOrder()));
                return vo;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public List<SelectVO> getInterfaceTypeSelect() {
        List<InterfaceTypeChildEntity> templateList = interfaceTypeChildDAO.selectList();
        if (CollectionUtils.isEmpty(templateList)) {
            return Collections.emptyList();
        }
        return templateList.stream().
            filter(templateDto -> !"HTTP".equals(templateDto.getEngName()))
            .map(templateDto -> {
                String engName = templateDto.getEngName();
                return new SelectVO(engName, engName);
            }).collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    @Override
    public Map<Long, List<AppRemoteCallResult>> getListGroupByAppId() {
        List<AppRemoteCallResult> allRecord = appRemoteCallDAO.getAllRecordByPage();
        return CollStreamUtil.groupByKey(allRecord, AppRemoteCallResult::getApplicationId);
    }

    /**
     * ??????id??????????????????
     *
     * @param ids
     */
    @Override
    public void batchLogicDelByIds(List<Long> ids) {
        if(CollectionUtils.isNotEmpty(ids)){
            appRemoteCallDAO.batchLogicDelByIds(ids);
        }
    }

    @Override
    public void batchSave(List<AppRemoteCallResult> appRemoteCallResults) {
        appRemoteCallDAO.batchSave(appRemoteCallResults);
    }

    @Override
    public void create(AppRemoteCallCreateV2Request request) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(request.getApplicationId());
        if (detailResult == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "???????????????");
        }
        this.checkRequest(request);
        // ??????????????????
        WebPluginUtils.fillUserData(request);
        Long mainTypeId = interfaceTypeChildDAO.selectByName(request.getInterfaceType()).getMainId();
        Integer mainTypeOrder = interfaceTypeMainDAO.selectById(mainTypeId).getValueOrder();
        AppRemoteCallResult result = appRemoteCallDAO.queryOne(detailResult.getApplicationName(), mainTypeOrder, request.getInterfaceName());
        if (Objects.nonNull(result)) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "????????????????????????");
        }

        AppRemoteCallCreateParam param = new AppRemoteCallCreateParam();
        BeanUtils.copyProperties(request, param);
        // ????????????
        WebPluginUtils.transferTenantParam(detailResult, param);

        param.setAppName(detailResult.getApplicationName());
        param.setInterfaceType(mainTypeOrder);
        param.setInterfaceChildType(request.getInterfaceType());
        param.setMockReturnValue(request.getMockValue());
        param.setRemark(request.getRemark());
        appRemoteCallDAO.insert(param);
        agentConfigCacheManager.evictRecallCalls(detailResult.getApplicationName());
    }

    private void checkRequest(AppRemoteCallCreateV2Request request) {
        // ?????????mock
        if(AppRemoteCallConfigEnum.RETURN_MOCK.getType().equals(request.getType()) && StringUtils.isBlank(request.getMockValue())) {
            // ????????????????????????
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "?????????mock????????????");
        }
        if(AppRemoteCallConfigEnum.FORWARD_MOCK.getType().equals(request.getType()) && StringUtils.isBlank(request.getMockValue())) {
            // ????????????????????????
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "??????mock????????????");
        }
    }

    @Override
    public void updateV2(AppRemoteCallUpdateV2Request request) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(request.getApplicationId());
        if (detailResult == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "???????????????");
        }
        this.checkRequest(request);
        agentConfigCacheManager.evictRecallCalls(detailResult.getApplicationName());
        if (Objects.isNull(request.getId())) {
            this.create(request);
            return;
        }
        // ??????????????????
        WebPluginUtils.fillUserData(request);
        getCallResult(request.getId());
        AppRemoteCallUpdateParam param = new AppRemoteCallUpdateParam();
        BeanUtils.copyProperties(request, param);
        Long mainId = interfaceTypeChildDAO.selectByName(request.getInterfaceType()).getMainId();
        InterfaceTypeMainEntity mainEntity = interfaceTypeMainDAO.selectById(mainId);

        WebPluginUtils.transferTenantParam(detailResult, param);

        param.setAppName(detailResult.getApplicationName());
        param.setInterfaceType(mainEntity.getValueOrder());
        param.setInterfaceChildType(request.getInterfaceType());
        param.setMockReturnValue(request.getMockValue());
//        param.setRemark(request.getRemark());
        appRemoteCallDAO.update(param);

    }

    @Override
    public AppRemoteCallOutputV2 getByIdV2(Long id) {
        AppRemoteCallResult result = getCallResult(id);
        AppRemoteCallOutputV2 output = new AppRemoteCallOutputV2();
        BeanUtils.copyProperties(result, output);
        output.setInterfaceType(result.getInterfaceChildType());
        output.setMockValue(result.getMockReturnValue());
        output.setRemark(result.getRemark());
        output.setApplicationId(String.valueOf(result.getApplicationId()));
        return output;
    }

    /**
     * ??????
     *
     * @param request
     */
    @Override
    public void batchUpdateV2(AppRemoteCallBatchUpdateV2Request request) {
        Integer updateType = request.getUpdateType();
        List<AppRemoteCallUpdateV2Request> updateInfos = request.getUpdateInfos();
        updateInfos.forEach(item ->{
            item.setType(updateType);
            this.updateV2(item);
        });

    }



}
