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
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.dao.confcenter.TLinkMntDao;
import com.pamirs.takin.entity.dao.confcenter.TWhiteListMntDao;
import com.pamirs.takin.entity.domain.entity.TAlarm;
import com.pamirs.takin.entity.domain.entity.TApplicationIp;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.entity.TBList;
import com.pamirs.takin.entity.domain.entity.TLinkServiceMnt;
import com.pamirs.takin.entity.domain.entity.TPradaHttpData;
import com.pamirs.takin.entity.domain.entity.TSecondLinkMnt;
import com.pamirs.takin.entity.domain.entity.TWList;
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
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.biz.service.linkManage.AppRemoteCallService;
import io.shulie.takin.web.biz.service.linkManage.impl.WhiteListFileService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.common.util.whitelist.WhitelistUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationPluginsConfigDAO;
import io.shulie.takin.web.data.dao.blacklist.BlackListDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginsConfigEntity;
import io.shulie.takin.web.data.param.application.ApplicationCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;
import io.shulie.takin.web.data.param.blacklist.BlackListCreateParam;
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
 * 说明: 配置中心service
 *
 * @author shulie
 * @date 2019/4/4 16:36
 * @see
 */
@Service
@Slf4j
public class ConfCenterService extends CommonService {

    private Integer number;

    private String whiteListPath;

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

