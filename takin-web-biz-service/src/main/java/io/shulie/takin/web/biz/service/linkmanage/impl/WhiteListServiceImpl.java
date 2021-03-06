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

package io.shulie.takin.web.biz.service.linkmanage.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.TakinDictTypeEnum;
import com.pamirs.takin.common.constant.YNEnum;
import com.pamirs.takin.entity.dao.dict.TDictionaryTypeMapper;
import com.pamirs.takin.entity.domain.dto.linkmanage.InterfaceVo;
import com.pamirs.takin.entity.domain.entity.TDictionaryType;
import com.pamirs.takin.entity.domain.entity.TWList;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListCreateListVO;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListOperateVO;
import com.pamirs.takin.entity.domain.query.whitelist.WhiteListQueryVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationInterfaceQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationInterfaceDTO;
import io.shulie.takin.web.amdb.enums.MiddlewareTypeGroupEnum;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistImportFromExcelInput;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistSearchInput;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistUpdatePartAppNameInput;
import io.shulie.takin.web.biz.pojo.request.whitelist.WhiteListDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.whitelist.WhiteListUpdateRequest;
import io.shulie.takin.web.biz.service.linkmanage.WhiteListService;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.whitelist.WhitelistTagEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.whitelist.WhitelistUtil;
import io.shulie.takin.web.common.vo.whitelist.WhiteListVO;
import io.shulie.takin.web.common.vo.whitelist.WhitelistPartVO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationWhiteListDAO;
import io.shulie.takin.web.data.dao.application.WhiteListDAO;
import io.shulie.takin.web.data.dao.application.WhitelistEffectiveAppDao;
import io.shulie.takin.web.data.mapper.mysql.WhiteListMapper;
import io.shulie.takin.web.data.model.mysql.WhiteListEntity;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationWhiteListCreateParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistAddPartAppNameParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistEffectiveAppDeleteParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistEffectiveAppSearchParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistGlobalOrPartParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSaveOrUpdateParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSearchParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistUpdatePartAppNameParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistEffectiveAppResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mubai
 * @date 2020-04-20 19:16
 */

@Service
@Slf4j
public class WhiteListServiceImpl implements WhiteListService {

    @Resource
    private WhiteListDAO whiteListDAO;
    @Resource
    private TDictionaryTypeMapper tDictionaryTypeMapper;

    @Autowired
    private ApplicationClient applicationClient;
    @Autowired
    private ConfigSyncService configSyncService;
    @Autowired
    private WhiteListFileService whiteListFileService;
    @Autowired
    private ApplicationWhiteListDAO applicationWhiteListDAO;
    @Autowired
    private ApplicationDAO applicationDAO;
    @Resource
    private WhiteListMapper whiteListMapper;
    @Autowired
    private WhitelistEffectiveAppDao whitelistEffectiveAppDao;


    /**
     * ?????????????????????????????????
     */
    private boolean isCheckDuplicateName;

    private Integer number;

    @PostConstruct
    public void init() {
        isCheckDuplicateName = ConfigServerHelper.getBooleanValueByKey(
            ConfigServerKeyEnum.TAKIN_WHITE_LIST_DUPLICATE_NAME_CHECK);
        number = ConfigServerHelper.getWrapperIntegerValueByKey(ConfigServerKeyEnum.TAKIN_WHITE_LIST_NUMBER_LIMIT);
    }

