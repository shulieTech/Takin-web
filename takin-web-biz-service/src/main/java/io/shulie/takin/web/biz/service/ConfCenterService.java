package io.shulie.takin.web.biz.service;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.MQConstant;
import com.pamirs.takin.common.constant.TakinDictTypeEnum;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.constant.WListRuleEnum;
import com.pamirs.takin.common.constant.WListTypeEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.redis.RedisKey;
import com.pamirs.takin.common.redis.WhiteBlackListRedisKey;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.common.util.TakinFileUtil;
import com.pamirs.takin.entity.dao.confcenter.TLinkMntDao;
import com.pamirs.takin.entity.domain.entity.TAlarm;
import com.pamirs.takin.entity.domain.entity.TApplicationIp;
import com.pamirs.takin.entity.domain.entity.TBList;
import com.pamirs.takin.entity.domain.entity.TLinkServiceMnt;
import com.pamirs.takin.entity.domain.entity.TPradaHttpData;
import com.pamirs.takin.entity.domain.entity.TSecondLinkMnt;
import com.pamirs.takin.entity.domain.entity.TWList;
import com.pamirs.takin.entity.domain.query.ApplicationQueryRequest;
import com.pamirs.takin.entity.domain.query.BListQueryParam;
import com.pamirs.takin.entity.domain.query.Result;
import com.pamirs.takin.entity.domain.query.TWListVo;
import com.pamirs.takin.entity.domain.vo.TApplicationInterface;
import com.pamirs.takin.entity.domain.vo.TLinkApplicationInterface;
import com.pamirs.takin.entity.domain.vo.TLinkBasicVO;
import com.pamirs.takin.entity.domain.vo.TLinkMntDictoryVo;
import com.pamirs.takin.entity.domain.vo.TLinkNodesVo;
import com.pamirs.takin.entity.domain.vo.TLinkServiceMntVo;
import com.pamirs.takin.entity.domain.vo.TLinkTopologyInfoVo;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceDataVo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.biz.service.linkmanage.impl.WhiteListFileService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.whitelist.WhitelistUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationPluginsConfigDAO;
import io.shulie.takin.web.data.dao.application.WhiteListDAO;
import io.shulie.takin.web.data.dao.blacklist.BlackListDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginsConfigEntity;
import io.shulie.takin.web.data.param.application.ApplicationCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.blacklist.BlackListCreateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * ??????: ????????????service
 *
 * @author shulie
 * @date 2019/4/4 16:36
 * @see
 */
@Service
@Slf4j
public class ConfCenterService extends CommonService {

    private Integer number;

    @Autowired
    @Qualifier("modifyMonitorThreadPool")
    protected ThreadPoolExecutor modifyMonitorExecutor;

    @Autowired
    private WhiteListFileService whiteListFileService;

    @Autowired
    private BlackListDAO blackListDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationPluginsConfigService pluginsConfigService;

    @Autowired
    private ApplicationPluginsConfigDAO applicationPluginsConfigDAO;

    @Autowired
    private AppRemoteCallService appRemoteCallService;

    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @Autowired
    private ApplicationService applicationService;

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    public static final String APPLICATION_CACHE_PREFIX = "application:cache";

    @PostConstruct
    public void init() {
        number = ConfigServerHelper.getWrapperIntegerValueByKey(ConfigServerKeyEnum.TAKIN_WHITE_LIST_NUMBER_LIMIT);
    }

    /**
     * ??????: ????????????????????????????????????,????????????????????????,???????????????????????????
     *
     * @throws TakinModuleException ??????????????????
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveApplication(ApplicationCreateParam tApplicationMnt) throws TakinModuleException {
        int applicationExist = applicationDAO.applicationExistByTenantIdAndAppName(
                WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), tApplicationMnt.getApplicationName());
        if (applicationExist > 0) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_APPLICATION_DUPICATE_EXCEPTION);
        }
        addApplication(tApplicationMnt);
        addApplicationToDataBuild(tApplicationMnt);
        addApplicationToLinkDetection(tApplicationMnt);
        addPluginsConfig(tApplicationMnt);//????????????-????????????key??????????????????
    }

    private void addPluginsConfig(ApplicationCreateParam applicationMnt) {
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
        param.setConfigItem("redis??????key?????????");
        param.setConfigKey("redis_expire");
        param.setConfigDesc("??????????????????redis??????key???????????????????????????key??????????????????????????????????????????key???????????????????????????????????????key??????????????????");
        param.setConfigValue("-1");
        param.setApplicationName(applicationMnt.getApplicationName());
        param.setApplicationId(applicationMnt.getApplicationId());
        param.setTenantId(applicationMnt.getTenantId());
        param.setEnvCode(applicationMnt.getEnvCode());
        param.setUserId(applicationMnt.getUserId());
        pluginsConfigService.add(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAgentRegisterApplication(ApplicationCreateParam tApplicationMnt) {
        int applicationExist = applicationDAO.applicationExistByTenantIdAndAppName(
                WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), tApplicationMnt.getApplicationName());
        if (applicationExist > 0) {
            OperationLogContextHolder.ignoreLog();
            return;
        }
        addApplication(tApplicationMnt);
        addApplicationToDataBuild(tApplicationMnt);
        addApplicationToLinkDetection(tApplicationMnt);
        //?????????????????????????????????????????????redis??????key?????????
        addPluginsConfig(tApplicationMnt);

    }

    /**
     * ??????: ??????????????????????????????????????????
     *
     * @author shulie
     */
    private void addApplicationToLinkDetection(ApplicationCreateParam tApplicationMnt) {
        Map<String, Object> map = new HashMap<String, Object>(10) {
            private static final long serialVersionUID = 1L;

            {
                put("linkDetectionId", snowflake.next());
                put("applicationId", tApplicationMnt.getApplicationId());
            }
        };
        TLinkDetectionDao.insertLinkDetection(map);
    }

    /**
     * ??????: ??????????????????????????????????????????
     *
     * @param tApplicationMnt ??????????????????
     * @author shulie
     */
    private void addApplicationToDataBuild(ApplicationCreateParam tApplicationMnt) {
        Map<String, Object> map = new HashMap<String, Object>(10) {
            private static final long serialVersionUID = 1L;

            {
                put("dataBuildId", snowflake.next());
                put("applicationId", tApplicationMnt.getApplicationId());
                if ("0".equals(tApplicationMnt.getCacheExpTime())) {
                    put("cacheBuildStatus", 2);
                }
            }
        };
        TDataBuildDao.insertDataBuild(map);
    }

    @PostConstruct
    public void initWhiteList() {
        writeWhiteListFile();
    }