    @PostConstruct
    public void init() {
        number = Integer.valueOf(ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_WHITE_LIST_NUMBER_LIMIT));
        whiteListPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_WHITE_LIST_CONFIG_PATH);
    }

    /**
     * 说明: 先校验该应用是否已经存在,不存在则进行添加,存在则返回提示信息
     *
     * @throws TakinModuleException 异常错误信息
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveApplication(TApplicationMnt tApplicationMnt) throws TakinModuleException {
        int applicationExist = tApplicationMntDao.applicationExistByCustomerIdAndAppName(
            WebPluginUtils.traceTenantId(), tApplicationMnt.getApplicationName());
        if (applicationExist > 0) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_APPLICATION_DUPICATE_EXCEPTION);
        }
        addApplication(tApplicationMnt);
        addApplicationToDataBuild(tApplicationMnt);
        addApplicationToLinkDetection(tApplicationMnt);
        addPluginsConfig(tApplicationMnt);//插件管理-添加影子key默认过期时间
    }

    private void addPluginsConfig(TApplicationMnt applicationMnt) {
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
        param.setConfigItem("redis影子key有效期");
        param.setConfigKey("redis_expire");
        param.setConfigDesc("可自定义设置redis影子key有效期，默认与业务key有效期一致。若设置时间比业务key有效期长，不生效，仍以业务key有效期为准。");
        param.setConfigValue("-1");
        param.setApplicationName(applicationMnt.getApplicationName());
        param.setApplicationId(applicationMnt.getApplicationId());
        param.setCustomerId(applicationMnt.getCustomerId());
        param.setUserId(applicationMnt.getUserId());
        pluginsConfigService.add(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAgentRegisteApplication(TApplicationMnt tApplicationMnt) {
        int applicationExist = tApplicationMntDao.applicationExistByCustomerIdAndAppName(WebPluginUtils.traceTenantId(), tApplicationMnt.getApplicationName());
        if (applicationExist > 0) {
            OperationLogContextHolder.ignoreLog();
            return;
        }
        addApplication(tApplicationMnt);
        addApplicationToDataBuild(tApplicationMnt);
        addApplicationToLinkDetection(tApplicationMnt);
        //应用自动上报需要设置插件管理的redis影子key默认值
        addPluginsConfig(tApplicationMnt);

    }

    /**
     * 说明: 新增应用时需要插入链路监控表
     *
     * @author shulie
     */
    private void addApplicationToLinkDetection(TApplicationMnt tApplicationMnt) {
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
     * 说明: 新增应用时需要插入数据构建表
     *
     * @param tApplicationMnt 应用实体对象
     * @author shulie
     */
    private void addApplicationToDataBuild(TApplicationMnt tApplicationMnt) {
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
     * 说明: 添加应用信息
     *
     * @param tApplicationMnt 应用管理实体类
     * @author shulie
     */
    private void addApplication(TApplicationMnt tApplicationMnt) {
        tApplicationMnt.setApplicationId(snowflake.next());
        tApplicationMnt.setCacheExpTime(
            StringUtils.isEmpty(tApplicationMnt.getCacheExpTime()) ? "0" : tApplicationMnt.getCacheExpTime());
        ApplicationCreateParam createParam = new ApplicationCreateParam();
        BeanUtils.copyProperties(tApplicationMnt, createParam);
        applicationDAO.insert(createParam);

        try {
            TakinFileUtil.createFile(getBasePath() + tApplicationMnt.getApplicationName());
        } catch (Throwable throwable) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_CONFIG_FILE_CREATE_ERROR, "文件创建失败！");
        }

    }

    /**
     * 说明: 查询白名单列表
     *
     * @param paramMap 参数集合
     * @return 白名单列表
     * @author shulie
     */
    public PageInfo<TApplicationInterface> queryWhiteList(Map<String, Object> paramMap) {
        String applicationName = MapUtils.getString(paramMap, "applicationName");
        String principalNo = MapUtils.getString(paramMap, "principalNo");
        String type = MapUtils.getString(paramMap, "type");
        String whiteListUrl = MapUtils.getString(paramMap, "whiteListUrl");
        Long applicationId = MapUtils.getLong(paramMap, "applicationId");
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TApplicationInterface> queryWhiteListInfo = tWListMntDao.queryOnlyWhiteList(applicationName, principalNo, type,
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
            applicationIds = (List)whiteListIds;
        }
        return tWListMntDao.queryOnlyWhiteList(applicationName, principalNo, type, whiteListUrl, applicationIds, null);
    }

    /**
     * 项目压测开关
     */
    public void projectPressureSwitch(Long applicationId) {
        // tWListMntDao.updateWListById();
    }

    /**
     * 说明: 查询应用列表信息
     *
     * @param paramMap 参数集合
     * @return 应用列表
     * @author shulie
     */
    public PageInfo<TApplicationMnt> queryApplicationList(Map<String, Object> paramMap) {
        String applicationName = MapUtils.getString(paramMap, "applicationName");
        List<String> applicationIds = (List<String>)MapUtils.getObject(paramMap, "applicationIds");
        if (!StringUtils.equals("-1", MapUtils.getString(paramMap, "pageSize"))) {
            PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        }
        List<TApplicationMnt> queryApplicationList = tApplicationMntDao.queryApplicationList(applicationName,
            applicationIds);

        return new PageInfo<>(queryApplicationList.isEmpty() ? Lists.newArrayList() : queryApplicationList);
    }

    /**
     * 说明: 根据应用id查询应用信息接口
     *
     * @param applicationId 应用id
     * @return 应用对象
     * @author shulie
     */
    public TApplicationMnt queryApplicationinfoById(long applicationId) {
        return tApplicationMntDao.queryApplicationinfoById(applicationId);
    }

    /**
     * 说明: 根据应用id和角色查询应用信息接口
     *
     * @param applicationId 应用id
     * @return 应用对象
     * @author JasonYan
     */
    public TApplicationMnt queryApplicationinfoByIdAndRole(long applicationId) {
        TApplicationMnt tApplicationMnt = tApplicationMntDao.queryApplicationinfoById(applicationId);
        return tApplicationMnt;
    }

    /**
     * 说明: 根据应用id删除应用信息接口(删除应用下的服务)
     *
     * @param applicationIds 应用ids
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public String deleteApplicationinfoByIds(String applicationIds) {
        GetDeleteIds deleteIds = new GetDeleteIds(applicationIds, "applicationName").invoke(tApplicationMntDao);
        List<String> ableDeleteApplicationList = deleteIds.getAbleDeleteList();
        if (!ableDeleteApplicationList.isEmpty()) {
            tWListMntDao.deleteApplicationInfoRelatedInterfaceByIds(ableDeleteApplicationList);
            ableDeleteApplicationList.forEach(applicationId -> {
                TApplicationMnt tApplicationMnt = tApplicationMntDao.queryApplicationinfoById(
                    Long.parseLong(applicationId));
                redisManager.removeKey(
                    WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY + tApplicationMnt.getApplicationName());
                redisManager.removeKey(
                    WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY_METRIC + tApplicationMnt.getApplicationName());
                // 采用租户的userAppKey
                //todo Aganet改造点
                agentConfigCacheManager.evictRecallCalls(tApplicationMnt.getApplicationName());
            });
            //删除白名单需要更新缓存
            redisManager.removeKey(WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY);
            redisManager.removeKey(WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY_METRIC);
            tApplicationMntDao.queryApplicationName(ableDeleteApplicationList).forEach(
                applicationName -> TakinFileUtil.recursiveDeleteFile(new File(getBasePath() + applicationName)));
            tApplicationMntDao.deleteApplicationinfoByIds(ableDeleteApplicationList);
            TDataBuildDao.deleteApplicationToDataBuild(ableDeleteApplicationList);
            TLinkDetectionDao.deleteApplicationToLinkDetection(ableDeleteApplicationList);
            tShadowTableDataSourceDao.deleteByApplicationIdList(ableDeleteApplicationList);

            Map<String, Object> logMap = Maps.newHashMap();
            List<Map<String, Object>> ableDeleteApplicationWlistData = tApplicationMntDao.queryApplicationListByIds(
                ableDeleteApplicationList);
            List<Map<String, Object>> ableDeleteDataBuild = TDataBuildDao.queryDataBuildListByIds(
                ableDeleteApplicationList);
            List<Map<String, Object>> ableDeleteLinkDetection = TLinkDetectionDao.queryLinkDetectionListByIds(
                ableDeleteApplicationList);
            logMap.put("ableDeleteApplicationWlistData", ableDeleteApplicationWlistData);
            logMap.put("ableDeleteDataBuild", ableDeleteDataBuild);
            logMap.put("ableDeleteLinkDetection", ableDeleteLinkDetection);
            // 同时更新白名单文件
            writeWhiteListFile();
            // 删除远程调用数据
            appRemoteCallService.deleteByApplicationIds(ableDeleteApplicationList.stream().map(Long::valueOf).collect(Collectors.toList()));
        }
        return deleteIds.getResult();
    }

    // =================================== 白名单管理=====================================================

    /**
     * 说明: 查询应用下拉框数据接口
     *
     * @return 应用集合
     * @author shulie
     */
    public List<Map<String, Object>> queryApplicationdata() {
        List<Map<String, Object>> list = transferElementToString(tApplicationMntDao.queryApplicationdata());

        return list;
    }

    //    private static final String whiteListPath = "/opt/takin/conf/takin-remote/api/confcenter/wbmnt/query/";

    /**
     * 说明: 根据应用id更新应用信息
     *
     * @param tApplicationMnt 应用实体类
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationinfo(TApplicationMnt tApplicationMnt) throws TakinModuleException {
        TApplicationMnt originApplicationMnt = tApplicationMntDao.queryApplicationinfoById(
            tApplicationMnt.getApplicationId());
        String originApplicationName = originApplicationMnt.getApplicationName();
        //比较是否更改了  applicationName  ，再看这个appName是否存在
        if (!StringUtils.equals(originApplicationName, tApplicationMnt.getApplicationName())) {
            int applicationExist = tApplicationMntDao.applicationExist(tApplicationMnt.getApplicationName());
            if (applicationExist > 0) {

                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_UPDATE_APPLICATION_DUPICATE_EXCEPTION);
            }
            TakinFileUtil.recursiveDeleteFile(new File(getBasePath() + originApplicationName));
            TakinFileUtil.createFile(getBasePath() + tApplicationMnt.getApplicationName());
        }
        tApplicationMntDao.updateApplicationinfo(tApplicationMnt);
        //之所以这里要多维护一次是因为 agent也要使用这个表的applicationName
        updatePlugins(tApplicationMnt);

    }

    private void updatePlugins(TApplicationMnt tApplicationMnt) {
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
        param.setApplicationId(tApplicationMnt.getApplicationId());
        List<ApplicationPluginsConfigEntity> list = applicationPluginsConfigDAO.findList(param);
        if (list != null && list.size() > 0) {
            ApplicationPluginsConfigEntity entity = list.get(0);
            //执行更新操作
            param.setApplicationName(tApplicationMnt.getApplicationName());
            param.setId(entity.getId());
            param.setConfigKey(entity.getConfigKey());
            param.setConfigValue(entity.getConfigValue());
            pluginsConfigService.update(param);
        }

    }

    /**
     * 说明: 添加白名单接口
     *
     * @param twListVo 白名单实体类
     * @throws TakinModuleException 异常
     * @author shulie
     */
    public List<String> saveWhiteList(TWListVo twListVo) throws TakinModuleException {
        List<String> duplicateUrlList = Lists.newArrayList();
        //MQ单独处理, 和其他白名单相比没有接口名interfaceName,只有queueName
        if (WListTypeEnum.MQ.getValue().equals(twListVo.getType())) {
            int wListExist = tWListMntDao.queryWhiteListCountByMqInfo(twListVo);
            if (wListExist > 0) {
                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_WLIST_DUPICATE_EXCEPTION);
            }
            TWList tWhiteList = new TWList();
            BeanUtils.copyProperties(twListVo, tWhiteList);
            addWhiteList(tWhiteList);
            //插入白名单需要更新缓存
        } else {
            String appId = twListVo.getApplicationId();
            List<String> list = twListVo.getList();
            List<TWList> twLists = Lists.newArrayList();
            list.forEach(url -> {
                int wListExist = tWListMntDao.whiteListExist(appId, url, twListVo.getUseYn());
                if (wListExist > 0) {
                    duplicateUrlList.add(url);
                } else {
                    TWList tWhiteList = new TWList();
                    BeanUtils.copyProperties(twListVo, tWhiteList);
                    tWhiteList.setInterfaceName(url);
                    twLists.add(tWhiteList);
                }
            });
            //批量插入
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
     * 说明: 批量添加白名单接口信息
     *
     * @param twLists 白名单实体类列表
     * @author shulie
     */
    private void batchAddWhiteList(List<TWList> twLists) {
        tWListMntDao.batchAddWhiteList(twLists);
    }

    /**
     * 说明: 添加白名单接口信息
     *
     * @param tWhiteList 白名单实体类
     * @author shulie
     */
    private void addWhiteList(TWList tWhiteList) {
        tWListMntDao.addWhiteList(tWhiteList);
    }

    /**
     * 说明: 链路名称为空时,查询白名单列表返回应用服务信息列表,数据格式为List<TLinkApplicationInterface>
     *
     * @param applicationName 应用名称
     * @param principalNo     负责人工号
     * @param type            白名单类型
     * @return 回应用服务信息列表
     * @author shulie
     */

    public List<TApplicationInterface> queryOnlyWhiteList(String applicationName, String principalNo, String type, String x,
        Long appId) {
        List<TApplicationInterface> queryWhiteListInfo = tWListMntDao.queryOnlyWhiteList(applicationName, principalNo, type, x,
            null, null);
        return queryWhiteListInfo.isEmpty() ? Lists.newArrayList() : queryWhiteListInfo;
    }

    /**
     * 说明: 当链路为空查询不到查询白名单列表时，根据应用名称,负责人和白名单类型查询应用信息
     *
     * @param applicationName 应用名称
     * @param principalNo     负责人
     * @param type            白名单类型
     * @return 应用服务列表
     * @author shulie
     */
    public List<TApplicationInterface> queryWhiteList(String applicationName, String principalNo, String type) {
        return tWListMntDao.queryWhiteList(applicationName, principalNo, type);
    }

    /**
     * 说明: 根据id查询白名单信息
     *
     * @param whitelistId 白名单id
     * @return 白名单信息详情
     * @throws TakinModuleException 异常
     * @author shulie
     */
    public TWList querySingleWhiteListById(String whitelistId) throws TakinModuleException {
        TWList tWlist = tWListMntDao.querySingleWhiteListById(whitelistId);
        if (tWlist == null) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYID_NOTEXIST);
        }

        return tWlist;
    }

    /**
     * 说明:  根据id更新白名单信息
     *
     * @param param 白名单实体类
     * @author shulie
     */
    public void updateWhiteListById(TWList param) throws TakinModuleException {
        TWList dbData = tWListMntDao.querySingleWhiteListById(String.valueOf(param.getWlistId()));
        if (dbData == null) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_QUERY_WLISTBYID_NOTEXIST);
        }
        int applicationExist = tWListMntDao.whiteListExist(param.getApplicationId(), param.getInterfaceName(),
            param.getUseYn());
        //            修改时,排除当前白名单记录,所以存在至少1个白名单
        if (applicationExist > 1) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_UPDATE_WLIST_DUPICATE_EXCEPTION);
        }
        //更新白名单需要更新缓存
        tWListMntDao.updateSelective(param);
        //写入文件 nginx访问
        writeWhiteListFile();
    }

    /**
     * 说明: 批量删除白名单
     *
     * @param whiteListIds 白名单id(多个id以逗号拼接)
     * @author shulie
     */
    public String deleteWhiteListByIds(String whiteListIds) {
        GetDeleteIds deleteIds = new GetDeleteIds(whiteListIds, "interfaceName").invoke(tWListMntDao);
        List<String> ableDeleteWhiteList = deleteIds.getAbleDeleteList();
        if (!ableDeleteWhiteList.isEmpty()) {
            List<TWList> ableDeleteWhiteLists = tWListMntDao.queryWhiteListByIds(ableDeleteWhiteList);
            tWListMntDao.deleteWhiteListByIds(ableDeleteWhiteList);
            //删除白名单需要更新缓存
            ableDeleteWhiteLists.stream().map(TWList::getApplicationId).distinct().forEach(applicationId -> {
                TApplicationMnt tApplicationMnt = tApplicationMntDao.queryApplicationinfoById(
                    Long.parseLong(applicationId));
            });

        }
        writeWhiteListFile();
        return deleteIds.getResult();
    }

    /**
     * 说明: 根据id批量删除白名单
     *
     * @author shulie
     */
    public void deleteWhiteListByIds(List<Long> ids) {
        if (!ids.isEmpty()) {
            List<TWList> ableDeleteWhiteLists = tWListMntDao.getWhiteListByIds(ids);
            tWListMntDao.deleteByIds(ids);

        }
        writeWhiteListFile();
    }

    // =================================== 黑名单管理=====================================================

    /**
     * 说明: 添加黑名单接口
     *
     * @param tBlackList 黑名单实体类
     * @throws TakinModuleException 异常
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
     * 说明: 添加黑名单接口信息
     *
     * @param tBlackList 黑名单实体类
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
        whiteListFileService.writeWhiteListFile(WebPluginUtils.traceTenantCommonExt());
    }

    /**
     * 说明: 根据id查询黑名单信息
     *
     * @return 单个黑名单详情
     * @author shulie
     */
    public TBList querySingleBlackListById(String blistId) {
        return tBListMntDao.querySingleBListById(blistId);
    }

    /**
     * 说明: 根据id更新黑名单信息
     *
     * @param tBlackList 黑名单实体类
     * @author shulie
     */
    public void updateBlackListById(TBList tBlackList) {
        TBList originBlackList = tBListMntDao.querySingleBListById(String.valueOf(tBlackList.getBlistId()));
        tBListMntDao.updateBListById(tBlackList);
        configSyncService.syncBlockList(WebPluginUtils.traceTenantCommonExt());
        whiteListFileService.writeWhiteListFile(WebPluginUtils.traceTenantCommonExt());
    }

    /**
     * 说明: 批量删除黑名单信息
     *
     * @param blistIds 黑名单id(多个id以逗号拼接)
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
     * 说明: 查询黑名单列表
     *
     * @param bListQueryParam redis的key和负责人工号
     * @return 黑名单列表集合
     * @author shulie
     */
    public Response queryBlackList(BListQueryParam bListQueryParam) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageNum", bListQueryParam.getCurrentPage());
        paramMap.put("pageSize", bListQueryParam.getPageSize());
        String redisKey = bListQueryParam.getRedisKey();
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TBList> queryBlackList = tBListMntDao.queryBList(redisKey, "");
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
     * 说明: 查询白名单列表，只供agent拦截使用，只选择USE_YN=1的白名单
     *
     * @return 黑白名单列表
     * @author shulie
     */
    public Map<String, List<Map<String, Object>>> queryBlackWhiteList(String appName) throws TakinModuleException {

        //先查询白名单  如果有就直接用缓存  否则从数据库搜索 在放入缓存  不用锁竞争  并发没问题 黑名单同样
        String appNameKey = "";
        if (StringUtils.isNotEmpty(appName)) {
            appNameKey = appName;
        }
        List<Map<String, Object>> wLists = tWListMntDao.queryWhiteListList(appName);
        List<Map<String, Object>> bLists = tBListMntDao.queryBListList();
        Map<String, List<Map<String, Object>>> resultMap = Maps.newHashMapWithExpectedSize(30);
        List<Map<String, Object>> wListsResult = Lists.newArrayList();
        if (wLists != null) {
            for (Map<String, Object> whiteItem : wLists) {
                String type = (String)whiteItem.get("TYPE");
                String interfaceName = (String)whiteItem.get("INTERFACE_NAME");
                // 格式校验
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
                // 去除空格
                whiteItemNew.put("INTERFACE_NAME", interfaceName.trim());
                wListsResult.add(whiteItemNew);
            }
        }

        resultMap.put("wLists", wListsResult);
        resultMap.put("bLists", bLists);

        return resultMap;
    }

    /**
     * 说明: 查询黑白名单列表
     *
     * @return 黑白名单列表
     * @author shulie
     */
    public Map<String, List<Map<String, Object>>> queryBlackWhiteMetricList(String appName) throws TakinModuleException {
        List<Map<String, Object>> wLists = null;
        List<Map<String, Object>> bLists = null;
        //先查询白名单  如果有就直接用缓存  否则从数据库搜索 在放入缓存  不用锁竞争  并发没问题 黑名单同样
        String appNameKey = "";
        if (StringUtils.isNotEmpty(appName)) {
            appNameKey = appName;
        }
        Optional<Object> wListValue = redisManager.valueGet(
            WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY_METRIC + appNameKey);
        if (wListValue.isPresent()) {
            wLists = (List<Map<String, Object>>)wListValue.get();
        } else {
            RedisKey wListRedis = new RedisKey(WhiteBlackListRedisKey.TAKIN_WHITE_LIST_KEY_METRIC + appNameKey,
                WhiteBlackListRedisKey.TIMEOUT);
            wLists = tWListMntDao.queryWhiteListList(appName);
            // 查询完白名单列表后，需要加工成海明需要的数据格式
            //先筛选出来type=mq的，然后再根据mqType计算出是ibmmq还是rocketmq
            //如果没查到直接报查询异常
            if (wLists == null || wLists.isEmpty()) {
                throw new TakinModuleException(TakinErrorEnum.CONFCENTER_QUERY_NOT_WLIST_FOR_APPNAME_EXCEPTION);
            }
            wLists.forEach(map -> {
                String type = (String)map.get("TYPE");
                if ("mq".equals(type)) {
                    String mqType = (String)map.get("MQ_TYPE");
                    //如果是ESB/IBM，则将type修改成ibmmq
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
            bLists = (List<Map<String, Object>>)blackListValue.get();
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

    // =================================== 链路管理=====================================================

    /**
     * 说明: 添加链路信息
     *
     * @param tLinkServiceMntVo 链路服务实体类
     * @throws TakinModuleException 异常
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

        //添加链路信息和链路关联服务
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
     * 说明: 添加链路信息
     * 链路改造时使用的新的保存链路方法
     *
     * @param tLinkServiceMntVo 链路服务实体类
     * @throws TakinModuleException 异常
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

        //添加链路信息和链路关联服务
        tLinkServiceMntVo.setLinkId(snowflake.next());
        tLinkMnDao.addBasicLink(tLinkServiceMntVo);

        //++ 添加二级链路和链路关系
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
     * 说明: 添加基础链路信息抽取方法
     *
     * @param tLinkServiceMntVo    基础链路服务实体类
     * @param linkId               基础链路id
     * @param tLinkServiceMntList  基础链路服务列表
     * @param tLinkServiceMntLists 新的基础链路服务列表
     * @param linkExist            基础链路名称是否存在
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
     * 说明: 查询链路信息列表
     *
     * @param paramMap 包含链路名称,链路等级,应用名称,负责人工号,接口名称
     *                 pageSize为-1代表不分页
     * @return 链路列表
     * @author shulie
     */
    public PageInfo<TLinkApplicationInterface> queryBasicLinkList(Map<String, Object> paramMap) {

        //FIXME 这里需要看是否修改
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
     * linkName查询APPID
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
     * 说明: 根据链路id查询链路信息详情
     *
     * @param linkId 链路id
     * @return 链路服务信息详情
     * @author shulie
     */
    public TLinkServiceMntVo queryLinkByLinkId(String linkId) {
        TLinkServiceMntVo tLinkServiceMntVo = tLinkMnDao.queryLinkByLinkId(linkId);
        //设置链路
        setTechLinks(tLinkServiceMntVo);
        //设置节点
        setNodes(tLinkServiceMntVo);

        return tLinkServiceMntVo;
    }

    private void setTechLinks(TLinkServiceMntVo tLinkServiceMntVo) {
        if (StringUtils.isNotBlank(tLinkServiceMntVo.getTechLinks())) {
            List<List<String>> techLinks = JSON.parseObject(tLinkServiceMntVo.getTechLinks(),
                new TypeReference<List<List<String>>>() {
                }); // Json 转List

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
            //节点
            List<Map<String, Object>> nodeList = Lists.newArrayListWithExpectedSize(tLinkNodesVoList.size() + 1);
            //箭头图
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
     * 说明: 批量删除链路信息
     *
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public String deleteLinkByLinkIds(String basicLinkIds) {
        GetDeleteIds deleteIds = new GetDeleteIds(basicLinkIds, "basicLinkName").invoke(tLinkMnDao);
        List<String> ableDeleteBasicLink = deleteIds.getAbleDeleteList();
        if (!ableDeleteBasicLink.isEmpty()) {
            tLinkMnDao.deleteLinkByLinkIds(ableDeleteBasicLink);
            // 删除业务链路和技术链路关联关系
            tLinkMnDao.deleteBTLinkRelationShip(ableDeleteBasicLink);
            tLinkMnDao.deleteLinkInterfaceByLinkIds(ableDeleteBasicLink);
            //删除二级链路和业务/技术链路关系
            tLinkMnDao.deleteSecondBasicLinkRef(Splitter.on(",").trimResults().omitEmptyStrings()
                .splitToList(basicLinkIds));
            List<TLinkServiceMntVo> tLinkServiceMntVos = tLinkMnDao.queryLinksByLinkIds(ableDeleteBasicLink);

        }
        return deleteIds.getResult();
    }

    /**
     * 说明: 删除链路服务信息接口
     * query/bwlistmetric
     *
     * @param linkServiceIds 链路服务关系表id
     * @author shulie
     */
    public void deleteLinkInterfaceByLinkServiceId(String linkServiceIds) {
        List<String> linkServiceIdsList = Arrays.stream(linkServiceIds.split(",")).filter(
            e -> StringUtils.isNotEmpty(e)).distinct().collect(Collectors.toList());
        List<TLinkServiceMntVo> tLinkServiceMntVos = tLinkMnDao.queryLinksByLinkIds(linkServiceIdsList);

        tLinkMnDao.deleteLinkInterfaceByLinkServiceId(linkServiceIdsList);
    }

    /**
     * 说明: 根据id更新基础链路信息
     *
     * @param tLinkServiceMntVo 基础链路服务实体类
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
        // 链路服务管理实体类
        List<TLinkServiceMnt> tLinkServiceMntList = tLinkServiceMntVo.gettLinkServiceMntList();
        List<TLinkServiceMnt> saveServiceMntLists = Lists.newArrayList();
        List<TLinkServiceMnt> updateServiceMntLists = Lists.newArrayList();
        if (linkExist > 0) {
            //更新
            if (tLinkServiceMntList != null && !tLinkServiceMntList.isEmpty()) {
                tLinkServiceMntList.forEach(tLinkServiceMnt -> {
                    int linkInterfaceExist = tLinkMnDao.updateLinkInterfaceExist(String.valueOf(linkId),
                        String.valueOf(tLinkServiceMnt.getLinkServiceId()));
                    if (linkInterfaceExist == 0) {
                        tLinkServiceMnt.setLinkServiceId(snowflake.next());
                        tLinkServiceMnt.setLinkId(linkId);
                        saveServiceMntLists.add(tLinkServiceMnt);

                    } else {
                        //更新
                        updateServiceMntLists.add(tLinkServiceMnt);
                    }
                });
            }
            tLinkMnDao.updateLink(tLinkServiceMntVo);

            // ==============================Start 新增业务链路和技术链路关系 Start==============================
            // 更新业务链路和技术链路关系
            // 先删除关系在绑定新关系
            tLinkMnDao.deleteReLationShipByTLinkId(Collections.singletonList(String.valueOf(linkId)));
            saveRelationLink(linkId, tLinkServiceMntVo.getTechLinks(), "t_bs_tch_link");
            // ==============================End 新增业务链路和技术链路关系 End==============================

            if (!saveServiceMntLists.isEmpty()) {
                //只添加基础链路关联服务
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

            }//更新基础链路关联服务
        } else {
            //添加基础链路信息和链路关联服务
            tLinkServiceMntVo.setLinkId(snowflake.next());
            tLinkMnDao.addBasicLink(tLinkServiceMntVo);

            addParam(tLinkServiceMntVo, String.valueOf(linkId), tLinkServiceMntList, saveServiceMntLists,
                String.valueOf(linkExist));
            tLinkMnDao.addLinkInterface(saveServiceMntLists);

        }
    }

    /**
     * 说明:
     * 新增:根据id更新基础链路信息
     *
     * @param tLinkServiceMntVo 基础链路服务实体类
     * @throws TakinModuleException
     * @author shulie
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLinkInfo(TLinkServiceMntVo tLinkServiceMntVo) throws TakinModuleException {
        //如果链路服务没有了,则删除链路服务
        if (StringUtils.isNotEmpty(tLinkServiceMntVo.getLinkServiceIds())) {
            deleteLinkInterfaceByLinkServiceId(tLinkServiceMntVo.getLinkServiceIds());
        }

        long linkId = tLinkServiceMntVo.getLinkId();
        int linkExist = tLinkMnDao.updateLinkExist(String.valueOf(linkId));
        // 链路服务管理实体类
        List<TLinkServiceMnt> tLinkServiceMntList = tLinkServiceMntVo.gettLinkServiceMntList();
        List<TLinkServiceMnt> saveServiceMntLists = Lists.newArrayList();
        List<TLinkServiceMnt> updateServiceMntLists = Lists.newArrayList();
        if (linkExist > 0) {
            //更新
            if (tLinkServiceMntList != null && !tLinkServiceMntList.isEmpty()) {
                tLinkServiceMntList.forEach(tLinkServiceMnt -> {
                    int linkInterfaceExist = tLinkMnDao.updateLinkInterfaceExist(String.valueOf(linkId),
                        String.valueOf(tLinkServiceMnt.getLinkServiceId()));
                    if (linkInterfaceExist == 0) {
                        tLinkServiceMnt.setLinkServiceId(snowflake.next());
                        tLinkServiceMnt.setLinkId(linkId);
                        saveServiceMntLists.add(tLinkServiceMnt);

                    } else {
                        //更新
                        updateServiceMntLists.add(tLinkServiceMnt);
                    }
                });
            }
            tLinkMnDao.updateLink(tLinkServiceMntVo);
            //++ 更新链路与二级链路的关系
            //存在就更新，不存在(迎合以前的旧数据)就新增,
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
                //只添加基础链路关联服务
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

            }//更新基础链路关联服务
        } else {
            //添加基础链路信息和链路关联服务
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
     * 说明: query application - ip by application name and type
     *
     * @param applicationName 应用名称
     * @return 应用列表
     * @author shulie
     */
    public List<TApplicationIp> queryApplicationIpByNameTypeList(String applicationName, String type) {
        return tApplicationIpDao.queryApplicationIpByNameTypeList(applicationName, type);
    }

    /**
     * 说明: query application - ip by application name and type
     *
     * @param applicationName 应用名称
     * @return 应用列表
     * @author shulie
     */
    public List<TApplicationIp> queryApplicationIpByNameList(String applicationName) {
        List<TApplicationIp> tApplicationIps = tApplicationIpDao.queryApplicationIpByNameList(applicationName);
        return tApplicationIps;
    }

    /**
     * 说明: 添加告警信息
     *
     * @param tAlarm 告警实体类
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
     * 说明:  根据appname查询该应用下的白名单
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
        // 先从缓存中获取
        if ("1".equals(type) && StringUtils.isEmpty(interfaceName)) {

            RedisTemplate redisTemplate = redisManager.getRedisTemplate();
            String pradaSynchronizedToRedisStatus = (String)redisTemplate.opsForValue().get("pradaSynchronizedToRedis");
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
                // 封装数据
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
        // 从数据库模糊查询
        //分页
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        switch (type) {
            case "1":
                List<TPradaHttpData> tPradaHttpData = tWListMntDao.queryInterfaceByAppNameByTPHD(applicationName, type,
                    interfaceName);
                return new PageInfo<>(tPradaHttpData.isEmpty() ? Lists.newArrayList() : tPradaHttpData);
            case "2":
                List<TUploadInterfaceDataVo> dubboData = tWListMntDao.queryInterfaceByAppNameFromTUID(applicationName,
                    type, interfaceName);
                return new PageInfo<>(dubboData.isEmpty() ? Lists.newArrayList() : dubboData);
            case "4":
                List<TUploadInterfaceDataVo> jobData = tWListMntDao.queryInterfaceByAppNameFromTUID(applicationName,
                    type, interfaceName);
                return new PageInfo<>(jobData.isEmpty() ? Lists.newArrayList() : jobData);
            default:
                break;
        }
        return new PageInfo(Lists.newArrayList());
    }

    /**
     * 说明: 过滤重复的接口
     *
     * @param list 待过滤的接口
     * @return 符合过滤规则的接口集合
     * @author shulie
     * @date 2019/3/1 15:11
     */
    public List<String> filterWhiteList(String appName, List<String> list) {

        //前面ip  /vas-cas-web/login;JSESSIONID=    NVAS-vas-cas-web
        //http://vip.pamirs.com:8080/vas-cas-web/login;JSESSIONID= NVAS-vas-cas-web
        //http://cubc.pamirs.com/cubc/codaccountmanage/queryModifyParamsById 结尾是数字  cubc
        //http://dpjjwms.pamirs.com/dpjjwms/ 结尾.cache.html wms-dpjjwms
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
     * 说明: 查询应用信息列表(prada同步表查数据)
     *
     * @return 应用列表
     * @author shulie
     * @date 2019/3/7 9:20
     */
    public List<?> queryAppNameList() {

        List<Object> returnList = Lists.newArrayListWithCapacity(200);
        List<String> list = tApplicationMntDao.queryAppNameList();
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        list.sort(String::compareTo);
        list.forEach(appName -> {
                Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
                map.put("key", appName);
                returnList.add(map);
            }
        );
        return returnList;
    }

    /**
     * 查询白名单列表
     *
     * @param applicationId 应用ID
     * @return
     */
    public List<Map<String, Object>> queryWhiteListByAppId(String applicationId) {
        return tWListMntDao.queryWhiteListByAppId(applicationId);

    }

    /**
     * 链路管理文件上传
     * 模版顺序参考@link LinkManageExcelVo
     *
     * @return
     * @throws IOException
     * @throws TakinModuleException
     */
    public void batchUploadLinkList(MultipartFile[] files) throws IOException, TakinModuleException {
        for (MultipartFile file : files) {
            //表示从第一个Sheet的第一行开始读取
            Sheet sheet = new Sheet(1, 1);
            InputStream stream = new BufferedInputStream(file.getInputStream());
            List<Object> lists = EasyExcelFactory.read(stream, sheet);

            List<TLinkServiceMntVo> addList = new ArrayList<>();
            List<TLinkServiceMntVo> updateList = new ArrayList<>();

            int index = 0;
            for (Object list : lists) {
                ArrayList inVo = (ArrayList)list;
                TLinkServiceMntVo vo = new TLinkServiceMntVo();
                vo.setAswanId(String.valueOf(inVo.get(0)));
                List<JSONObject> mnts = JSONObject.parseObject((String)inVo.get(1), List.class);
                List<TLinkServiceMnt> tLinkServiceMntList = new ArrayList<>(mnts.size());
                StringBuffer sb = new StringBuffer();
                mnts.forEach(mnt -> {
                    TLinkServiceMnt nt = new TLinkServiceMnt();
                    nt.setInterfaceName(String.valueOf(mnt.get("接口名称")));
                    nt.setInterfaceDesc(String.valueOf(mnt.get("接口说明")));
                    String linkServiceId = mnt.getString("链路服务id");
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
                    throw new TakinModuleException(TakinErrorEnum.CONFCENTER_NOT_ALLOW_EMPTY + "，第" + index++ + "行数据");
                }

                String secondlinkId = secondLinkDao.queryAppIdByAppName(secondLinkName);
                vo.setSecondLinkId(secondlinkId);
                vo.setTargetSuccessRate(String.valueOf(inVo.get(9)));
                vo.setTargetTps(String.valueOf(inVo.get(10)));
                vo.setUseYn("可用".equalsIgnoreCase(String.valueOf(inVo.get(11))) ? 1 : 0);
                vo.setVolumeCalcStatus("1");
                //基础链路id
                String linkid = ObjectUtils.toString(inVo.get(12));
                //当前tps
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
        Map dataList = (Map)data.get("dicList");
        Iterator iterator = dataList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (StringUtils.equalsIgnoreCase(ObjectUtils.toString(entry.getValue()), key1)) {
                return String.valueOf(entry.getKey());
            }

        }
        return null;

    }

    private String getValue(TakinDictTypeEnum enums, Object value) {
        String key = String.valueOf(value);
        Map data = this.queryDicList(enums);
        Map dataList = (Map)data.get("dicList");
        Iterator iterator = dataList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (StringUtils.equalsIgnoreCase(ObjectUtils.toString(entry.getValue()), key)) {
                return String.valueOf(entry.getKey());
            }

        }
        return null;

    }

    /**
     * 白名单文件上传
     */
    public void batchUploadWhiteList(MultipartFile[] files) throws IOException, TakinModuleException {
        //应用数据下接口
        List<Map<String, Object>> applicationDataList = this.queryApplicationdata();
        Set<Object> applicationSet = new HashSet<Object>();
        for (MultipartFile file : files) {
            //校验应用名
            applicationDataList.forEach(map -> {
                applicationSet.add(map.get("applicationName"));
            });

            //表示从第一个Sheet的第一行开始读取
            Sheet sheet = new Sheet(1, 1);
            InputStream stream = new BufferedInputStream(file.getInputStream());

            List<Object> lists = EasyExcelFactory.read(stream, sheet);
            for (Object list : lists) {

                TWListVo vo = new TWListVo();
                ArrayList excelVo = (ArrayList)list;
                String applicationName = String.valueOf(excelVo.get(1));
                if (!applicationSet.contains(applicationName)) {
                    throw new TakinModuleException(TakinErrorEnum.CONFCENTER_ADD_APPLICATION_NOTEXIST_EXCEPTION);

                }
                vo.setPrincipalNo("000000");
                Long id = tApplicationMntDao.queryIdByApplicationName(String.valueOf(excelVo.get(1)));
                vo.setApplicationId(String.valueOf(id));
                //植入白名单类型的值
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
                String useYn = String.valueOf(excelVo.get(6)).equalsIgnoreCase("可用") ? "1" : "0";
                vo.setUseYn(useYn);
                String interfaceName = isEmPty(excelVo.get(7)) ? null : String.valueOf(excelVo.get(7));
                if (StringUtils.isNotBlank(interfaceName)) {
                    String[] strings = interfaceName.split(",");
                    vo.setList(Arrays.asList(strings));
                }
                vo.setDictType("ca888ed801664c81815d8c4f5b8dff0c");
                //http页面分类
                String pageLevel = isEmPty(excelVo.get(8)) ? null : String.valueOf(excelVo.get(8));
                vo.setPageLevel(pageLevel(pageLevel));
                //队列名字
                String queueName = isEmPty(excelVo.get(9)) ? null : String.valueOf(excelVo.get(9));
                vo.setQueueName(queueName);

                //白名单id
                String twlist = isEmPty(excelVo.get(10)) ? null : String.valueOf(excelVo.get(10));
                if (twlist != null) {
                    vo.setWlistId(Long.parseLong(twlist));
                }
                if (StringUtils.isBlank(twlist)) {
                    //添加
                    this.saveWhiteList(vo);
                } else {
                    //修改
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
            case "普通页面加载":
                return "1";
            case "简单查询页面/复杂界面":
                return "2";
            case "复杂查询页面":
                return "3";
            default:
                return null;
        }
    }

    public List<TLinkBasicVO> queryLinkByLinkType(Map<String, Object> queryMap) {
        List<Integer> linkTypeList = new ArrayList<>();
        if (queryMap != null && queryMap.get("linkType") != null) {
            String linkTypeString = (String)queryMap.get("linkType");
            if (StringUtils.isNotEmpty(linkTypeString)) {
                linkTypeList = Arrays.asList(linkTypeString.split(",")).stream().map(s -> Integer.parseInt(s.trim()))
                    .collect(Collectors.toList());
            }
        }
        return tLinkMntDao.queryLinksByLinkType(linkTypeList);
    }

    /**
     * 更新应用 agentVersion 版本
     *
     * @param appName       应用名
     * @param agentVersion  应用agent版本
     * @param pradarVersion Pradar Agent版本
     * @throws TakinModuleException
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateAppAgentVersion(String appName, String agentVersion, String pradarVersion) throws
        TakinModuleException {
        // 是否执行
        String isUpdateAgentVersionString = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.AGENT_HTTP_UPDATE_VERSION);
        if (!Boolean.parseBoolean(isUpdateAgentVersionString)) {
            return;
        }

        if (StringUtils.isEmpty(appName)) {
            throw new TakinModuleException(TakinErrorEnum.CONFCENTER_UPDATE_APPLICATION_AGENT_VERSION_EXCEPTION);
        }
        TApplicationMnt applicationMnt = applicationService.queryTApplicationMntByName(appName);
        if (applicationMnt == null) {
            log.warn("应用查询异常，未查到, updateAppAgentVersion fail!");
            return;
        }
        tApplicationMntDao.updateApplicaionAgentVersion(applicationMnt.getApplicationId(), agentVersion, pradarVersion);
    }

    /**
     * 插叙下拉款
     *
     * @return
     */
    public List<Map<String, String>> queryLinkIdName() {
        return tLinkMnDao.queryLinkIdName();
    }

    /**
     * 给链路提供白名单列表接口
     *
     * @return
     */
    public List<Map<String, String>> getWhiteListForLink() {
        return tWListMntDao.getWhiteListForLink();
    }

    /**
     * 查询链路头信息
     *
     * @return
     */
    @Deprecated
    public List<Map<String, Object>> queryLinkHeaderList() {
        /**
         * 1,查询链路模块名称，数量，索引
         * 2,通过集合去分组　
         */
        List<Map<String, Object>> resultList = Lists.newArrayList();
        List<TLinkMntDictoryVo> linkHeaderInfo = tLinkMnDao.queryLinkHeaderInfo();
        Map<String, List<TLinkMntDictoryVo>> collect = new HashMap<>(10);
        /**
         * 按链路模块名称;链路模块序号分组
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
             * 如果某个链路模块下没有链路或者链路数量为1，则这里的tLinkMntDictoryVoList的size==1,
             *  此时数量需要区分
             *  否则，直接使用tLinkMntDictoryVoList.size()就是该链路模块下的链路数量
             */
            String linkName = tLinkMntDictoryVoList.get(0).getLinkName();
            if (StringUtils.isEmpty(linkName)) {
                resultMap.put("count", 0);
            } else {
                resultMap.put("count", tLinkMntDictoryVoList.size());
            }
            //获取calcVolumeLinkList对象内容
            List<Map<String, String>> list = tLinkMnDao.queryCalcVolumeLinkList(nameOrderArr[1]);
            resultMap.put("calcVolumeLinkList", list);
            resultList.add(resultMap);
        });
        Collections.sort(resultList, Comparator.comparingInt(o -> Integer.valueOf((String)o.get("order"))));
        return resultList;
    }

    /**
     * 查询链路头信息
     *
     * @return
     */
    public List<Map<String, Object>> queryLinkHeaderInfoList() {
        /**
         * 1,查询链路模块名称，数量，索引
         * 2,通过集合去分组　
         */
        List<Map<String, Object>> resultList = Lists.newArrayList();
        List<TLinkTopologyInfoVo> linkHeaderInfo = tLinkMnDao.queryLinkHeaderInfoList();
        Map<String, List<TLinkTopologyInfoVo>> collect = new HashMap<>(16);
        /**
         * 按链路模块名称;链路模块序号分组
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
             * 如果某个链路模块下没有链路或者链路数量为1，则这里的tLinkMntDictoryVoList的size==1,
             *  此时数量需要区分
             *  否则，直接使用tLinkMntDictoryVoList.size()就是该链路模块下的链路数量
             */
            String linkName = tLinkTopologyInfoVoList.get(0).getLinkName();
            if (StringUtils.isEmpty(linkName)) {
                resultMap.put("count", 0);
            } else {
                resultMap.put("count", tLinkTopologyInfoVoList.size());
            }
            //获取二级链路列表
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
            //获取calcVolumeLinkList对象内容
            List<Map<String, String>> list = tLinkMnDao.queryCalcVolumeLinkListByModule(nameOrderArr[1]);
            resultMap.put("calcVolumeLinkList", list);
            resultList.add(resultMap);
        });
        Collections.sort(resultList, Comparator.comparingInt(o -> Integer.valueOf((String)o.get("order"))));
        return resultList;
    }

    /**
     * 说明: 根据链路模块查询二级链路列表
     *
     * @param linkModule 链路模块
     * @return java.util.List<TSecondLinkMnt>
     * @author shulie
     * @create 2019/4/15 17:59
     */
    public List<TSecondLinkMnt> querySecondLinkByModule(String linkModule) {
        return tLinkMnDao.querySecondLinkMapByModule(linkModule);
    }

    // =================================== 黑名单管理=====================================================

    /**
     * 说明: 添加黑名单接口
     *
     * @param tBList 黑名单实体类
     * @throws TakinWebException 异常
     * @author shulie
     */
    public void saveBList(TBList tBList) throws TakinWebException {
        if (StringUtils.isBlank(tBList.getUseYn())) {
            tBList.setUseYn("1");
        }
        int applicationExist = tBListMntDao.bListExist(tBList.getRedisKey());
        if (applicationExist > 0) {
            throw new TakinWebException(TakinErrorEnum.CONFCENTER_ADD_BLIST_DUPICATE_EXCEPTION,"");
        }
        addBList(tBList);
        writeWhiteListFile();
    }

    /**
     * 说明: 添加黑名单接口信息
     *
     * @param tBList 黑名单实体类
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
     * 说明: 根据id查询黑名单信息
     *
     * @return 单个黑名单详情
     * @author shulie
     */
    public TBList querySingleBListById(String blistId) {
        TBList tbList = tBListMntDao.querySingleBListById(blistId);

        return tbList;
    }

    /**
     * 说明: 根据id更新黑名单信息
     *
     * @param tBList 黑名单实体类
     * @author shulie
     */
    public void updateBListById(TBList tBList) {
        TBList originBList = tBListMntDao.querySingleBListById(String.valueOf(tBList.getBlistId()));
        tBListMntDao.updateBListById(tBList);
        whiteListFileService.writeWhiteListFile(WebPluginUtils.traceTenantCommonExt());
    }

    /**
     * 说明: 批量删除黑名单信息
     *
     * @param blistIds 黑名单id(多个id以逗号拼接)
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
     * 说明: 查询黑名单列表
     *
     * @param bListQueryParam redis的key和负责人工号
     * @return 黑名单列表集合
     * @author shulie
     */
    public Response queryBList(BListQueryParam bListQueryParam) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageNum", bListQueryParam.getCurrentPage());
        paramMap.put("pageSize", bListQueryParam.getPageSize());
        String redisKey = bListQueryParam.getRedisKey();
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TBList> queryBList = tBListMntDao.queryBList(redisKey, "");
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




    // =================================== 黑名单管理=====================================================



    /**
     * 说明: 分离可删除的id和不可删除的名称字符串
     *
     * @author shulie
     * @date 2018/7/10 15:35
     * @return 可删除的id集合和不可删除的名称字符串
     */
    private class GetDeleteIds<T> {
        private String ids;
        private String disableName;
        private List<String> ableDeleteList;
        private String result;

        public GetDeleteIds(String ids, String disableName) {
            this.ids = ids;
            this.disableName = disableName;
        }

        public List<String> getAbleDeleteList() {
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
                if (t instanceof TApplicationMntDao) {
                    map = tApplicationMntDao.queryApplicationRelationBasicLinkByApplicationId(id);
                } else if (t instanceof TWhiteListMntDao) {
                    map = tWListMntDao.queryWhiteListRelationBasicLinkByWhiteListId(id);
                } else if (t instanceof TLinkMntDao) {
                    map = tLinkMnDao.querySecondLinkRelationBasicLinkByBasicLinkId(id);
                }
                if (Objects.isNull(map)) {
                    ableDeleteList.add(id);
                } else {
                    sb.append(MapUtils.getString(map, disableName)).append(",");
                }
            });
            result = StringUtils.substringBeforeLast(sb.toString(), ",");
            return this;
        }
    }
}