    private Integer getInterfaceIntType(String interfaceType) {
        int type = -1;
        String interfaceTypeString = String.valueOf(interfaceType);
        interfaceTypeString = MiddlewareTypeGroupEnum.getMiddlewareGroupType(interfaceTypeString).getType();
        switch (interfaceTypeString) {
            case "HTTP":
            case "1":
                type = 1;
                break;
            case "DUBBO":
            case "2":
                type = 2;
                break;
            case "UNKNOWN":
            case "-1":
                type = -1;
                break;
            default:
        }
        return type;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void importWhiteListFromExcel(List<WhitelistImportFromExcelInput> inputs) {
        if (CollectionUtils.isEmpty(inputs)) {
            return;
        }
        Long applicationId = inputs.get(0).getApplicationId();
        // ????????????
        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(applicationId);
        if (applicationDetailResult == null) {
            throw new TakinWebException(ExceptionCode.EXCEL_IMPORT_ERROR, "???????????????");
        }
        // takin ????????????
        List<WhitelistResult> whitelistResults = whiteListDAO.listByApplicationId(applicationId);
        Map<String, List<WhitelistResult>> whitelistResultsMap = whitelistResults.stream()
                .collect(Collectors.groupingBy(w -> WhitelistUtil.buildWhiteId(w.getType(), w.getInterfaceName())));
        // ???????????????
        List<String> armdString = whitelistResults.stream().map(WhitelistResult::getInterfaceName).collect(
                Collectors.toList());
        List<String> existWhite = getExistWhite(armdString, Lists.newArrayList());

        // ???????????? ?????? ??????
        List<WhitelistSaveOrUpdateParam> saveOrUpdateParams = Lists.newArrayList();
        inputs.forEach(input -> {
            String id = WhitelistUtil.buildWhiteId(input.getInterfaceType(), input.getInterfaceName());
            List<WhitelistResult> results = whitelistResultsMap.get(id);
            WhitelistSaveOrUpdateParam saveOrUpdateParam = new WhitelistSaveOrUpdateParam();
            if (CollectionUtils.isNotEmpty(results)) {
                WhitelistResult whitelistResult = results.get(0);
                BeanUtils.copyProperties(input, saveOrUpdateParam);
                saveOrUpdateParam.setWlistId(whitelistResult.getWlistId());
                saveOrUpdateParam.setGmtModified(new Date());
                saveOrUpdateParam.setType(String.valueOf(input.getInterfaceType()));
                saveOrUpdateParam.setUseYn(input.getUseYn());
                // ???????????????
                if (WhitelistUtil.isDuplicate(existWhite,
                        WhitelistUtil.buildWhiteId(saveOrUpdateParam.getType(), saveOrUpdateParam.getInterfaceName()))) {
                    saveOrUpdateParam.setIsGlobal(!isCheckDuplicateName);
                }
                saveOrUpdateParam.setIsHandwork(input.getIsHandwork());
            } else {
                BeanUtils.copyProperties(input, saveOrUpdateParam);
                saveOrUpdateParam.setType(String.valueOf(input.getInterfaceType()));
                saveOrUpdateParam.setUseYn(input.getUseYn());
                // ???????????????
                if (WhitelistUtil.isDuplicate(existWhite,
                        WhitelistUtil.buildWhiteId(saveOrUpdateParam.getType(), saveOrUpdateParam.getInterfaceName()))) {
                    saveOrUpdateParam.setIsGlobal(!isCheckDuplicateName);
                }
                saveOrUpdateParam.setIsHandwork(input.getIsHandwork());
            }
            // ????????????
            saveOrUpdateParam.setTenantId(applicationDetailResult.getTenantId());
            saveOrUpdateParam.setUserId(applicationDetailResult.getUserId());
            saveOrUpdateParams.add(saveOrUpdateParam);
        });
        whiteListDAO.batchSaveOrUpdate(saveOrUpdateParams);
        // ????????????
        List<WhitelistAddPartAppNameParam> addPartAppNameParams = Lists.newArrayList();
        List<WhitelistResult> againResult = whiteListDAO.listByApplicationId(applicationId);
        Map<String, List<WhitelistResult>> againResultsMap = againResult.stream()
                .collect(Collectors.groupingBy(w -> WhitelistUtil.buildWhiteId(w.getType(), w.getInterfaceName())));
        for (WhitelistImportFromExcelInput input : inputs) {
            if (CollectionUtils.isEmpty(input.getEffectAppNames())) {
                continue;
            }
            String id = WhitelistUtil.buildWhiteId(input.getInterfaceType(), input.getInterfaceName());
            List<WhitelistResult> listResult = againResultsMap.get(id);

            input.getEffectAppNames().forEach(appName -> {
                WhitelistAddPartAppNameParam addPartAppNameParam = new WhitelistAddPartAppNameParam();
                addPartAppNameParam.setInterfaceName(listResult.get(0).getInterfaceName());
                addPartAppNameParam.setType(listResult.get(0).getType());
                addPartAppNameParam.setEffectiveAppName(appName);
                addPartAppNameParam.setTenantId(applicationDetailResult.getTenantId());
                addPartAppNameParam.setUserId(applicationDetailResult.getUserId());
                addPartAppNameParam.setWlistId(listResult.get(0).getWlistId());
                addPartAppNameParams.add(addPartAppNameParam);
            });
        }
        // ????????????
        List<Long> wlistIds = addPartAppNameParams.stream().map(WhitelistAddPartAppNameParam::getWlistId).collect(
                Collectors.toList());
        WhitelistEffectiveAppDeleteParam deleteParam = new WhitelistEffectiveAppDeleteParam();
        deleteParam.setTenantId(applicationDetailResult.getTenantId());
        deleteParam.setWlistIds(wlistIds);
        whitelistEffectiveAppDao.batchDelete(deleteParam);
        // ??????
        whitelistEffectiveAppDao.addPartAppName(addPartAppNameParams);
        // ??????agent
        whiteListFileService.writeWhiteListFile();
        TenantCommonExt commonExt = WebPluginUtils.fillTenantCommonExt(applicationDetailResult.getTenantId(), applicationDetailResult.getEnvCode());
        configSyncService.syncAllowList(commonExt, applicationId, applicationDetailResult.getApplicationName());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveWhitelist(WhiteListCreateListVO vo) {
        Long applicationId = vo.getApplicationId();
        Integer interfaceType = vo.getInterfaceType();
        List<String> interfaceList = vo.getInterfaceList();
        // ???????????????
        for (String interfaceName : interfaceList) {
            if (WhitelistUtil.checkWhiteFormatError(interfaceName, number)) {
                throw new TakinWebException(ExceptionCode.WHITELIST_FORMAT_APP_ERROR, "??????????????????????????????????????????????????????");
            }
        }

        TDictionaryType tDictionaryType = tDictionaryTypeMapper.selectDictionaryByTypeAlias(
                TakinDictTypeEnum.WLIST.name());

        List<TWList> duplicateList = Lists.newArrayList();
        List<TWList> toAddList = Lists.newArrayList();
        List<TWList> toUpdateList = Lists.newArrayList();

        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(applicationId);
        //??????????????????????????????????????????????????????????????????????????????

        List<TWList> twLists = whiteListDAO.queryWhiteListTotalByApplicationId(applicationId);

        List<String> armdString = twLists.stream().map(TWList::getInterfaceName).collect(Collectors.toList());
        // ???????????????
        List<String> existWhite = this.getExistWhite(armdString, Lists.newArrayList());

        if (CollectionUtils.isEmpty(twLists)) {
            for (String interfaceName : interfaceList) {
                TWList tWhiteList = TWList.build(String.valueOf(applicationId),
                        String.valueOf(interfaceType),
                        interfaceName,
                        String.valueOf(YNEnum.YES.getValue()),
                        tDictionaryType.getId());
                twLists.add(tWhiteList);
            }
            List<ApplicationWhiteListCreateParam> paramList = twLists.stream().map(twList -> {
                ApplicationWhiteListCreateParam param = new ApplicationWhiteListCreateParam();
                BeanUtils.copyProperties(twList, param);
                param.setTenantId(applicationDetailResult.getTenantId());
                param.setUserId(applicationDetailResult.getUserId());
                param.setIsHandwork(true);
                // ???????????????
                if (WhitelistUtil.isDuplicate(existWhite,
                        WhitelistUtil.buildWhiteId(param.getType(), param.getInterfaceName()))) {
                    param.setIsGlobal(!isCheckDuplicateName);
                }
                return param;
            }).collect(Collectors.toList());
            applicationWhiteListDAO.insertBatch(paramList);
            whiteListFileService.writeWhiteListFile();
            TenantCommonExt commonExt = WebPluginUtils.fillTenantCommonExt(applicationDetailResult.getTenantId(), applicationDetailResult.getEnvCode());
            configSyncService.syncAllowList(commonExt, applicationId, applicationDetailResult.getApplicationName());
            return;
        } else {
            List<String> existInterfaceList = twLists.stream().map(
                    e -> WhitelistUtil.buildWhiteId(e.getType(), e.getInterfaceName())).collect(
                    Collectors.toList());
            for (String interfaceName : interfaceList) {
                // ?????????????????????
                if (existInterfaceList.contains(WhitelistUtil.buildWhiteId(interfaceType, interfaceName))) {
                    TWList whitelist = twLists.stream()
                            .filter(twList -> interfaceName.equals(twList.getInterfaceName()))
                            .findFirst()
                            .orElse(new TWList());
                    // ????????????
                    whitelist.setIsHandwork(true);
                    if (String.valueOf(YNEnum.YES.getValue()).equals(whitelist.getUseYn())) {
                        //??????????????????????????????
                        duplicateList.add(whitelist);
                    } else {
                        //?????????????????????????????????????????????
                        whitelist.setUseYn(String.valueOf(YNEnum.YES.getValue()));
                        toUpdateList.add(whitelist);
                    }
                } else {
                    //?????????????????????????????????????????????
                    TWList tWhiteList = TWList.build(String.valueOf(applicationId),
                            String.valueOf(interfaceType),
                            interfaceName,
                            String.valueOf(YNEnum.YES.getValue()),
                            tDictionaryType.getId());
                    toAddList.add(tWhiteList);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(toAddList)) {
            List<ApplicationWhiteListCreateParam> paramList = toAddList.stream().map(twList -> {
                ApplicationWhiteListCreateParam param = new ApplicationWhiteListCreateParam();
                BeanUtils.copyProperties(twList, param);
                param.setTenantId(applicationDetailResult.getTenantId());
                param.setUserId(applicationDetailResult.getUserId());
                // ???????????????
                if (WhitelistUtil.isDuplicate(existWhite,
                        WhitelistUtil.buildWhiteId(param.getType(), param.getInterfaceName()))) {
                    param.setIsGlobal(!isCheckDuplicateName);
                }
                if (twList.getIsHandwork() == null) {
                    param.setIsHandwork(true);
                }
                return param;
            }).collect(Collectors.toList());
            applicationWhiteListDAO.insertBatch(paramList);
        }

        if (CollectionUtils.isNotEmpty(toUpdateList)) {
            List<Long> wlistIdList = toUpdateList.stream().map(TWList::getWlistId).collect(Collectors.toList());
            whiteListDAO.batchEnableWhiteList(wlistIdList);
        }
        whiteListFileService.writeWhiteListFile();
        TenantCommonExt commonExt = WebPluginUtils.fillTenantCommonExt(applicationDetailResult.getTenantId(), WebPluginUtils.traceEnvCode());
        configSyncService.syncAllowList(commonExt, applicationId, applicationDetailResult.getApplicationName());
    }

    @Override
    public void operateWhitelist(WhiteListOperateVO vo) {
        if (vo.getApplicationId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR, "??????????????????id");
        }

        List<TWList> twLists = whiteListDAO.getWhiteListByApplicationId(vo.getApplicationId());
        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(vo.getApplicationId());
        Map<String, TWList> twMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(twLists)) {
            twMap = twLists.stream().collect(
                    Collectors
                            .toMap(e -> WhitelistUtil.buildWhiteId(e.getType(), e.getInterfaceName()), e -> e, (ov, nv) -> ov));
        }

        List<String> armdString = twLists.stream().map(TWList::getInterfaceName).collect(Collectors.toList());
        // ???????????????
        List<String> existWhite = this.getExistWhite(armdString, Lists.newArrayList());
        // ???????????????
        if (vo.getType() == 1) {
            // ??????????????????????????????
            if (CollectionUtils.isNotEmpty(vo.getIds())) {
                List<TWList> beAddList = Lists.newArrayList();
                List<Long> beUpdateList = Lists.newArrayList();
                for (String id : vo.getIds()) {
                    TWList twList = twMap.get(id);
                    if (twList == null) {
                        String[] split = splitId(id);
                        twList = new TWList();
                        twList.setInterfaceName(split[0]);
                        twList.setType(String.valueOf(getInterfaceIntType(split[1])));
                        twList.setDictType("ca888ed801664c81815d8c4f5b8dff0c");
                        twList.setApplicationId(vo.getApplicationId() + "");
                        twList.setUseYn("1");
                        twList.setTenantId(applicationDetailResult.getTenantId());
                        twList.setUserId(applicationDetailResult.getUserId());
                        twList.setIsGlobal(true);
                        // ????????????
                        if (WhitelistUtil.isDuplicate(existWhite,
                                WhitelistUtil.buildWhiteId(twList.getType(), twList.getInterfaceName()))) {
                            if (isCheckDuplicateName) {
                                twList.setIsGlobal(false);
                            }
                        }
                        beAddList.add(twList);
                    } else {
                        beUpdateList.add(twList.getWlistId());
                    }
                }
                if (CollectionUtils.isNotEmpty(beAddList)) {
                    whiteListDAO.batchAddWhiteList(beAddList);
                }
                if (CollectionUtils.isNotEmpty(beUpdateList)) {
                    whiteListDAO.batchEnableWhiteList(beUpdateList);
                }
            }
        }

        // ???????????????
        if (vo.getType() == 0) {
            if (CollectionUtils.isNotEmpty(vo.getIds())) {
                List<Long> beList = Lists.newArrayList();
                for (String id : vo.getIds()) {
                    TWList twList = twMap.get(id);
                    if (twList != null) {
                        beList.add(twList.getWlistId());
                    }
                }
                if (CollectionUtils.isNotEmpty(beList)) {
                    whiteListDAO.batchDisableWhiteList(beList);
                }
            }
        }
        ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(vo.getApplicationId());
        whiteListFileService.writeWhiteListFile();
        TenantCommonExt tenantCommonExt = WebPluginUtils.fillTenantCommonExt(tApplicationMnt.getTenantId(), tApplicationMnt.getEnvCode());
        configSyncService.syncAllowList(tenantCommonExt, vo.getApplicationId(), tApplicationMnt.getApplicationName());
    }

    @Override
    public List<String> getExistWhite(List<String> interfaceNames, List<ApplicationDetailResult> appDetailResults) {
        List<String> existWhites = Lists.newArrayList();
        // ????????????
        List<WhitelistResult> results = whiteListDAO.getList(new WhitelistSearchParam());

        if (CollectionUtils.isEmpty(appDetailResults)) {
            appDetailResults = getApplicationDetailResults();
        }
        Map<Long, List<ApplicationDetailResult>> appMap = appDetailResults
                .stream().collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationId));
        List<String> appNameWhiteIds = results.stream().map(result -> {
            // ?????????
            List<ApplicationDetailResult> appList = appMap.get(result.getApplicationId());
            return WhitelistUtil.buildAppNameWhiteId(
                    CollectionUtils.isNotEmpty(appList) ? appList.get(0).getApplicationName() : "",
                    result.getType(), result.getInterfaceName());
        }).collect(Collectors.toList());

        List<String> list = results.stream().map(
                        result -> WhitelistUtil.buildWhiteId(result.getType(), result.getInterfaceName()))
                .collect(Collectors.toList());
        existWhites.addAll(list);
        // amdb
        ApplicationInterfaceQueryDTO query = new ApplicationInterfaceQueryDTO();
        query.setPageSize(1000);
        query.setServiceName(StringUtils.join(interfaceNames, ","));
        // ?????????????????????????????????
        query.setAppName(appDetailResults.stream().map(ApplicationDetailResult::getApplicationName)
                .collect(Collectors.joining(",")));
        List<ApplicationInterfaceDTO> dtos = applicationClient.listInterfaces(query);
        if (CollectionUtils.isNotEmpty(dtos)) {
            List<String> amdbInferfaces = dtos.stream().filter(item -> {
                        Integer interfaceType = getInterfaceIntType(item.getInterfaceType());
                        String appNameWhiteId = WhitelistUtil.buildAppNameWhiteId(item.getAppName(), interfaceType,
                                item.getInterfaceName());
                        // ???AMDB???????????????????????????????????????????????????????????????--20210303 CYF
                        return (appNameWhiteIds.stream().filter(e -> e.equals(appNameWhiteId)).count() == 0) && (!"-1".equals(
                                interfaceType));
                    }).map(item -> WhitelistUtil
                            .buildWhiteId(getInterfaceIntType(item.getInterfaceType()), item.getInterfaceName()))
                    .collect(Collectors.toList());
            existWhites.addAll(amdbInferfaces);
        }
        return existWhites;
    }