    /**
     * ??????: ??????????????????
     *
     * @param tApplicationMnt ?????????????????????
     * @author shulie
     */
    private void addApplication(ApplicationCreateParam tApplicationMnt) {
        tApplicationMnt.setApplicationId(snowflake.next());
        tApplicationMnt.setCacheExpTime(
                StringUtils.isEmpty(tApplicationMnt.getCacheExpTime()) ? "0" : tApplicationMnt.getCacheExpTime());

        applicationDAO.insert(tApplicationMnt);

        try {
            TakinFileUtil.createFile(getBasePath() + tApplicationMnt.getApplicationName());
        } catch (Throwable e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_CONFIG_FILE_CREATE_ERROR, "?????????????????????", e);
        }

    }

    /**
     * ??????: ?????????????????????
     *
     * @param paramMap ????????????
     * @return ???????????????
     * @author shulie
     */
    public PageInfo<TApplicationInterface> queryWhiteList(Map<String, Object> paramMap) {
        String applicationName = MapUtils.getString(paramMap, "applicationName");
        String principalNo = MapUtils.getString(paramMap, "principalNo");
        String type = MapUtils.getString(paramMap, "type");
        String whiteListUrl = MapUtils.getString(paramMap, "whiteListUrl");
        Long applicationId = MapUtils.getLong(paramMap, "applicationId");
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TApplicationInterface> queryWhiteListInfo = whiteListDAO.queryOnlyWhiteList(applicationName, principalNo, type,
                whiteListUrl, null, applicationId);

        return new PageInfo<>(queryWhiteListInfo.isEmpty() ? Lists.newArrayList() : queryWhiteListInfo);
    }

    public List<TApplicationInterface> queryWhiteListDownLoad(Map<String, Object> paramMap) {
        String applicationName = MapUtils.getString(paramMap, "applicationName");
        String principalNo = MapUtils.getString(paramMap, "principalNo");
        String type = MapUtils.getString(paramMap, "type");
        String whiteListUrl = MapUtils.getString(paramMap, "whiteListUrl");
        List<String> applicationIds = null;
        Object whiteListIds = paramMap.get("wlistIds");
        if (null != whiteListIds) {
            applicationIds = (List) whiteListIds;
        }
        return whiteListDAO.queryOnlyWhiteList(applicationName, principalNo, type, whiteListUrl, applicationIds, null);
    }

    /**
     * ??????????????????
     */
    public void projectPressureSwitch(Long applicationId) {
        // whiteListDAO.updateWListById();
    }

    /**
     * ??????: ????????????????????????
     *
     * @return ????????????
     * @author shulie
     */
    public PagingList<ApplicationDetailResult> queryApplicationList(ApplicationQueryRequest request) {
        ApplicationQueryParam param = new ApplicationQueryParam();
        BeanUtils.copyProperties(request, param);
        return applicationDAO.queryApplicationList(param);
    }

    /**
     * ??????: ????????????id????????????????????????
     *
     * @param applicationId ??????id
     * @return ????????????
     * @author shulie
     */
    public ApplicationDetailResult queryApplicationInfoById(long applicationId) {
        return applicationDAO.getApplicationById(applicationId);
    }

    /**
     * ??????: ????????????id?????????????????????????????????
     *
     * @param applicationId ??????id
     * @return ????????????
     * @author JasonYan
     */
    public ApplicationDetailResult queryApplicationInfoByIdAndRole(long applicationId) {
        ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(applicationId);
        return tApplicationMnt;
    }

    /**
     * ??????: ????????????id????????????????????????(????????????????????????)
     *
     * @param applicationIds ??????ids
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public String deleteApplicationInfoByIds(String applicationIds) {
        GetDeleteIds deleteIds = new GetDeleteIds(applicationIds, "applicationName").invoke(applicationDAO);
        List<Long> ableDeleteApplicationList = deleteIds.getAbleDeleteList();
        if (!ableDeleteApplicationList.isEmpty()) {
            whiteListDAO.deleteApplicationInfoRelatedInterfaceByIds(ableDeleteApplicationList);
            ableDeleteApplicationList.forEach(applicationId -> {
                ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(applicationId);
                redisManager.removeKey(
                        WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY + tApplicationMnt.getApplicationName());
                redisManager.removeKey(
                        WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY_METRIC + tApplicationMnt.getApplicationName());
                // ???????????????userAppKey
                //todo Aganet?????????
                agentConfigCacheManager.evictRecallCalls(tApplicationMnt.getApplicationName());
                delApplicationCache(tApplicationMnt.getApplicationName());
            });
            //?????????????????????????????????
            redisManager.removeKey(WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY);
            redisManager.removeKey(WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY_METRIC);
            applicationDAO.getApplicationByIds(ableDeleteApplicationList).forEach(
                    applicationName -> TakinFileUtil.recursiveDeleteFile(new File(getBasePath() + applicationName)));
            applicationDAO.deleteApplicationInfoByIds(ableDeleteApplicationList);
            TDataBuildDao.deleteApplicationToDataBuild(ableDeleteApplicationList);
            TLinkDetectionDao.deleteApplicationToLinkDetection(ableDeleteApplicationList);
            tShadowTableDataSourceDao.deleteByApplicationIdList(ableDeleteApplicationList);

            Map<String, Object> logMap = Maps.newHashMap();
            List<Map<String, Object>> ableDeleteApplicationWlistData = applicationDAO.queryApplicationListByIds(
                    ableDeleteApplicationList);
            List<Map<String, Object>> ableDeleteDataBuild = TDataBuildDao.queryDataBuildListByIds(
                    ableDeleteApplicationList);
            List<Map<String, Object>> ableDeleteLinkDetection = TLinkDetectionDao.queryLinkDetectionListByIds(
                    ableDeleteApplicationList);
            logMap.put("ableDeleteApplicationWlistData", ableDeleteApplicationWlistData);
            logMap.put("ableDeleteDataBuild", ableDeleteDataBuild);
            logMap.put("ableDeleteLinkDetection", ableDeleteLinkDetection);
            // ???????????????????????????
            writeWhiteListFile();
            // ????????????????????????
            appRemoteCallService.deleteByApplicationIds(ableDeleteApplicationList.stream().map(Long::valueOf).collect(Collectors.toList()));
        }
        return deleteIds.getResult();
    }

    // =================================== ???????????????=====================================================

    /**
     * ??????: ?????????????????????????????????
     *
     * @return ????????????
     * @author shulie
     */
    public List<Map<String, Object>> queryApplicationdata() {
        List<Map<String, Object>> list = transferElementToString(applicationDAO.queryApplicationData());

        return list;
    }

    /**
     * ??????: ????????????id??????????????????
     *
     * @param tApplicationMnt ???????????????
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationInfo(ApplicationCreateParam tApplicationMnt) throws TakinModuleException {
        ApplicationDetailResult originApplicationMnt = applicationDAO.getApplicationById(tApplicationMnt.getApplicationId());
        if (originApplicationMnt == null) {
            log.error("updateApplicationInfo ??????????????? ???{}???", tApplicationMnt.getApplicationName());
            return;
        }
        String originApplicationName = originApplicationMnt.getApplicationName();
        //?????????????????????  applicationName  ???????????????appName????????????
        if (!StringUtils.equals(originApplicationName, tApplicationMnt.getApplicationName())) {
            int applicationExist = applicationDAO.applicationExist(tApplicationMnt.getApplicationName());
            if (applicationExist > 0) {

                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_UPDATE_APPLICATION_DUPICATE_EXCEPTION);
            }
            TakinFileUtil.recursiveDeleteFile(new File(getBasePath() + originApplicationName));
            TakinFileUtil.createFile(getBasePath() + tApplicationMnt.getApplicationName());
        }
        applicationDAO.updateApplicationInfo(tApplicationMnt);
        //?????????????????????????????????????????? agent????????????????????????applicationName
        updatePlugins(tApplicationMnt);
        delApplicationCache(originApplicationName);
    }

    private void updatePlugins(ApplicationCreateParam tApplicationMnt) {
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
        param.setApplicationId(tApplicationMnt.getApplicationId());
        List<ApplicationPluginsConfigEntity> list = applicationPluginsConfigDAO.findList(param);
        if (list != null && list.size() > 0) {
            ApplicationPluginsConfigEntity entity = list.get(0);
            //??????????????????
            param.setApplicationName(tApplicationMnt.getApplicationName());
            param.setId(entity.getId());
            param.setConfigKey(entity.getConfigKey());
            param.setConfigValue(entity.getConfigValue());
            pluginsConfigService.update(param);
        }
    }

    /**
     * ??????: ?????????????????????
     *
     * @param twListVo ??????????????????
     * @throws TakinModuleException ??????
     * @author shulie
     */
    public List<String> saveWhiteList(TWListVo twListVo) throws TakinModuleException {
        List<String> duplicateUrlList = Lists.newArrayList();
        //MQ????????????, ???????????????????????????????????????interfaceName,??????queueName
        if (WListTypeEnum.MQ.getValue().equals(twListVo.getType())) {
            int wListExist = whiteListDAO.queryWhiteListCountByMqInfo(twListVo);
            if (wListExist > 0) {
                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_WLIST_DUPICATE_EXCEPTION);
            }
            TWList tWhiteList = new TWList();
            BeanUtils.copyProperties(twListVo, tWhiteList);
            addWhiteList(tWhiteList);
            //?????????????????????????????????
        } else {
            String appId = twListVo.getApplicationId();
            List<String> list = twListVo.getList();
            List<TWList> twLists = Lists.newArrayList();
            list.forEach(url -> {
                int wListExist = whiteListDAO.whiteListExist(appId, url, twListVo.getUseYn());
                if (wListExist > 0) {
                    duplicateUrlList.add(url);
                } else {
                    TWList tWhiteList = new TWList();
                    BeanUtils.copyProperties(twListVo, tWhiteList);
                    tWhiteList.setInterfaceName(url);
                    twLists.add(tWhiteList);
                }
            });
            //????????????
            if (twLists.size() == 0 && duplicateUrlList.size() == 0) {
                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_WLIST_INTERFACE_LOST_EXCEPTION);
            }
            if (twLists.size() == 0 && duplicateUrlList.size() != 0) {
                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_WLIST_DUPICATE_EXCEPTION);
                //for update
            }

            batchAddWhiteList(twLists);
        }
        writeWhiteListFile();
        return duplicateUrlList;
    }

    private void writeWhiteListFile() {
        try {
            String whiteListPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_WHITE_LIST_CONFIG_PATH);
            Map<String, List<Map<String, Object>>> result = queryBlackWhiteList("");
            if (null != result && result.size() > 0) {
                File file = new File(whiteListPath);
                if (!file.exists()) {
                    file.mkdirs();
                }

                if (file.exists()) {
                    file = new File(whiteListPath + "bwlist");
                    if (!file.isFile()) {
                        file.createNewFile();
                    }
                }

                ResponseOk.ResponseResult response = ResponseOk.result(result);
                String content = JSONObject.toJSONString(response);
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), false);
                BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
                bufferWritter.write(content);
                bufferWritter.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * ??????: ?????????????????????????????????
     *
     * @param twLists ????????????????????????
     * @author shulie
     */
    private void batchAddWhiteList(List<TWList> twLists) {
        whiteListDAO.batchAddWhiteList(twLists);
    }

    /**
     * ??????: ???????????????????????????
     *
     * @param tWhiteList ??????????????????
     * @author shulie
     */
    private void addWhiteList(TWList tWhiteList) {
        whiteListDAO.addWhiteList(tWhiteList);
    }

    /**
     * ??????: ?????????????????????,???????????????????????????????????????????????????,???????????????List<TLinkApplicationInterface>
     *
     * @param applicationName ????????????
     * @param principalNo     ???????????????
     * @param type            ???????????????
     * @return ???????????????????????????
     * @author shulie
     */

    public List<TApplicationInterface> queryOnlyWhiteList(String applicationName, String principalNo, String type, String x,
                                                          Long appId) {
        List<TApplicationInterface> queryWhiteListInfo = whiteListDAO.queryOnlyWhiteList(applicationName, principalNo, type, x,
                null, null);
        return queryWhiteListInfo.isEmpty() ? Lists.newArrayList() : queryWhiteListInfo;
    }


    /**
     * ??????: ??????id?????????????????????
     *
     * @param whitelistId ?????????id
     * @return ?????????????????????
     * @throws TakinModuleException ??????
     * @author shulie
     */
    public TWList querySingleWhiteListById(String whitelistId) throws TakinModuleException {
        TWList tWlist = whiteListDAO.querySingleWhiteListById(whitelistId);
        if (tWlist == null) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYID_NOTEXIST);
        }

        return tWlist;
    }

    /**
     * ??????:  ??????id?????????????????????
     *
     * @param param ??????????????????
     * @author shulie
     */
    public void updateWhiteListById(TWList param) throws TakinModuleException {
        TWList dbData = whiteListDAO.querySingleWhiteListById(String.valueOf(param.getWlistId()));
        if (dbData == null) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYID_NOTEXIST);
        }
        int applicationExist = whiteListDAO.whiteListExist(param.getApplicationId(), param.getInterfaceName(),
                param.getUseYn());
        //            ?????????,???????????????????????????,??????????????????1????????????
        if (applicationExist > 1) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_UPDATE_WLIST_DUPICATE_EXCEPTION);
        }
        //?????????????????????????????????
        whiteListDAO.updateSelective(param);
        //???????????? nginx??????
        writeWhiteListFile();
    }

    /**
     * ??????: ?????????????????????
     *
     * @param whiteListIds ?????????id(??????id???????????????)
     * @author shulie
     */
    public String deleteWhiteListByIds(String whiteListIds) {
        GetDeleteIds deleteIds = new GetDeleteIds(whiteListIds, "interfaceName").invoke(whiteListDAO);
        List<String> ableDeleteWhiteList = deleteIds.getAbleDeleteList();
        if (!ableDeleteWhiteList.isEmpty()) {
            List<TWList> ableDeleteWhiteLists = whiteListDAO.queryWhiteListByIds(ableDeleteWhiteList);
            whiteListDAO.deleteWhiteListByIds(ableDeleteWhiteList);
            //?????????????????????????????????
            ableDeleteWhiteLists.stream().map(TWList::getApplicationId).distinct().forEach(applicationId -> {

            });

        }
        writeWhiteListFile();
        return deleteIds.getResult();
    }

    /**
     * ??????: ??????id?????????????????????
     *
     * @author shulie
     */
    public void deleteWhiteListByIds(List<Long> ids) {
        if (!ids.isEmpty()) {
            List<TWList> ableDeleteWhiteLists = whiteListDAO.getWhiteListByIds(ids);
            whiteListDAO.deleteByIds(ids);

        }
        writeWhiteListFile();
    }

    // =================================== ???????????????=====================================================

    /**
     * ??????: ?????????????????????
     *
     * @param tBlackList ??????????????????
     * @throws TakinModuleException ??????
     * @author shulie
     */
    public void saveBlackList(TBList tBlackList) throws TakinModuleException {
        if (StringUtils.isBlank(tBlackList.getUseYn())) {
            tBlackList.setUseYn("1");
        }
        int applicationExist = tBListMntDao.bListExist(tBlackList.getRedisKey());
        if (applicationExist > 0) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_BLIST_DUPICATE_EXCEPTION);
        }
        addBlackList(tBlackList);
        writeWhiteListFile();
    }

    /**
     * ??????: ???????????????????????????
     *
     * @param tBlackList ??????????????????
     * @author shulie
     */
    private void addBlackList(TBList tBlackList) {
        BlackListCreateParam param = new BlackListCreateParam();
        param.setRedisKey(tBlackList.getRedisKey());
        param.setUseYn(Integer.parseInt(tBlackList.getUseYn()));
        param.setCreateTime(new Date());
        param.setUpdateTime(new Date());
        blackListDAO.insert(param);
        configSyncService.syncBlockList(WebPluginUtils.traceTenantCommonExt());
        whiteListFileService.writeWhiteListFile();
    }

    /**
     * ??????: ??????id?????????????????????
     *
     * @return ?????????????????????
     * @author shulie
     */
    public TBList querySingleBlackListById(String blistId) {
        return tBListMntDao.querySingleBListById(blistId);
    }

    /**
     * ??????: ??????id?????????????????????
     *
     * @param tBlackList ??????????????????
     * @author shulie
     */
    public void updateBlackListById(TBList tBlackList) {
        TBList originBlackList = tBListMntDao.querySingleBListById(String.valueOf(tBlackList.getBlistId()));
        tBListMntDao.updateBListById(tBlackList);
        configSyncService.syncBlockList(WebPluginUtils.traceTenantCommonExt());
        whiteListFileService.writeWhiteListFile(WebPluginUtils.traceTenantCommonExt());
    }

    /**
     * ??????: ???????????????????????????
     *
     * @param blistIds ?????????id(??????id???????????????)
     * @author shulie
     */
    @Deprecated
    public void deleteBlackListByIds(String blistIds) {
        List<String> blistIdList = Arrays.stream(blistIds.split(",")).filter(StringUtils::isNotEmpty).distinct()
                .collect(Collectors.toList());
        tBListMntDao.deleteBListByIds(blistIdList);
        configSyncService.syncBlockList(WebPluginUtils.traceTenantCommonExt());
        List<TBList> deleteBlackLists = tBListMntDao.queryBListByIds(blistIdList);
        whiteListFileService.writeWhiteListFile(WebPluginUtils.traceTenantCommonExt());
    }

    public List<TBList> queryBlackListByIds(List<Long> blistIds) {
        if (CollectionUtils.isEmpty(blistIds)) {
            return Lists.newArrayList();
        }
        List<String> blistIdList = blistIds.stream().map(String::valueOf).collect(Collectors.toList());
        return tBListMntDao.queryBListByIds(blistIdList);
    }

    /**
     * ??????: ?????????????????????
     *
     * @param bListQueryParam redis???key??????????????????
     * @return ?????????????????????
     * @author shulie
     */
    public Response queryBlackList(BListQueryParam bListQueryParam) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageNum", bListQueryParam.getCurrentPage());
        paramMap.put("pageSize", bListQueryParam.getPageSize());
        String redisKey = bListQueryParam.getRedisKey();
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TBList> queryBlackList = tBListMntDao.queryBList(redisKey, "", WebPluginUtils.getQueryAllowUserIdList());
        if (CollectionUtils.isNotEmpty(queryBlackList)) {
            for (TBList tbList : queryBlackList) {
                List<Long> allowUpdateUserIdList = WebPluginUtils.getUpdateAllowUserIdList();
                if (CollectionUtils.isNotEmpty(allowUpdateUserIdList)) {
                    tbList.setCanEdit(allowUpdateUserIdList.contains(tbList.getUserId()));
                }
                List<Long> allowDeleteUserIdList = WebPluginUtils.getDeleteAllowUserIdList();
                if (CollectionUtils.isNotEmpty(allowDeleteUserIdList)) {
                    tbList.setCanRemove(allowDeleteUserIdList.contains(tbList.getUserId()));
                }
                List<Long> allowEnableDisableUserIdList = WebPluginUtils.getEnableDisableAllowUserIdList();
                if (CollectionUtils.isNotEmpty(allowEnableDisableUserIdList)) {
                    tbList.setCanEnableDisable(allowEnableDisableUserIdList.contains(tbList.getUserId()));
                }
            }
        }
        PageInfo<TBList> pageInfo = new PageInfo<>(queryBlackList.isEmpty() ? Lists.newArrayList() : queryBlackList);
        Response response = Response.success(pageInfo.getList(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? 0 : pageInfo.getTotal());
        return response;
    }

    /**
     * ??????: ??????????????????????????????agent????????????????????????USE_YN=1????????????
     *
     * @return ??????????????????
     * @author shulie
     */
    public Map<String, List<Map<String, Object>>> queryBlackWhiteList(String appName) throws TakinModuleException {

        //??????????????????  ???????????????????????????  ???????????????????????? ???????????????  ???????????????  ??????????????? ???????????????
        String appNameKey = "";
        if (StringUtils.isNotEmpty(appName)) {
            appNameKey = appName;
        }
        List<Map<String, Object>> wLists = whiteListDAO.queryWhiteListList(appName);
        List<Map<String, Object>> bLists = tBListMntDao.queryBListList();
        Map<String, List<Map<String, Object>>> resultMap = Maps.newHashMapWithExpectedSize(30);
        List<Map<String, Object>> wListsResult = Lists.newArrayList();
        if (wLists != null) {
            for (Map<String, Object> whiteItem : wLists) {
                String type = (String) whiteItem.get("TYPE");
                String interfaceName = (String) whiteItem.get("INTERFACE_NAME");
                // ????????????
                if (WhitelistUtil.checkWhiteFormatError(interfaceName, number)) {
                    continue;
                }
                if ("dubbo".equals(type)) {
                    if (StringUtils.contains(interfaceName, "#")) {
                        interfaceName = StringUtils.substringBefore(interfaceName, "#");
                    }
                }
                Map<String, Object> whiteItemNew = new HashMap<String, Object>();
                whiteItemNew.put("TYPE", type);
                // ????????????
                whiteItemNew.put("INTERFACE_NAME", interfaceName.trim());
                wListsResult.add(whiteItemNew);
            }
        }

        resultMap.put("wLists", wListsResult);
        resultMap.put("bLists", bLists);

        return resultMap;
    }

    /**
     * ??????: ????????????????????????
     *
     * @return ??????????????????
     * @author shulie
     */
    public Map<String, List<Map<String, Object>>> queryBlackWhiteMetricList(String appName) throws TakinModuleException {
        List<Map<String, Object>> wLists = null;
        List<Map<String, Object>> bLists = null;
        //??????????????????  ???????????????????????????  ???????????????????????? ???????????????  ???????????????  ??????????????? ???????????????
        String appNameKey = "";
        if (StringUtils.isNotEmpty(appName)) {
            appNameKey = appName;
        }
        Optional<Object> wListValue = redisManager.valueGet(
                WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY_METRIC + appNameKey);
        if (wListValue.isPresent()) {
            wLists = (List<Map<String, Object>>) wListValue.get();
        } else {
            RedisKey wListRedis = new RedisKey(WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY_METRIC + appNameKey,
                    WhiteBlackListRedisKey.TIMEOUT);
            wLists = whiteListDAO.queryWhiteListList(appName);
            // ????????????????????????????????????????????????????????????????????????
            //???????????????type=mq?????????????????????mqType????????????ibmmq??????rocketmq
            //????????????????????????????????????
            if (wLists == null || wLists.isEmpty()) {
                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_QUERY_NOT_WLIST_FOR_APPNAME_EXCEPTION);
            }
            wLists.forEach(map -> {
                String type = (String) map.get("TYPE");
                if ("mq".equals(type)) {
                    String mqType = (String) map.get("MQ_TYPE");
                    //?????????ESB/IBM?????????type?????????ibmmq
                    if (MQConstant.ESB.equals(mqType) || MQConstant.IBM.equals(mqType)) {
                        map.put("TYPE", "ibmmq");
                    } else if (MQConstant.ROCKETMQ.equals(mqType) || MQConstant.DPBOOT_ROCKETMQ.equals(mqType)) {
                        map.put("TYPE", "rocketmq");
                    }
                }
            });
            redisManager.valuePut(wListRedis, wLists);
        }
        Optional<Object> blackListValue = redisManager.valueGet(WhiteBlackListRedisKey.TAKIN_BLACK_LIST_KEY);
        if (blackListValue.isPresent()) {
            bLists = (List<Map<String, Object>>) blackListValue.get();
        } else {
            RedisKey bListRedis = new RedisKey(WhiteBlackListRedisKey.TAKIN_BLACK_LIST_KEY,
                    WhiteBlackListRedisKey.TIMEOUT);
            bLists = tBListMntDao.queryBListList();
            redisManager.valuePut(bListRedis, bLists);
        }
        Map<String, List<Map<String, Object>>> resultMap = Maps.newHashMapWithExpectedSize(30);

        resultMap.put("wLists", wLists);
        resultMap.put("bLists", bLists);

        return resultMap;
    }

    // =================================== ????????????=====================================================

    /**
     * ??????: ??????????????????
     *
     * @param tLinkServiceMntVo ?????????????????????
     * @throws TakinModuleException ??????
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveBasicLink(TLinkServiceMntVo tLinkServiceMntVo) throws TakinModuleException {
        Map<String, Object> selectLinkId = tLinkMnDao.selectLinkId(tLinkServiceMntVo.getLinkName());
        Long linkId = MapUtils.getLong(selectLinkId, "LINK_ID");
        String valueOf = String.valueOf(linkId == null ? "" : linkId);
        List<TLinkServiceMnt> tLinkServiceMntList = tLinkServiceMntVo.gettLinkServiceMntList();
        List<TLinkServiceMnt> tLinkServiceMntLists = Lists.newArrayList();

        if (StringUtils.isNotEmpty(valueOf)) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_LINK_EXIST);
        }

        //???????????????????????????????????????
        tLinkServiceMntVo.setLinkId(snowflake.next());
        tLinkMnDao.addBasicLink(tLinkServiceMntVo);

        saveRelationLink(tLinkServiceMntVo.getLinkId(), tLinkServiceMntVo.getTechLinks(), "t_bs_tch_link");

        if (tLinkServiceMntList != null && !tLinkServiceMntList.isEmpty()) {
            tLinkServiceMntList.forEach(tLinkServiceMnt -> {
                tLinkServiceMnt.setLinkServiceId(snowflake.next());
                tLinkServiceMnt.setLinkId(tLinkServiceMntVo.getLinkId());
                tLinkServiceMntLists.add(tLinkServiceMnt);
            });

            tLinkMnDao.addLinkInterface(tLinkServiceMntLists);
        }

    }

    /**
     * ??????: ??????????????????
     * ????????????????????????????????????????????????
     *
     * @param tLinkServiceMntVo ?????????????????????
     * @throws TakinModuleException ??????
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveLink(TLinkServiceMntVo tLinkServiceMntVo) throws TakinModuleException {
        Map<String, Object> selectLinkId = tLinkMnDao.selectLinkId(tLinkServiceMntVo.getLinkName());
        Long linkId = MapUtils.getLong(selectLinkId, "LINK_ID");
        String valueOf = String.valueOf(linkId == null ? "" : linkId);
        List<TLinkServiceMnt> tLinkServiceMntList = tLinkServiceMntVo.gettLinkServiceMntList();
        List<TLinkServiceMnt> tLinkServiceMntLists = Lists.newArrayList();

        if (StringUtils.isNotEmpty(valueOf)) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_LINK_EXIST);
        }

        //???????????????????????????????????????
        tLinkServiceMntVo.setLinkId(snowflake.next());
        tLinkMnDao.addBasicLink(tLinkServiceMntVo);

        //++ ?????????????????????????????????
        tLinkMnDao.addSecondLinkRef(tLinkServiceMntVo.getSecondLinkId(), String.valueOf(tLinkServiceMntVo.getLinkId()));

        if (tLinkServiceMntList != null && !tLinkServiceMntList.isEmpty()) {
            tLinkServiceMntList.forEach(tLinkServiceMnt -> {
                tLinkServiceMnt.setLinkServiceId(snowflake.next());
                tLinkServiceMnt.setLinkId(tLinkServiceMntVo.getLinkId());
                tLinkServiceMntLists.add(tLinkServiceMnt);
            });
            tLinkMnDao.addLinkInterface(tLinkServiceMntLists);
        }

    }

    /**
     * ??????: ????????????????????????????????????
     *
     * @param tLinkServiceMntVo    ???????????????????????????
     * @param linkId               ????????????id
     * @param tLinkServiceMntList  ????????????????????????
     * @param tLinkServiceMntLists ??????????????????????????????
     * @param linkExist            ??????????????????????????????
     * @throws TakinModuleException
     * @author shulie
     */
    private void addParam(TLinkServiceMntVo tLinkServiceMntVo,
                          String linkId,
                          List<TLinkServiceMnt> tLinkServiceMntList,
                          List<TLinkServiceMnt> tLinkServiceMntLists,
                          String linkExist) throws TakinModuleException {
        if (tLinkServiceMntList.isEmpty()) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_LINK_PARAM_EXCEPTION);
        }

        tLinkServiceMntList.forEach(tLinkServiceMnt -> {
            if (StringUtils.isNotEmpty(linkId)) {
                int linkInterfaceExist = tLinkMnDao.saveLinkInterfaceExist(linkId, tLinkServiceMnt.getInterfaceName());
                if (linkInterfaceExist == 0) {
                    tLinkServiceMnt.setLinkServiceId(snowflake.next());
                    tLinkServiceMnt.setLinkId(Long.valueOf(linkId));
                    tLinkServiceMntLists.add(tLinkServiceMnt);
                }
            } else {
                tLinkServiceMnt.setLinkServiceId(snowflake.next());
                tLinkServiceMnt.setLinkId(tLinkServiceMntVo.getLinkId());
                tLinkServiceMntLists.add(tLinkServiceMnt);
            }
        });

        tLinkServiceMntList.clear();
        tLinkServiceMntVo.settLinkServiceMntList(tLinkServiceMntLists);
    }

    /**
     * ??????: ????????????????????????
     *
     * @param paramMap ??????????????????,????????????,????????????,???????????????,????????????
     *                 pageSize???-1???????????????
     * @return ????????????
     * @author shulie
     */
    public PageInfo<TLinkApplicationInterface> queryBasicLinkList(Map<String, Object> paramMap) {

        //FIXME ???????????????????????????
        boolean paginationFlag = StringUtils.equals("-1", MapUtils.getString(paramMap, "pageSize")) ? false : true;
        Page<TLinkApplicationInterface> newPage = new Page<>();
        if (paginationFlag) {
            newPage = PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        }
        List<TLinkApplicationInterface> queryBasicLinkList = tLinkMnDao.queryBasicLinkList(paramMap);
        if (CollectionUtils.isEmpty(queryBasicLinkList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        queryBasicLinkList = queryBasicLinkList.stream()
                //                .filter(tLinkApplicationInterface -> !StringUtils.isAnyEmpty(tLinkApplicationInterface
                //                .getApplicationName(), tLinkApplicationInterface.getInterfaceName()))
                .peek(tLinkApplicationInterface -> {
                    List<List<Map<String, Object>>> basicLinkList = getRelationLinkRelationShip("t_bs_tch_link",
                            String.valueOf(tLinkApplicationInterface.getLinkId()));
                    tLinkApplicationInterface.setTechLinks(JSON.toJSONString(basicLinkList));
                })
                .collect(Collectors.toList());
        newPage.clear();
        newPage.addAll(queryBasicLinkList);

        return new PageInfo<TLinkApplicationInterface>(newPage);
    }

    public List<TLinkServiceMntVo> queryBasicLinkListDownload(Map<String, Object> paramMap) {

        List<TLinkServiceMntVo> tLinkServiceMntVos = tLinkMnDao.queryBasicLinkListDownload(paramMap);
        for (TLinkServiceMntVo tLinkServiceMntVo : tLinkServiceMntVos) {
            List<TLinkServiceMnt> tLinkServiceMnts = tLinkMntDao.queryLinkInterface(
                    String.valueOf(tLinkServiceMntVo.getLinkId()));
            tLinkServiceMntVo.settLinkServiceMntList(tLinkServiceMnts);
        }

        return tLinkServiceMntVos;
    }

    /**
     * linkName??????APPID
     *
     * @return
     */
    public String queryAppIdByAppName(Object obj) {

        if (obj == null) {
            return "1";

        }
        String str = String.valueOf(obj);
        String linkName = secondLinkDao.queryAppIdByAppName(str);
        return String.valueOf(linkName);

    }

    /**
     * ??????: ????????????id????????????????????????
     *
     * @param linkId ??????id
     * @return ????????????????????????
     * @author shulie
     */
    public TLinkServiceMntVo queryLinkByLinkId(String linkId) {
        TLinkServiceMntVo tLinkServiceMntVo = tLinkMnDao.queryLinkByLinkId(linkId);
        //????????????
        setTechLinks(tLinkServiceMntVo);
        //????????????
        setNodes(tLinkServiceMntVo);

        return tLinkServiceMntVo;
    }

    private void setTechLinks(TLinkServiceMntVo tLinkServiceMntVo) {
        if (StringUtils.isNotBlank(tLinkServiceMntVo.getTechLinks())) {
            List<List<String>> techLinks = JSON.parseObject(tLinkServiceMntVo.getTechLinks(),
                    new TypeReference<List<List<String>>>() {
                    }); // Json ???List

            List<List<Map<String, Object>>> showTechLinks = Lists.newArrayListWithExpectedSize(techLinks.size());
            techLinks.forEach(lstString -> {
                List<Map<String, Object>> tempObj = tLinkMntDao.transferBusinessLinkNameAndId(lstString);
                ;
                showTechLinks.add(tempObj);
            });
            tLinkServiceMntVo.setTechLinksList(showTechLinks);
        }
    }

    private void setNodes(TLinkServiceMntVo tLinkServiceMntVo) {
        List<TLinkNodesVo> tLinkNodesVoList = tLinkMntDao.getNodesByBlinkId(
                String.valueOf(tLinkServiceMntVo.getLinkId()));
        if (CollectionUtils.isNotEmpty(tLinkNodesVoList)) {
            Map<String, Object> linkNode = Maps.newHashMapWithExpectedSize(tLinkNodesVoList.size());
            Integer maxBlank = tLinkNodesVoList.stream().mapToInt(tempNode -> tempNode.gettLinkBank()).max().getAsInt();
            //??????
            List<Map<String, Object>> nodeList = Lists.newArrayListWithExpectedSize(tLinkNodesVoList.size() + 1);
            //?????????
            List<Map<String, Object>> linksList = Lists.newArrayListWithExpectedSize(tLinkNodesVoList.size() + 1);
            Integer parentKey = 10001;
            Map<String, Object> parentNode = Maps.newHashMapWithExpectedSize(6);
            parentNode.put("bank", "0");
            parentNode.put("x", "1");
            parentNode.put("y",
                    maxBlank % 2 == 0 ? String.valueOf(maxBlank / 2 + 0.5) : String.valueOf(maxBlank / 2 + 1));
            parentNode.put("text", tLinkServiceMntVo.getLinkName());
            parentNode.put("baseLinkId", String.valueOf(tLinkServiceMntVo.getLinkId()));
            parentNode.put("key", String.valueOf(parentKey));

            nodeList.add(parentNode);
            tLinkNodesVoList.stream().forEach(nodeVo -> {
                Map<String, Object> tempLink = Maps.newHashMapWithExpectedSize(4);
                if (1 == nodeVo.gettLinkOrder().intValue()) {
                    tempLink.put("from", String.valueOf(parentKey));
                    tempLink.put("source", tLinkServiceMntVo.getLinkName());
                } else {
                    tempLink.put("from",
                            String.valueOf(parentKey + nodeVo.gettLinkBank() * 10000 + nodeVo.gettLinkOrder() - 1));
                    tempLink.put("source", tLinkNodesVoList.stream().filter(
                            s -> s.gettLinkBank().equals(nodeVo.gettLinkBank()) && s.gettLinkOrder()
                                    .equals(nodeVo.gettLinkOrder() - 1)).findFirst().get().getLinkName());
                }
                tempLink.put("to", String.valueOf(parentKey + nodeVo.gettLinkBank() * 10000 + nodeVo.gettLinkOrder()));
                tempLink.put("target", nodeVo.getLinkName());
                linksList.add(tempLink);
                Map<String, Object> childNode = Maps.newHashMapWithExpectedSize(6);
                childNode.put("bank", String.valueOf(nodeVo.gettLinkBank()));
                childNode.put("x", String.valueOf(1 + nodeVo.gettLinkOrder()));
                childNode.put("y", String.valueOf(nodeVo.gettLinkBank()));
                childNode.put("text", nodeVo.getLinkName());
                childNode.put("techLinkId", String.valueOf(nodeVo.gettLinkId()));
                childNode.put("key",
                        String.valueOf(parentKey + nodeVo.gettLinkBank() * 10000 + nodeVo.gettLinkOrder()));
                nodeList.add(childNode);
            });
            tLinkServiceMntVo.setLinks(linksList);
            tLinkServiceMntVo.setNodes(nodeList);
        }
    }

    /**
     * ??????: ????????????????????????
     *
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public String deleteLinkByLinkIds(String basicLinkIds) {
        GetDeleteIds deleteIds = new GetDeleteIds(basicLinkIds, "basicLinkName").invoke(tLinkMnDao);
        List<String> ableDeleteBasicLink = deleteIds.getAbleDeleteList();
        if (!ableDeleteBasicLink.isEmpty()) {
            tLinkMnDao.deleteLinkByLinkIds(ableDeleteBasicLink);
            // ?????????????????????????????????????????????
            tLinkMnDao.deleteBTLinkRelationShip(ableDeleteBasicLink);
            tLinkMnDao.deleteLinkInterfaceByLinkIds(ableDeleteBasicLink);
            //???????????????????????????/??????????????????
            tLinkMnDao.deleteSecondBasicLinkRef(Splitter.on(",").trimResults().omitEmptyStrings()
                    .splitToList(basicLinkIds));
            List<TLinkServiceMntVo> tLinkServiceMntVos = tLinkMnDao.queryLinksByLinkIds(ableDeleteBasicLink);

        }
        return deleteIds.getResult();
    }

    /**
     * ??????: ??????????????????????????????
     * query/bwlistmetric
     *
     * @param linkServiceIds ?????????????????????id
     * @author shulie
     */
    public void deleteLinkInterfaceByLinkServiceId(String linkServiceIds) {
        List<String> linkServiceIdsList = Arrays.stream(linkServiceIds.split(",")).filter(
                e -> StringUtils.isNotEmpty(e)).distinct().collect(Collectors.toList());
        List<TLinkServiceMntVo> tLinkServiceMntVos = tLinkMnDao.queryLinksByLinkIds(linkServiceIdsList);

        tLinkMnDao.deleteLinkInterfaceByLinkServiceId(linkServiceIdsList);
    }

    /**
     * ??????: ??????id????????????????????????
     *
     * @param tLinkServiceMntVo ???????????????????????????
     * @throws TakinModuleException
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLinkinfo(TLinkServiceMntVo tLinkServiceMntVo) throws TakinModuleException {

        if (StringUtils.isNotEmpty(tLinkServiceMntVo.getLinkServiceIds())) {
            deleteLinkInterfaceByLinkServiceId(tLinkServiceMntVo.getLinkServiceIds());
        }

        long linkId = tLinkServiceMntVo.getLinkId();
        int linkExist = tLinkMnDao.updateLinkExist(String.valueOf(linkId));
        // ???????????????????????????
        List<TLinkServiceMnt> tLinkServiceMntList = tLinkServiceMntVo.gettLinkServiceMntList();
        List<TLinkServiceMnt> saveServiceMntLists = Lists.newArrayList();
        List<TLinkServiceMnt> updateServiceMntLists = Lists.newArrayList();
        if (linkExist > 0) {
            //??????
            if (tLinkServiceMntList != null && !tLinkServiceMntList.isEmpty()) {
                tLinkServiceMntList.forEach(tLinkServiceMnt -> {
                    int linkInterfaceExist = tLinkMnDao.updateLinkInterfaceExist(String.valueOf(linkId),
                            String.valueOf(tLinkServiceMnt.getLinkServiceId()));
                    if (linkInterfaceExist == 0) {
                        tLinkServiceMnt.setLinkServiceId(snowflake.next());
                        tLinkServiceMnt.setLinkId(linkId);
                        saveServiceMntLists.add(tLinkServiceMnt);

                    } else {
                        //??????
                        updateServiceMntLists.add(tLinkServiceMnt);
                    }
                });
            }
            tLinkMnDao.updateLink(tLinkServiceMntVo);

            // ==============================Start ??????????????????????????????????????? Start==============================
            // ???????????????????????????????????????
            // ?????????????????????????????????
            tLinkMnDao.deleteReLationShipByTLinkId(Collections.singletonList(String.valueOf(linkId)));
            saveRelationLink(linkId, tLinkServiceMntVo.getTechLinks(), "t_bs_tch_link");
            // ==============================End ??????????????????????????????????????? End==============================

            if (!saveServiceMntLists.isEmpty()) {
                //?????????????????????????????????
                tLinkMnDao.addLinkInterface(saveServiceMntLists);

            }

            if (!updateServiceMntLists.isEmpty()) {

                for (TLinkServiceMnt mnt : updateServiceMntLists) {
                    List<TLinkServiceMnt> updateServiceMntLists2 = Lists.newArrayList();
                    updateServiceMntLists2.add(mnt);
                    LOGGER.info("update object:" + org.apache.commons.lang3.builder.ToStringBuilder
                            .reflectionToString(mnt, org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE));

                    tLinkMnDao.updateLinkInterface(updateServiceMntLists2);
                }

            }//??????????????????????????????
        } else {
            //?????????????????????????????????????????????
            tLinkServiceMntVo.setLinkId(snowflake.next());
            tLinkMnDao.addBasicLink(tLinkServiceMntVo);

            addParam(tLinkServiceMntVo, String.valueOf(linkId), tLinkServiceMntList, saveServiceMntLists,
                    String.valueOf(linkExist));
            tLinkMnDao.addLinkInterface(saveServiceMntLists);

        }
    }

    /**
     * ??????:
     * ??????:??????id????????????????????????
     *
     * @param tLinkServiceMntVo ???????????????????????????
     * @throws TakinModuleException
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLinkInfo(TLinkServiceMntVo tLinkServiceMntVo) throws TakinModuleException {
        //???????????????????????????,?????????????????????
        if (StringUtils.isNotEmpty(tLinkServiceMntVo.getLinkServiceIds())) {
            deleteLinkInterfaceByLinkServiceId(tLinkServiceMntVo.getLinkServiceIds());
        }

        long linkId = tLinkServiceMntVo.getLinkId();
        int linkExist = tLinkMnDao.updateLinkExist(String.valueOf(linkId));
        // ???????????????????????????
        List<TLinkServiceMnt> tLinkServiceMntList = tLinkServiceMntVo.gettLinkServiceMntList();
        List<TLinkServiceMnt> saveServiceMntLists = Lists.newArrayList();
        List<TLinkServiceMnt> updateServiceMntLists = Lists.newArrayList();
        if (linkExist > 0) {
            //??????
            if (tLinkServiceMntList != null && !tLinkServiceMntList.isEmpty()) {
                tLinkServiceMntList.forEach(tLinkServiceMnt -> {
                    int linkInterfaceExist = tLinkMnDao.updateLinkInterfaceExist(String.valueOf(linkId),
                            String.valueOf(tLinkServiceMnt.getLinkServiceId()));
                    if (linkInterfaceExist == 0) {
                        tLinkServiceMnt.setLinkServiceId(snowflake.next());
                        tLinkServiceMnt.setLinkId(linkId);
                        saveServiceMntLists.add(tLinkServiceMnt);

                    } else {
                        //??????
                        updateServiceMntLists.add(tLinkServiceMnt);
                    }
                });
            }
            tLinkMnDao.updateLink(tLinkServiceMntVo);
            //++ ????????????????????????????????????
            //???????????????????????????(????????????????????????)?????????,
            int existSecondLinkRef = tLinkMnDao.existSecondLinkRef(tLinkServiceMntVo.getSecondLinkId(),
                    String.valueOf(tLinkServiceMntVo.getLinkId()));
            if (existSecondLinkRef > 0) {
                tLinkMnDao.updateSecondLinkRef(tLinkServiceMntVo.getSecondLinkId(),
                        String.valueOf(tLinkServiceMntVo.getLinkId()));
            } else {
                tLinkMnDao.addSecondLinkRef(tLinkServiceMntVo.getSecondLinkId(),
                        String.valueOf(tLinkServiceMntVo.getLinkId()));
            }

            if (!saveServiceMntLists.isEmpty()) {
                //?????????????????????????????????
                tLinkMnDao.addLinkInterface(saveServiceMntLists);

            }

            if (!updateServiceMntLists.isEmpty()) {

                for (TLinkServiceMnt mnt : updateServiceMntLists) {
                    List<TLinkServiceMnt> updateServiceMntLists2 = Lists.newArrayList();
                    updateServiceMntLists2.add(mnt);
                    LOGGER.info("update object:" + org.apache.commons.lang3.builder.ToStringBuilder
                            .reflectionToString(mnt, org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE));

                    tLinkMnDao.updateLinkInterface(updateServiceMntLists2);
                }

            }//??????????????????????????????
        } else {
            //?????????????????????????????????????????????
            Map<String, Object> selectLinkId = tLinkMnDao.selectLinkId(tLinkServiceMntVo.getLinkName());
            Long saveLinkId = MapUtils.getLong(selectLinkId, "LINK_ID");
            String valueOf = String.valueOf(saveLinkId == null ? "" : linkId);
            if (StringUtils.isNotEmpty(valueOf)) {
                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_LINK_EXIST);
            }
            tLinkServiceMntVo.setLinkId(snowflake.next());
            saveLink(tLinkServiceMntVo);
            tLinkMnDao.addBasicLink(tLinkServiceMntVo);

            addParam(tLinkServiceMntVo, String.valueOf(linkId), tLinkServiceMntList, saveServiceMntLists,
                    String.valueOf(linkExist));
            tLinkMnDao.addLinkInterface(saveServiceMntLists);

        }
    }

    /**
     * ??????: query application - ip by application name and type
     *
     * @param applicationName ????????????
     * @return ????????????
     * @author shulie
     */
    public List<TApplicationIp> queryApplicationIpByNameTypeList(String applicationName, String type) {
        return tApplicationIpDao.queryApplicationIpByNameTypeList(applicationName, type);
    }

    /**
     * ??????: query application - ip by application name and type
     *
     * @param applicationName ????????????
     * @return ????????????
     * @author shulie
     */
    public List<TApplicationIp> queryApplicationIpByNameList(String applicationName) {
        List<TApplicationIp> tApplicationIps = tApplicationIpDao.queryApplicationIpByNameList(applicationName);
        return tApplicationIps;
    }

    /**
     * ??????: ??????????????????
     *
     * @param tAlarm ???????????????
     * @author shulie
     */
    public void addAlarm(TAlarm tAlarm) throws TakinModuleException {

        List<TApplicationIp> tApplicationIps = tApplicationIpDao.queryApplicationIpByIpList(tAlarm.getIp());
        if (CollectionUtils.isEmpty(tApplicationIps)) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_IP_NOTEXISTAPP_EXCEPTION);
        }

        for (TApplicationIp tApplicationIp : tApplicationIps) {
            tAlarm.setId(null);
            tAlarm.setWarPackages(tApplicationIp.getApplicationName());
            Result<Void> add = tAlarmService.add(tAlarm);

            if (!add.isSuccess()) {
                TakinModuleException takinModuleException = new TakinModuleException(add.getMessage());

                throw takinModuleException;
            }
        }
    }

    public List<TLinkServiceMntVo> queryLinkMntListByIds(List<String> ids) {
        return tLinkMnDao.queryLinksByLinkIds(ids);
    }

    /**
     * ??????:  ??????appname??????????????????????????????
     *
     * @author shulie
     * @date 2019/3/1 14:48
     */
    public PageInfo<?> queryWhiteListByAppName(Map<String, Object> paramMap) {
        String type = MapUtils.getString(paramMap, "type");
        String interfaceName = MapUtils.getString(paramMap, "interfaceName");
        String applicationName = MapUtils.getString(paramMap, "applicationName");
        String pageNum = MapUtils.getString(paramMap, "pageNum");
        String pageSize = MapUtils.getString(paramMap, "pageSize");
        // ?????????????????????
        if ("1".equals(type) && StringUtils.isEmpty(interfaceName)) {

            RedisTemplate redisTemplate = redisManager.getRedisTemplate();
            String pradaSynchronizedToRedisStatus = (String) redisTemplate.opsForValue().get("pradaSynchronizedToRedis");
            if ("Success".equals(pradaSynchronizedToRedisStatus)) {
                Long total = redisTemplate.opsForList().size(applicationName);
                int pageNumN = Integer.parseInt(pageNum);
                int pageSizeN = Integer.parseInt(pageSize);
                int start = (pageNumN - 1) * pageSizeN;
                int end = pageNumN * pageSizeN;

                List<String> list = null;
                if (StringUtils.isEmpty(applicationName)) {
                    list = redisTemplate.opsForList().range("allUrlList", start, end - 1);
                } else {
                    list = redisTemplate.opsForList().range(applicationName, start, end - 1);
                }

                List<Object> returnList = Lists.newArrayListWithCapacity(200);
                // ????????????
                list.forEach(url -> {
                    Map<String, String> objectObjectHashMap = Maps.newHashMapWithExpectedSize(1);
                    objectObjectHashMap.put("interfaceName", url);
                    returnList.add(objectObjectHashMap);
                });

                PageInfo<Object> pageInfo = new PageInfo<>(returnList.isEmpty() ? Lists.newArrayList() : returnList);
                pageInfo.setPageNum(pageNumN);
                pageInfo.setPageSize(pageSizeN);
                pageInfo.setTotal(total);
                return pageInfo;
            }
        }
        // ????????????????????????
        //??????
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        switch (type) {
            case "1":
                List<TPradaHttpData> tPradaHttpData = whiteListDAO.queryInterfaceByAppNameByTPHD(applicationName, type,
                        interfaceName);
                return new PageInfo<>(tPradaHttpData.isEmpty() ? Lists.newArrayList() : tPradaHttpData);
            case "2":
                List<TUploadInterfaceDataVo> dubboData = whiteListDAO.queryInterfaceByAppNameFromTUID(applicationName,
                        type, interfaceName);
                return new PageInfo<>(dubboData.isEmpty() ? Lists.newArrayList() : dubboData);
            case "4":
                List<TUploadInterfaceDataVo> jobData = whiteListDAO.queryInterfaceByAppNameFromTUID(applicationName,
                        type, interfaceName);
                return new PageInfo<>(jobData.isEmpty() ? Lists.newArrayList() : jobData);
            default:
                break;
        }
        return new PageInfo(Lists.newArrayList());
    }

    /**
     * ??????: ?????????????????????
     *
     * @param list ??????????????????
     * @return ?????????????????????????????????
     * @author shulie
     * @date 2019/3/1 15:11
     */
    public List<String> filterWhiteList(String appName, List<String> list) {

        //??????ip  /vas-cas-web/login;JSESSIONID=    NVAS-vas-cas-web
        //http://vip.pamirs.com:8080/vas-cas-web/login;JSESSIONID= NVAS-vas-cas-web
        //http://cubc.pamirs.com/cubc/codaccountmanage/queryModifyParamsById ???????????????  cubc
        //http://dpjjwms.pamirs.com/dpjjwms/ ??????.cache.html wms-dpjjwms
        List<String> filterList = Lists.newArrayListWithCapacity(4);
        if (!WListRuleEnum.getAppNameList().contains(appName)) {
            return list;
        }

        List<String> numberDomainList = Lists.newArrayListWithCapacity(200);
        List<String> domainList = Lists.newArrayListWithCapacity(200);
        List<String> cubcList = Lists.newArrayListWithCapacity(200);
        List<String> wmsDpjjwmsList = Lists.newArrayListWithCapacity(200);
        for (String info : list) {
            if (info.contains(WListRuleEnum.CUBC.getRule()) && NumberUtils.isDigits(
                    StringUtils.substringAfterLast(info, "/"))) {
                cubcList.add(info);
                continue;
            }
            if (info.contains(WListRuleEnum.WMS_DPJJWMS.getRule()) && info.endsWith(".cache.html")) {
                wmsDpjjwmsList.add(info);
                continue;
            }
            String domain = StringUtils.substringBetween(info, "//", "/");
            String domainNumber = domain.replaceAll("\\.", "")
                    .replaceAll(":", "");
            boolean numberDomain = NumberUtils.isDigits(domainNumber);
            if (numberDomain && info.contains(WListRuleEnum.NVAS_VAS_CAS_WEB.getRule())) {
                numberDomainList.add(info);
            }

            if (!numberDomain && info.contains(WListRuleEnum.VIP_NVAS_VAS_CAS_WEB.getRule())) {
                domainList.add(info);
            }
        }

        if (CollectionUtils.isNotEmpty(numberDomainList)) {
            filterList.add(numberDomainList.get(0));
        }
        if (CollectionUtils.isNotEmpty(domainList)) {
            filterList.add(domainList.get(0));
        }
        if (CollectionUtils.isNotEmpty(cubcList)) {
            filterList.add(cubcList.get(0));
        }
        if (CollectionUtils.isNotEmpty(wmsDpjjwmsList)) {
            filterList.add(wmsDpjjwmsList.get(0));
        }
        return filterList;
    }


    /**
     * ?????????????????????
     *
     * @param applicationId ??????ID
     * @return
     */
    public List<Map<String, Object>> queryWhiteListByAppId(String applicationId) {
        return whiteListDAO.queryWhiteListByAppId(applicationId);

    }

    /**
     * ????????????????????????
     * ??????????????????@link LinkManageExcelVo
     *
     * @return
     * @throws IOException
     * @throws TakinModuleException
     */
    public void batchUploadLinkList(MultipartFile[] files) throws IOException, TakinModuleException {
        for (MultipartFile file : files) {
            //??????????????????Sheet????????????????????????
            Sheet sheet = new Sheet(1, 1);
            InputStream stream = new BufferedInputStream(file.getInputStream());
            List<Object> lists = EasyExcelFactory.read(stream, sheet);

            List<TLinkServiceMntVo> addList = new ArrayList<>();
            List<TLinkServiceMntVo> updateList = new ArrayList<>();

            int index = 0;
            for (Object list : lists) {
                ArrayList inVo = (ArrayList) list;
                TLinkServiceMntVo vo = new TLinkServiceMntVo();
                vo.setAswanId(String.valueOf(inVo.get(0)));
                List<JSONObject> mnts = JSONObject.parseObject((String) inVo.get(1), List.class);
                List<TLinkServiceMnt> tLinkServiceMntList = new ArrayList<>(mnts.size());
                StringBuffer sb = new StringBuffer();
                mnts.forEach(mnt -> {
                    TLinkServiceMnt nt = new TLinkServiceMnt();
                    nt.setInterfaceName(String.valueOf(mnt.get("????????????")));
                    nt.setInterfaceDesc(String.valueOf(mnt.get("????????????")));
                    String linkServiceId = mnt.getString("????????????id");
                    nt.setLinkServiceId(Long.parseLong(linkServiceId));
                    tLinkServiceMntList.add(nt);
                    sb.append(linkServiceId).append(",");

                });

                vo.setLinkServiceIds(sb.toString().substring(0, sb.length() - 1));
                vo.settLinkServiceMntList(tLinkServiceMntList);

                vo.setLinkDesc(String.valueOf(inVo.get(2)));
                vo.setLinkEntrence(String.valueOf(inVo.get(3)));
                vo.setLinkModule("1");
                vo.setLinkName(String.valueOf(inVo.get(4)));
                vo.setLinkRank("1");
                vo.setLinkType("1");
                vo.setPrincipalNo("000000");
                vo.setRt(String.valueOf(inVo.get(6)));
                vo.setRtSa(String.valueOf(inVo.get(7)));
                String secondLinkName = String.valueOf(inVo.get(8));
                if ("null".equalsIgnoreCase(secondLinkName)) {
                    throw new TakinModuleException(TakinErrorEnum.CONFCENTER_NOT_ALLOW_EMPTY + "??????" + index++ + "?????????");
                }

                String secondlinkId = secondLinkDao.queryAppIdByAppName(secondLinkName);
                vo.setSecondLinkId(secondlinkId);
                vo.setTargetSuccessRate(String.valueOf(inVo.get(9)));
                vo.setTargetTps(String.valueOf(inVo.get(10)));
                vo.setUseYn("??????".equalsIgnoreCase(String.valueOf(inVo.get(11))) ? 1 : 0);
                vo.setVolumeCalcStatus("1");
                //????????????id
                String linkid = ObjectUtils.toString(inVo.get(12));
                //??????tps
                vo.setTps(String.valueOf(inVo.get(13)));

                if (StringUtils.isBlank(linkid)) {
                    addList.add(vo);
                } else {
                    vo.setLinkId(Long.parseLong(linkid));
                    updateList.add(vo);
                }
            }

            if (addList.size() > 0) {
                for (TLinkServiceMntVo vo : addList) {
                    this.saveLink(vo);
                }
            }
            if (updateList.size() > 0) {
                for (TLinkServiceMntVo vo : updateList) {
                    this.updateLinkInfo(vo);
                }
            }
        }
    }

    private String getKey(TakinDictTypeEnum enums, Object key) {
        String key1 = String.valueOf(key);
        Map data = this.queryDicList(enums);
        Map dataList = (Map) data.get("dicList");
        Iterator iterator = dataList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (StringUtils.equalsIgnoreCase(ObjectUtils.toString(entry.getValue()), key1)) {
                return String.valueOf(entry.getKey());
            }

        }
        return null;

    }

    private String getValue(TakinDictTypeEnum enums, Object value) {
        String key = String.valueOf(value);
        Map data = this.queryDicList(enums);
        Map dataList = (Map) data.get("dicList");
        Iterator iterator = dataList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (StringUtils.equalsIgnoreCase(ObjectUtils.toString(entry.getValue()), key)) {
                return String.valueOf(entry.getKey());
            }

        }
        return null;

    }

    /**
     * ?????????????????????
     */
    public void batchUploadWhiteList(MultipartFile[] files) throws IOException, TakinModuleException {
        //?????????????????????
        List<Map<String, Object>> applicationDataList = this.queryApplicationdata();
        Set<Object> applicationSet = new HashSet<Object>();
        for (MultipartFile file : files) {
            //???????????????
            applicationDataList.forEach(map -> {
                applicationSet.add(map.get("applicationName"));
            });

            //??????????????????Sheet????????????????????????
            Sheet sheet = new Sheet(1, 1);
            InputStream stream = new BufferedInputStream(file.getInputStream());

            List<Object> lists = EasyExcelFactory.read(stream, sheet);
            for (Object list : lists) {

                TWListVo vo = new TWListVo();
                ArrayList excelVo = (ArrayList) list;
                String applicationName = String.valueOf(excelVo.get(1));
                if (!applicationSet.contains(applicationName)) {
                    throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_APPLICATION_NOTEXIST_EXCEPTION);

                }
                vo.setPrincipalNo("000000");
                Long id = applicationDAO.queryIdByApplicationName(String.valueOf(excelVo.get(1)));
                vo.setApplicationId(String.valueOf(id));
                //???????????????????????????
                WListTypeEnum[] wListTypeEnums = WListTypeEnum.values();
                Arrays.asList(wListTypeEnums).forEach(
                        wListTypeEnum -> {
                            if (StringUtils.equalsIgnoreCase(wListTypeEnum.getName()
                                    , StringUtils.trim(String.valueOf(excelVo.get(2))))) {
                                vo.setType(wListTypeEnum.getValue());
                            }
                        }
                );
                String httpType = isEmPty(excelVo.get(3)) ? null : String.valueOf(excelVo.get(3));
                vo.setHttpType(httpType);
                String jobInterval = isEmPty(excelVo.get(4)) ? null : String.valueOf(excelVo.get(4));
                vo.setJobInterval(jobInterval);
                String mqType = isEmPty(excelVo.get(5)) ? null : String.valueOf(excelVo.get(5));
                vo.setMqType(mqType);
                String useYn = String.valueOf(excelVo.get(6)).equalsIgnoreCase("??????") ? "1" : "0";
                vo.setUseYn(useYn);
                String interfaceName = isEmPty(excelVo.get(7)) ? null : String.valueOf(excelVo.get(7));
                if (StringUtils.isNotBlank(interfaceName)) {
                    String[] strings = interfaceName.split(",");
                    vo.setList(Arrays.asList(strings));
                }
                vo.setDictType("ca888ed801664c81815d8c4f5b8dff0c");
                //http????????????
                String pageLevel = isEmPty(excelVo.get(8)) ? null : String.valueOf(excelVo.get(8));
                vo.setPageLevel(pageLevel(pageLevel));
                //????????????
                String queueName = isEmPty(excelVo.get(9)) ? null : String.valueOf(excelVo.get(9));
                vo.setQueueName(queueName);

                //?????????id
                String twlist = isEmPty(excelVo.get(10)) ? null : String.valueOf(excelVo.get(10));
                if (twlist != null) {
                    vo.setWlistId(Long.parseLong(twlist));
                }
                if (StringUtils.isBlank(twlist)) {
                    //??????
                    this.saveWhiteList(vo);
                } else {
                    //??????
                    TWList tWhiteList = new TWList();
                    BeanUtils.copyProperties(vo, tWhiteList);
                    tWhiteList.setInterfaceName(interfaceName);
                    tWhiteList.setQueueName(queueName);
                    tWhiteList.setPageLevel(pageLevel);
                    tWhiteList.setUseYn(useYn);
                    tWhiteList.setMqType(mqType);
                    tWhiteList.setHttpType(httpType);
                    tWhiteList.setJobInterval(jobInterval);
                    this.updateWhiteListById(tWhiteList);
                }

            }
        }
    }

    private boolean isEmPty(Object t) {
        return null == t
                || StringUtils.isBlank(ObjectUtils.toString(t))
                || "null".equalsIgnoreCase(ObjectUtils.toString(t));
    }

    private String pageLevel(String pageLevel) {
        if (pageLevel == null) {
            return null;
        }
        pageLevel = pageLevel.trim();
        switch (pageLevel) {
            case "??????????????????":
                return "1";
            case "??????????????????/????????????":
                return "2";
            case "??????????????????":
                return "3";
            default:
                return null;
        }
    }

    public List<TLinkBasicVO> queryLinkByLinkType(Map<String, Object> queryMap) {
        List<Integer> linkTypeList = new ArrayList<>();
        if (queryMap != null && queryMap.get("linkType") != null) {
            String linkTypeString = (String) queryMap.get("linkType");
            if (StringUtils.isNotEmpty(linkTypeString)) {
                linkTypeList = Arrays.asList(linkTypeString.split(",")).stream().map(s -> Integer.parseInt(s.trim()))
                        .collect(Collectors.toList());
            }
        }
        return tLinkMntDao.queryLinksByLinkType(linkTypeList);
    }

    /**
     * ???????????? agentVersion ??????
     *
     * @param appName       ?????????
     * @param agentVersion  ??????agent??????
     * @param pradarVersion Pradar Agent??????
     * @throws TakinModuleException
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateAppAgentVersion(String appName, String agentVersion, String pradarVersion) throws TakinModuleException {
        // ????????????
        if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.AGENT_HTTP_UPDATE_VERSION)) {
            return;
        }

        if (StringUtils.isEmpty(appName)) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_UPDATE_APPLICATION_AGENT_VERSION_EXCEPTION);
        }
        Long applicationId = applicationService.queryApplicationIdByAppName(appName);
        if (applicationId == null) {
            log.warn("??????????????????????????????, updateAppAgentVersion fail!");
            return;
        }
        applicationDAO.updateApplicationAgentVersion(applicationId, agentVersion, pradarVersion);
    }

    /**
     * ???????????????
     *
     * @return
     */
    public List<Map<String, String>> queryLinkIdName() {
        return tLinkMnDao.queryLinkIdName();
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    public List<Map<String, String>> getWhiteListForLink() {
        return whiteListDAO.getWhiteListForLink();
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @Deprecated
    public List<Map<String, Object>> queryLinkHeaderList() {
        /**
         * 1,??????????????????????????????????????????
         * 2,????????????????????????
         */
        List<Map<String, Object>> resultList = Lists.newArrayList();
        List<TLinkMntDictoryVo> linkHeaderInfo = tLinkMnDao.queryLinkHeaderInfo();
        Map<String, List<TLinkMntDictoryVo>> collect = new HashMap<>(10);
        /**
         * ?????????????????????;????????????????????????
         */
        for (TLinkMntDictoryVo tLinkMntDictoryVo : linkHeaderInfo) {
            collect.computeIfAbsent(tLinkMntDictoryVo.getName() + ";" + tLinkMntDictoryVo.getOrder(),
                    k -> new ArrayList<>()).add(tLinkMntDictoryVo);
        }
        collect.forEach((nameOrder, tLinkMntDictoryVoList) -> {
            Map<String, Object> resultMap = Maps.newHashMap();
            String[] nameOrderArr = nameOrder.split(";");
            resultMap.put("name", nameOrderArr[0]);
            resultMap.put("order", nameOrderArr[1]);
            /**
             * ????????????????????????????????????????????????????????????1???????????????tLinkMntDictoryVoList???size==1,
             *  ????????????????????????
             *  ?????????????????????tLinkMntDictoryVoList.size()???????????????????????????????????????
             */
            String linkName = tLinkMntDictoryVoList.get(0).getLinkName();
            if (StringUtils.isEmpty(linkName)) {
                resultMap.put("count", 0);
            } else {
                resultMap.put("count", tLinkMntDictoryVoList.size());
            }
            //??????calcVolumeLinkList????????????
            List<Map<String, String>> list = tLinkMnDao.queryCalcVolumeLinkList(nameOrderArr[1]);
            resultMap.put("calcVolumeLinkList", list);
            resultList.add(resultMap);
        });
        Collections.sort(resultList, Comparator.comparingInt(o -> Integer.valueOf((String) o.get("order"))));
        return resultList;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public List<Map<String, Object>> queryLinkHeaderInfoList() {
        /**
         * 1,??????????????????????????????????????????
         * 2,????????????????????????
         */
        List<Map<String, Object>> resultList = Lists.newArrayList();
        List<TLinkTopologyInfoVo> linkHeaderInfo = tLinkMnDao.queryLinkHeaderInfoList();
        Map<String, List<TLinkTopologyInfoVo>> collect = new HashMap<>(16);
        /**
         * ?????????????????????;????????????????????????
         */
        for (TLinkTopologyInfoVo tLinkTopologyInfoVo : linkHeaderInfo) {
            collect.computeIfAbsent(tLinkTopologyInfoVo.getName() + ";" + tLinkTopologyInfoVo.getOrder(),
                    k -> new ArrayList<>()).add(tLinkTopologyInfoVo);
        }
        collect.forEach((nameOrder, tLinkTopologyInfoVoList) -> {
            Map<String, Object> resultMap = Maps.newHashMap();
            String[] nameOrderArr = nameOrder.split(";");
            resultMap.put("name", nameOrderArr[0]);
            resultMap.put("order", nameOrderArr[1]);
            /**
             * ????????????????????????????????????????????????????????????1???????????????tLinkMntDictoryVoList???size==1,
             *  ????????????????????????
             *  ?????????????????????tLinkMntDictoryVoList.size()???????????????????????????????????????
             */
            String linkName = tLinkTopologyInfoVoList.get(0).getLinkName();
            if (StringUtils.isEmpty(linkName)) {
                resultMap.put("count", 0);
            } else {
                resultMap.put("count", tLinkTopologyInfoVoList.size());
            }
            //????????????????????????
            List<Map<String, String>> secondLinkList = Lists.newArrayList();
            Map<String, List<TLinkTopologyInfoVo>> collect1 = new HashMap<>(32);
            for (TLinkTopologyInfoVo tLinkTopologyInfoVo : tLinkTopologyInfoVoList) {
                if (StringUtils.isNotEmpty(tLinkTopologyInfoVo.getSecondLinkId())) {
                    collect1.computeIfAbsent(
                            tLinkTopologyInfoVo.getSecondLinkId() + ";" + tLinkTopologyInfoVo.getSecondLinkName(),
                            k -> new ArrayList<>()).add(tLinkTopologyInfoVo);
                }
            }
            collect1.forEach((secondLinkIdName, secondLinkIdNameList) -> {
                Map<String, String> secondLinkMap = Maps.newHashMap();
                secondLinkMap.put("secondLinkId", secondLinkIdNameList.get(0).getSecondLinkId());
                secondLinkMap.put("secondLinkName", secondLinkIdNameList.get(0).getSecondLinkName());
                secondLinkList.add(secondLinkMap);
            });
            resultMap.put("secondLinkList", secondLinkList);
            //??????calcVolumeLinkList????????????
            List<Map<String, String>> list = tLinkMnDao.queryCalcVolumeLinkListByModule(nameOrderArr[1]);
            resultMap.put("calcVolumeLinkList", list);
            resultList.add(resultMap);
        });
        Collections.sort(resultList, Comparator.comparingInt(o -> Integer.valueOf((String) o.get("order"))));
        return resultList;
    }

    /**
     * ??????: ??????????????????????????????????????????
     *
     * @param linkModule ????????????
     * @return java.util.List<TSecondLinkMnt>
     * @author shulie
     * @create 2019/4/15 17:59
     */
    public List<TSecondLinkMnt> querySecondLinkByModule(String linkModule) {
        return tLinkMnDao.querySecondLinkMapByModule(linkModule);
    }

    // =================================== ???????????????=====================================================

    /**
     * ??????: ?????????????????????
     *
     * @param tBList ??????????????????
     * @throws TakinWebException ??????
     * @author shulie
     */
    public void saveBList(TBList tBList) throws TakinWebException {
        if (StringUtils.isBlank(tBList.getUseYn())) {
            tBList.setUseYn("1");
        }
        int applicationExist = tBListMntDao.bListExist(tBList.getRedisKey());
        if (applicationExist > 0) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_ADD_BLIST_DUPICATE_EXCEPTION, "");
        }
        addBList(tBList);
        writeWhiteListFile();
    }

    /**
     * ??????: ???????????????????????????
     *
     * @param tBList ??????????????????
     * @author shulie
     */
    private void addBList(TBList tBList) {
        BlackListCreateParam param = new BlackListCreateParam();
        param.setRedisKey(tBList.getRedisKey());
        param.setUseYn(Integer.parseInt(tBList.getUseYn()));
        param.setCreateTime(new Date());
        param.setUpdateTime(new Date());
        blackListDAO.insert(param);
        configSyncService.syncBlockList(WebPluginUtils.traceTenantCommonExt());
        whiteListFileService.writeWhiteListFile(WebPluginUtils.traceTenantCommonExt());
    }

    /**
     * ??????: ??????id?????????????????????
     *
     * @return ?????????????????????
     * @author shulie
     */
    public TBList querySingleBListById(String blistId) {
        TBList tbList = tBListMntDao.querySingleBListById(blistId);

        return tbList;
    }

    /**
     * ??????: ??????id?????????????????????
     *
     * @param tBList ??????????????????
     * @author shulie
     */
    public void updateBListById(TBList tBList) {
        TBList originBList = tBListMntDao.querySingleBListById(String.valueOf(tBList.getBlistId()));
        tBListMntDao.updateBListById(tBList);
        whiteListFileService.writeWhiteListFile(WebPluginUtils.traceTenantCommonExt());
    }

    /**
     * ??????: ???????????????????????????
     *
     * @param blistIds ?????????id(??????id???????????????)
     * @author shulie
     */
    @Deprecated
    public void deleteBListByIds(String blistIds) {
        List<String> blistIdList = Arrays.stream(blistIds.split(",")).filter(StringUtils::isNotEmpty).distinct()
                .collect(Collectors.toList());
        tBListMntDao.deleteBListByIds(blistIdList);
        configSyncService.syncBlockList(WebPluginUtils.traceTenantCommonExt());
        whiteListFileService.writeWhiteListFile(WebPluginUtils.traceTenantCommonExt());
    }

    /**
     * ??????: ?????????????????????
     *
     * @param bListQueryParam redis???key??????????????????
     * @return ?????????????????????
     * @author shulie
     */
    public Response queryBList(BListQueryParam bListQueryParam) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageNum", bListQueryParam.getCurrentPage());
        paramMap.put("pageSize", bListQueryParam.getPageSize());
        String redisKey = bListQueryParam.getRedisKey();
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TBList> queryBList = tBListMntDao.queryBList(redisKey, "", WebPluginUtils.getQueryAllowUserIdList());
        if (CollectionUtils.isNotEmpty(queryBList)) {
            for (TBList tbList : queryBList) {
                List<Long> allowUpdateUserIdList = WebPluginUtils.getUpdateAllowUserIdList();
                if (CollectionUtils.isNotEmpty(allowUpdateUserIdList)) {
                    tbList.setCanEdit(allowUpdateUserIdList.contains(tbList.getUserId()));
                }
                List<Long> allowDeleteUserIdList = WebPluginUtils.getDeleteAllowUserIdList();
                if (CollectionUtils.isNotEmpty(allowDeleteUserIdList)) {
                    tbList.setCanRemove(allowDeleteUserIdList.contains(tbList.getUserId()));
                }
                List<Long> allowEnableDisableUserIdList = WebPluginUtils.getEnableDisableAllowUserIdList();
                if (CollectionUtils.isNotEmpty(allowEnableDisableUserIdList)) {
                    tbList.setCanEnableDisable(allowEnableDisableUserIdList.contains(tbList.getUserId()));
                }
            }
        }
        PageInfo<TBList> pageInfo = new PageInfo<>(queryBList.isEmpty() ? Lists.newArrayList() : queryBList);
        Response response = Response.success(pageInfo.getList(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? 0 : pageInfo.getTotal());
        return response;
    }

    public List<TBList> queryBListByIds(List<Long> blistIds) {
        if (CollectionUtils.isEmpty(blistIds)) {
            return Lists.newArrayList();
        }
        List<String> blistIdList = blistIds.stream().map(String::valueOf).collect(Collectors.toList());
        return tBListMntDao.queryBListByIds(blistIdList);
    }


    // =================================== ???????????????=====================================================


    /**
     * ??????: ??????????????????id?????????????????????????????????
     *
     * @author shulie
     * @date 2018/7/10 15:35
     * @return ????????????id???????????????????????????????????????
     */
    private class GetDeleteIds<T> {
        private String ids;
        private String disableName;
        private List<Long> ableDeleteList;
        private String result;

        public GetDeleteIds(String ids, String disableName) {
            this.ids = ids;
            this.disableName = disableName;
        }

        public List<Long> getAbleDeleteList() {
            return ableDeleteList;
        }

        public String getResult() {
            return result;
        }

        public GetDeleteIds invoke(T t) {
            ableDeleteList = Lists.newArrayList();
            StringBuffer sb = new StringBuffer();
            Splitter.on(",").omitEmptyStrings().trimResults().splitToList(ids).stream().distinct().forEach(id -> {
                Map<String, Object> map = null;
                if (t instanceof ApplicationDAO) {
                    map = applicationDAO.queryApplicationRelationBasicLinkByApplicationId(id);
                } else if (t instanceof WhiteListDAO) {
                    map = whiteListDAO.queryWhiteListRelationBasicLinkByWhiteListId(id);
                } else if (t instanceof TLinkMntDao) {
                    map = tLinkMnDao.querySecondLinkRelationBasicLinkByBasicLinkId(id);
                }
                if (Objects.isNull(map)) {
                    ableDeleteList.add(Long.valueOf(id));
                } else {
                    sb.append(MapUtils.getString(map, disableName)).append(",");
                }
            });
            result = StringUtils.substringBeforeLast(sb.toString(), ",");
            return this;
        }
    }

    private void delApplicationCache(String applicationName) {
        redisTemplate.opsForHash().delete(APPLICATION_CACHE_PREFIX, generateApplicationCacheKey(applicationName));
    }

    // ??????????????????key
    public static String generateApplicationCacheKey(String applicationName) {
        return String.format("%s:%s:%s", WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), applicationName);
    }
}