    @Override
    public PageInfo<WhiteListVO> queryWhitelist(WhiteListQueryVO vo) {
        Map<String, WhiteListVO> totalResult = Maps.newHashMap();

        List<TWList> dbResult = whiteListDAO.queryDistinctWhiteListTotalByApplicationId(vo.getApplicationId());
        String applicationName = applicationDAO.selectApplicationName(String.valueOf(vo.getApplicationId()));
        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(vo.getApplicationId());
        if (applicationName == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR, "????????????id ??????????????????");
        }

        if (vo.getUseYn() == null) {
            //???????????????????????????????????????
            List<InterfaceVo> cardingResult = getAllInterface(applicationName);
            //??????mysql????????????????????????????????????
            if (CollectionUtils.isNotEmpty(cardingResult)) {
                mergeCardingList(totalResult, cardingResult, applicationDetailResult);
            }
            if (CollectionUtils.isNotEmpty(dbResult)) {
                mergeDbList(totalResult, dbResult);
            }

        } else if (vo.getUseYn() == 1) {
            if (CollectionUtils.isNotEmpty(dbResult)) {
                List<TWList> dbFilterResult = dbResult.stream().filter(w -> "1".equals(w.getUseYn())).collect(
                        Collectors.toList());
                mergeDbList(totalResult, dbFilterResult);
            }

        } else {
            //???????????????????????????????????????
            List<InterfaceVo> cardingResult = getAllInterface(applicationName);
            //??????mysql????????????????????????????????????
            if (CollectionUtils.isNotEmpty(cardingResult)) {
                mergeCardingList(totalResult, cardingResult, applicationDetailResult);
            }

            if (CollectionUtils.isNotEmpty(dbResult)) {
                mergeDbList(totalResult, dbResult);
                List<TWList> dbFilterResult = dbResult.stream().filter(w -> "1".equals(w.getUseYn())).collect(
                        Collectors.toList());
                mergeOutList(totalResult, dbFilterResult);
            }
        }

        if (totalResult.size() == 0) {
            return new PageInfo<>(Lists.newArrayList());
        }
        Iterator<Map.Entry<String, WhiteListVO>> iterator = totalResult.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WhiteListVO> next = iterator.next();
            if (vo.getInterfaceType() != null && (!next.getValue().getInterfaceType().equals(vo.getInterfaceType()))) {
                iterator.remove();
                continue;
            }
            if (StringUtils.isNotBlank(vo.getInterfaceName()) && (!next.getValue().getInterfaceName().contains(
                    vo.getInterfaceName()))) {
                iterator.remove();
            }
            // nothing;
        }

        int start = Math.min(vo.getOffset(), totalResult.size());
        int end = Math.min((vo.getOffset() + vo.getPageSize()), totalResult.size());
        List<WhiteListVO> resList = new ArrayList<>(totalResult.values());
        //??????
        resList.sort(this::whiteListCompare);
        resList = resList.subList(start, end);

        List<String> armdString = resList.stream().map(WhiteListVO::getInterfaceName).collect(Collectors.toList());
        // ???????????????
        List<String> existWhite = getExistWhite(armdString, Lists.newArrayList());

        for (WhiteListVO dto : resList) {
            List<Long> allowUpdateUserIdList = WebPluginUtils.getUpdateAllowUserIdList();
            if (dto.getIsDbValue()) {
                if (CollectionUtils.isEmpty(allowUpdateUserIdList)) {
                    //?????????
                    dto.setCanEdit(true);
                } else {
                    //????????????
                    dto.setCanEdit(allowUpdateUserIdList.contains(dto.getUserId()));
                }
                //????????????????????????????????????
                if (dto.getIsHandwork() == null) {
                    dto.setIsHandwork(true);
                }
                // ???????????????????????????
                if (!dto.getIsHandwork()) {
                    dto.setCanEdit(false);
                }
            } else {
                //????????????????????????????????????
                dto.setCanEdit(false);
                dto.setIsHandwork(false);
            }

            // ????????????
            dto.setTags(getTags(existWhite, dto));
            List<Long> allowDeleteUserIdList = WebPluginUtils.getDeleteAllowUserIdList();
            if (dto.getIsDbValue()) {
                if (CollectionUtils.isEmpty(allowDeleteUserIdList)) {
                    dto.setCanRemove(true);
                } else {
                    dto.setCanRemove(allowDeleteUserIdList.contains(dto.getUserId()));
                }
                // ???????????????????????????
                if (!dto.getIsHandwork()) {
                    dto.setCanEdit(false);
                }
            } else {
                dto.setCanRemove(false);
            }

            List<Long> allowEnableDisableUserIdList = WebPluginUtils.getEnableDisableAllowUserIdList();
            if (CollectionUtils.isNotEmpty(allowEnableDisableUserIdList)) {
                dto.setCanEnableDisable(allowEnableDisableUserIdList.contains(dto.getUserId()));
            }
        }
        PageInfo<WhiteListVO> whiteListDtoPageInfo = new PageInfo<>(resList);
        whiteListDtoPageInfo.setTotal(totalResult.size());
        return whiteListDtoPageInfo;
    }

    public int whiteListCompare(WhiteListVO o1, WhiteListVO o2) {
        int sort;
        if (o1.getGmtUpdate() != null && o2.getGmtUpdate() != null) {
            // ??????????????????
            sort = -o1.getGmtUpdate().compareTo(o2.getGmtUpdate());
            if (sort != 0) {
                return sort;
            }
        }
        if (o1.getDbId() != null && o2.getDbId() != null) {
            sort = -o1.getDbId().compareTo(o2.getDbId());
            if (sort != 0) {
                return sort;
            }
        }
        return o1.getInterfaceName().compareTo(o2.getInterfaceName());
    }

    private void mergeOutList(Map<String, WhiteListVO> totalResult, List<TWList> dbResult) {
        if (MapUtils.isEmpty(totalResult) || CollectionUtils.isEmpty(dbResult)) {
            return;
        }
        dbResult.forEach(it -> {
            String id = WhitelistUtil.buildWhiteId(it.getType(), it.getInterfaceName());
            totalResult.remove(id);
        });
    }

    private void mergeCardingList(Map<String, WhiteListVO> totalResult, List<InterfaceVo> graphResult,
                                  ApplicationDetailResult applicationDetailResult) {
        if (CollectionUtils.isEmpty(graphResult)) {
            return;
        }
        graphResult.forEach(graph -> {
            String interfaceType = graph.getInterfaceType();
            String id = WhitelistUtil.buildWhiteId(interfaceType, graph.getInterfaceName());
            WhiteListVO whiteListVO = new WhiteListVO();
            whiteListVO.setWlistId(id);
            whiteListVO.setUseYn(0);
            whiteListVO.setInterfaceType(getInterfaceIntType(interfaceType));
            whiteListVO.setInterfaceName(graph.getInterfaceName());
            whiteListVO.setTenantId(applicationDetailResult.getTenantId());
            whiteListVO.setUserId(applicationDetailResult.getUserId());
            whiteListVO.setIsDbValue(false);
            // ??????????????????
            whiteListVO.setIsGlobal(true);
            // ??????
            whiteListVO.setIsHandwork(false);
            totalResult.putIfAbsent(id, whiteListVO);
        });
    }

    private void mergeDbList(Map<String, WhiteListVO> totalResult, List<TWList> dbResults) {
        if (CollectionUtils.isEmpty(dbResults)) {
            return;
        }
        dbResults.forEach(dbResult -> {
            String interfaceType = dbResult.getType();
            String id = WhitelistUtil.buildWhiteId(interfaceType, dbResult.getInterfaceName());

            WhiteListVO whiteListVO = new WhiteListVO();
            // ????????????
            whiteListVO.setIsGlobal(dbResult.getIsGlobal());
            //????????????
            whiteListVO.setIsHandwork(dbResult.getIsHandwork());
            whiteListVO.setWlistId(id);
            whiteListVO.setUseYn(Integer.parseInt(dbResult.getUseYn()));
            Integer type = getInterfaceIntType(interfaceType);
            whiteListVO.setInterfaceType(type);
            whiteListVO.setInterfaceName(dbResult.getInterfaceName());
            whiteListVO.setGmtUpdate(dbResult.getGmtModified());
            whiteListVO.setTenantId(dbResult.getTenantId());
            whiteListVO.setUserId(dbResult.getUserId());
            whiteListVO.setIsDbValue(true);
            whiteListVO.setGmtCreate(dbResult.getCreateTime());
            whiteListVO.setDbId(String.valueOf(dbResult.getWlistId()));
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            totalResult.merge(id, whiteListVO, (ov, nv) -> {
                // ????????????????????????????????????????????????????????????????????????????????????????????????
                // ??????AMDB?????????????????????????????????
                nv.setIsDbValue(ov.getIsDbValue() & nv.getIsDbValue());
                return nv;
            });
        });
    }

    @Override
    public List<TWList> getAllEnableWhitelists(String applicationId) {
        return whiteListDAO.getAllEnableWhitelists(applicationId);
    }

    @Override
    public void updateWhitelist(WhiteListUpdateRequest request) {
        WhiteListEntity whiteListEntity = whiteListMapper.selectById(request.getDbId());
        if (whiteListEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR,
                    String.format("ID[%s]????????????????????????????????????????????????", request.getDbId()));
        }
        // ???????????????
        if (WhitelistUtil.checkWhiteFormatError(request.getInterfaceName(), number)) {
            throw new TakinWebException(ExceptionCode.WHITELIST_FORMAT_APP_ERROR, "??????????????????????????????????????????????????????");
        }

        // ????????????????????????
        if (!request.getInterfaceType().equals(whiteListEntity.getType()) || !request.getInterfaceName().equals(
                whiteListEntity.getInterfaceName())) {
            // ???????????????????????????????????????
            //??????????????????????????????????????????????????????????????????????????????
            List<TWList> existWhiteLists = whiteListDAO.queryWhiteListTotalByApplicationId(
                    whiteListEntity.getApplicationId());
            List<String> data = existWhiteLists.stream().map(
                    tw -> WhitelistUtil.buildWhiteId(tw.getType(), tw.getInterfaceName())).collect(
                    Collectors.toList());
            if (data.contains(WhitelistUtil.buildWhiteId(request.getInterfaceType(), request.getInterfaceName()))) {
                throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR,
                        String.format("??????????????????, id: %d????????????????????????????????????", request.getDbId()));
            }
        }

        WhiteListEntity updateEntity = new WhiteListEntity();
        updateEntity.setInterfaceName(request.getInterfaceName());
        updateEntity.setType(request.getInterfaceType());
        updateEntity.setWlistId(request.getDbId());
        whiteListMapper.updateById(updateEntity);

        // ??????????????????????????????????????????
        WhitelistEffectiveAppSearchParam searchParam = new WhitelistEffectiveAppSearchParam();
        searchParam.setWlistId(request.getDbId());
        List<WhitelistEffectiveAppResult> appResults = whitelistEffectiveAppDao.getList(searchParam);
        List<WhitelistUpdatePartAppNameParam> params = appResults.stream().map(result -> {
            WhitelistUpdatePartAppNameParam param = new WhitelistUpdatePartAppNameParam();
            BeanUtils.copyProperties(result, param);
            param.setInterfaceName(request.getInterfaceName());
            param.setType(request.getInterfaceType());
            param.setGmtModified(new Date());
            return param;
        }).collect(Collectors.toList());
        whitelistEffectiveAppDao.updatePartAppName(params);
        whiteListFileService.writeWhiteListFile();
        TenantCommonExt commonExt = WebPluginUtils.traceTenantCommonExt();
        configSyncService.syncAllowList(commonExt, whiteListEntity.getApplicationId(), null);
    }

    @Override
    public void deleteWhitelist(WhiteListDeleteRequest request) {
        List<WhiteListEntity> listEntities = whiteListMapper.selectBatchIds(request.getDbIds());
        if (CollectionUtils.isEmpty(listEntities)) {
            return;
        }
        List<String> interfaceNames = listEntities.stream().map(WhiteListEntity::getInterfaceName).collect(
                Collectors.toList());
        OperationLogContextHolder.addVars(Vars.INTERFACE, StringUtils.join(interfaceNames, ","));
        whiteListMapper.deleteBatchIds(request.getDbIds());
        // ????????????????????????
        List<Long> ids = listEntities.stream().map(WhiteListEntity::getWlistId).collect(Collectors.toList());
        WhitelistEffectiveAppDeleteParam deleteParam = new WhitelistEffectiveAppDeleteParam();
        deleteParam.setWlistIds(ids);
        whitelistEffectiveAppDao.batchDelete(deleteParam);
        whiteListFileService.writeWhiteListFile();
        listEntities.forEach(entry -> configSyncService.syncAllowList(WebPluginUtils.traceTenantCommonExt(), entry.getApplicationId(), null));
    }

    @Override
    public WhitelistPartVO getPart(Long wlistId) {
        WhitelistPartVO vo = new WhitelistPartVO();
        // ????????????
        WhitelistEffectiveAppSearchParam appSearchParam = new WhitelistEffectiveAppSearchParam();
        appSearchParam.setTenantId(WebPluginUtils.traceTenantId());
        appSearchParam.setWlistId(wlistId);
        List<WhitelistEffectiveAppResult> appResults = whitelistEffectiveAppDao.getList(appSearchParam);
        vo.setEffectiveAppNames(CollectionUtils.isNotEmpty(appResults) ?
                appResults.stream().map(WhitelistEffectiveAppResult::getEffectiveAppName).collect(Collectors.toList())
                : Lists.newArrayList());
        // all
        ApplicationQueryParam queryParam = new ApplicationQueryParam();
        List<String> allAppNames = applicationDAO.getAllApplicationName(queryParam);
        vo.setAllAppNames(allAppNames);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void part(WhitelistUpdatePartAppNameInput input) {
        WhitelistResult whitelistResult = whiteListDAO.selectById(input.getWlistId());
        if (whitelistResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR, "?????????????????????");
        }

        if (CollectionUtils.isEmpty(input.getEffectiveAppName())) {
            return;
        }
        UserExt user = WebPluginUtils.traceUser();
        if (WebPluginUtils.checkUserPlugin() && user == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR, "???????????????????????????");
        }

        // ??????????????????
        WhitelistEffectiveAppDeleteParam deleteParam = new WhitelistEffectiveAppDeleteParam();
        deleteParam.setTenantId(WebPluginUtils.traceTenantId());
        deleteParam.setWlistId(input.getWlistId());
        whitelistEffectiveAppDao.delete(deleteParam);
        // ????????????
        List<WhitelistAddPartAppNameParam> params = Lists.newArrayList();
        input.getEffectiveAppName().forEach(appName -> {
            WhitelistAddPartAppNameParam param = new WhitelistAddPartAppNameParam();
            param.setInterfaceName(whitelistResult.getInterfaceName());
            param.setType(whitelistResult.getType());
            param.setEffectiveAppName(appName);
            param.setTenantId(WebPluginUtils.traceTenantId());
            param.setUserId(user.getId());
            param.setWlistId(input.getWlistId());
            params.add(param);
        });
        whitelistEffectiveAppDao.addPartAppName(params);
        // ??????????????????
        WhitelistGlobalOrPartParam param = new WhitelistGlobalOrPartParam();
        param.setIsGlobal(false);
        param.setWlistId(input.getWlistId());
        whiteListDAO.updateWhitelistGlobal(param);
        // agent??????
        whiteListFileService.writeWhiteListFile();
        configSyncService.syncAllowList(WebPluginUtils.traceTenantCommonExt(), whitelistResult.getApplicationId(), null);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void global(Long wlistId) {
        WhitelistResult whitelistResult = whiteListDAO.selectById(wlistId);
        if (whitelistResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR, "?????????????????????");
        }

        // ????????????????????????
        if (isCheckDuplicateName) {
            // ???????????????
            List<String> amdbString = Lists.newArrayList();
            amdbString.add(whitelistResult.getInterfaceName());
            List<String> existWhite = this.getExistWhite(amdbString, Lists.newArrayList());
            if (WhitelistUtil.isDuplicate(existWhite,
                    WhitelistUtil.buildWhiteId(whitelistResult.getType(), whitelistResult.getInterfaceName()))) {
                throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_WHITELIST_VALIDATE_ERROR, "????????????????????????????????????");
            }
        }
        WhitelistGlobalOrPartParam param = new WhitelistGlobalOrPartParam();
        param.setIsGlobal(true);
        param.setWlistId(wlistId);
        whiteListDAO.updateWhitelistGlobal(param);
        // agent??????
        whiteListFileService.writeWhiteListFile();
        configSyncService.syncAllowList(WebPluginUtils.traceTenantCommonExt(), whitelistResult.getApplicationId(),
                null);
    }

    @Override
    public PagingList<WhiteListVO> pagingList(WhitelistSearchInput input) {
        // ????????????
        List<ApplicationDetailResult> appDetailResults = this.getApplicationDetailResults();
        // ???mysql????????????
        PagingList<WhiteListVO> dbPagingList = getDbPagingList(input, appDetailResults);
        // ???amdb????????????
        PagingList<WhiteListVO> amdbPagingList = getAmdbPagingList(input, appDetailResults);
        // ??????amdb??????
        List<WhiteListVO> results = Lists.newArrayList();
        if (Integer.valueOf(input.getPageSize() * (input.getCurrentPage() + 1)).longValue() >= dbPagingList.getTotal()
                || dbPagingList.getTotal() < input.getPageSize()) {
            // ?????? ???????????????
            results.addAll(dbPagingList.getList());
            results.addAll(amdbPagingList.getList());
        } else {
            results.addAll(dbPagingList.getList());
        }
        List<String> armdString = results.stream().map(WhiteListVO::getInterfaceName).collect(Collectors.toList());
        // ???????????????
        List<String> existWhite = getExistWhite(armdString, appDetailResults);
        results.forEach(vo -> {
            // ????????????
            vo.setTags(getTags(existWhite, vo));
        });
        return PagingList.of(results, dbPagingList.getTotal() + amdbPagingList.getTotal());
    }

    private List<ApplicationDetailResult> getApplicationDetailResults() {
        ApplicationQueryParam queryParam = new ApplicationQueryParam();
        if (WebPluginUtils.traceUser() == null) {
            // ???????????????
            return Lists.newArrayList();
        }
        queryParam.setTenantId(WebPluginUtils.traceTenantId());
        queryParam.setEnvCode(WebPluginUtils.traceEnvCode());
        return applicationDAO.getApplicationList(queryParam);
    }

    private PagingList<WhiteListVO> getAmdbPagingList(WhitelistSearchInput input,
                                                      List<ApplicationDetailResult> appDetailResults) {
        ApplicationInterfaceQueryDTO query = new ApplicationInterfaceQueryDTO();
        // ????????????????????????
        query.setPageSize(input.getPageSize());
        if (StringUtils.isNotBlank(input.getInterfaceName())) {
            query.setServiceName(StringUtils.join(input.getInterfaceName(), ","));
        }
        if (StringUtils.isNotBlank(input.getAppName())) {
            query.setAppName(input.getAppName());
        } else {
            query.setAppName(appDetailResults.stream().map(ApplicationDetailResult::getApplicationName)
                    .collect(Collectors.joining(",")));
        }
        // ????????????
        List<WhitelistResult> results = whiteListDAO.getList(new WhitelistSearchParam());
        Map<Long, List<ApplicationDetailResult>> appMap =
                appDetailResults.stream().collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationId));
        List<String> appNameWhiteIds = results.stream().map(result -> {
            // ?????????
            List<ApplicationDetailResult> appList = appMap.get(result.getApplicationId());
            return WhitelistUtil.buildAppNameWhiteId(
                    CollectionUtils.isNotEmpty(appList) ? appList.get(0).getApplicationName() : "",
                    result.getType(), result.getInterfaceName());
        }).collect(Collectors.toList());

        AtomicLong atomicLong = new AtomicLong(0);
        PagingList<ApplicationInterfaceDTO> dtos = applicationClient.pageInterfaces(query);
        if (CollectionUtils.isNotEmpty(dtos.getList())) {
            List<WhiteListVO> whiteListVoList = dtos.getList().stream().filter(item -> {
                Integer interfaceType = getInterfaceIntType(item.getInterfaceType());
                String appNameWhiteId = WhitelistUtil.buildAppNameWhiteId(item.getAppName(), interfaceType,
                        item.getInterfaceName());
                // ???AMDB???????????????????????????????????????????????????????????????--20210303 CYF
                boolean flag = (appNameWhiteIds.stream().noneMatch(e -> e.equals(appNameWhiteId))) &&
                        (!"-1".equals(interfaceType.toString()));
                if (!flag) {
                    // ?????????????????????
                    atomicLong.incrementAndGet();
                }
                return flag;
            }).map(item -> {
                WhiteListVO vo = new WhiteListVO();
                vo.setAppName(item.getAppName());
                vo.setInterfaceType(getInterfaceIntType(item.getInterfaceType()));
                vo.setInterfaceName(item.getInterfaceName());
                vo.setIsHandwork(false);
                // ?????????
                vo.setUseYn(0);
                vo.setIsGlobal(true);
                return vo;
            }).collect(Collectors.toList());
            return PagingList.of(whiteListVoList, dtos.getTotal() - atomicLong.get());
        } else {
            return PagingList.empty();
        }
    }

    private PagingList<WhiteListVO> getDbPagingList(WhitelistSearchInput input,
                                                    List<ApplicationDetailResult> appDetailResults) {
        WhitelistSearchParam param = new WhitelistSearchParam();
        BeanUtils.copyProperties(input, param);
        // ?????????????????????
        if (StringUtils.isNotBlank(input.getAppName())) {
            List<Long> ids = appDetailResults.stream().filter(
                            app -> app.getApplicationName().contains(input.getAppName()))
                    .map(ApplicationDetailResult::getApplicationId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(ids)) {
                return PagingList.empty();
            }
            param.setIds(ids);
        }
        if (StringUtils.isNotBlank(input.getInterfaceName())) {
            List<String> interfaceNames = Lists.newArrayList(StringUtils.split(input.getInterfaceName(), ","));
            param.setInterfaceNames(interfaceNames);
        }
        PagingList<WhiteListVO> pagingList = whiteListDAO.pagingList(param);
        // ????????????
        // ????????????????????????????????????????????????
        WhitelistEffectiveAppSearchParam searchParam = new WhitelistEffectiveAppSearchParam();
        List<WhitelistEffectiveAppResult> effectiveAppDaoList = whitelistEffectiveAppDao.getList(searchParam);
        Map<Long, List<WhitelistEffectiveAppResult>> appResultsMap = effectiveAppDaoList.stream()
                .collect(Collectors.groupingBy(WhitelistEffectiveAppResult::getWlistId));

        // ?????????
        Map<Long, List<ApplicationDetailResult>> appMap =
                appDetailResults.stream().collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationId));

        // ??????
        pagingList.getList().forEach(vo -> {
            List<ApplicationDetailResult> appList = appMap.get(Long.valueOf(vo.getApplicationId()));
            if (CollectionUtils.isNotEmpty(appList)) {
                vo.setAppName(appList.get(0).getApplicationName());
            }
            // ?????????????????????
            vo.setInterfaceType(getInterfaceIntType(String.valueOf(vo.getInterfaceType())));
            // ??????
            List<WhitelistEffectiveAppResult> appResults = appResultsMap.get(Long.valueOf(vo.getWlistId()));
            vo.setEffectiveAppNames(CollectionUtils.isNotEmpty(appResults) ?
                    appResults.stream().map(WhitelistEffectiveAppResult::getEffectiveAppName).collect(Collectors.toList())
                    : Lists.newArrayList());
        });
        return pagingList;
    }

    private List<String> getTags(List<String> list, WhiteListVO vo) {
        List<String> tags = Lists.newArrayList();
        // ????????????
        if (WhitelistUtil.isDuplicate(list, WhitelistUtil.buildWhiteId(vo.getInterfaceType(), vo.getInterfaceName()))) {
            tags.add(WhitelistTagEnum.DUPLICATE_NAME.getTagName());
            if (isCheckDuplicateName) {
                vo.setIsGlobal(false);
            }
        }
        // ??????????????????
        if (vo.getIsHandwork() != null && vo.getIsHandwork()) {
            tags.add(WhitelistTagEnum.MANUALLY_ADD.getTagName());
        }
        return tags;
    }

    private String[] splitId(String id) {
        return id.split("@@");
    }

    @Override
    public List<InterfaceVo> getAllInterface(String appName) {
        ApplicationInterfaceQueryDTO query = new ApplicationInterfaceQueryDTO();
        query.setAppName(appName);
        List<ApplicationInterfaceDTO> applicationInterfaceDtoList = applicationClient.listInterfaces(query);
        if (CollectionUtils.isEmpty(applicationInterfaceDtoList)) {
            return Lists.newArrayList();
        }
        return applicationInterfaceDtoList.stream().map(item -> {
            InterfaceVo interfaceVo = new InterfaceVo();
            interfaceVo.setId(item.getId());
            Integer interfaceType = getInterfaceIntType(item.getInterfaceType());
            interfaceVo.setInterfaceType(String.valueOf(interfaceType));
            interfaceVo.setInterfaceName(item.getInterfaceName());
            return interfaceVo;
        }).filter(
                // ???AMDB???????????????????????????????????????????????????????????????--20210303 CYF
                interfaceVo -> !"-1".equals(interfaceVo.getInterfaceType())
        ).collect(Collectors.toList());
    }
}
