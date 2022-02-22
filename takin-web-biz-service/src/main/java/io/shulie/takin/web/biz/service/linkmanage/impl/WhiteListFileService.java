package io.shulie.takin.web.biz.service.linkmanage.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.entity.dao.confcenter.TBListMntDao;
import com.pamirs.takin.entity.domain.entity.TBList;
import com.pamirs.takin.entity.domain.query.whitelist.AgentWhiteList;
import io.shulie.takin.web.biz.service.linkmanage.WhiteListService;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.util.whitelist.WhitelistUtil;
import io.shulie.takin.web.common.vo.agent.AgentBlacklistVO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.WhiteListDAO;
import io.shulie.takin.web.data.dao.application.WhitelistEffectiveAppDao;
import io.shulie.takin.web.data.dao.blacklist.BlackListDAO;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistEffectiveAppSearchParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSearchParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.blacklist.BlacklistResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistEffectiveAppResult;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-04-20 19:16
 */

@Service
@Slf4j
public class WhiteListFileService {

    @Autowired
    private TBListMntDao tbListMntDao;

    @Resource
    private WhiteListDAO whiteListDAO;


    @Autowired
    private BlackListDAO blackListDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private WhitelistEffectiveAppDao whitelistEffectiveAppDao;

    @Autowired
    private WhiteListService whiteListService;

    @PostConstruct
    public void init() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            0, 1,
            0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1),
            r -> new Thread(r, "初始化白名单"), new CallerRunsPolicy());
        threadPoolExecutor.submit(() -> {
            log.info("开始初始化白名单");
            // 老版本 agent 新版本agent 已转到远程调用模块
            // 带租户插件
            for (TenantInfoExt infoExt : WebPluginUtils.getTenantInfoList()) {
                infoExt.getEnvs().forEach(t -> {
                    TenantCommonExt ext = new TenantCommonExt();
                    ext.setTenantCode(infoExt.getTenantCode());
                    ext.setTenantId(infoExt.getTenantId());
                    ext.setTenantAppKey(infoExt.getTenantAppKey());
                    ext.setEnvCode(t.getEnvCode());
                    writeWhiteListFile(ext);
                });
            }
        });
    }

    public void writeWhiteListFile() {
        writeWhiteListFile(null);
    }

    public void writeWhiteListFile(TenantCommonExt ext) {
        try {
            if (ext == null){
                ext = WebPluginUtils.traceTenantCommonExt();
            }
            String whiteListPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_WHITE_LIST_CONFIG_PATH);
            Map<String, Object> result = queryBlackWhiteList("", ext);
            if (null != result && result.size() > 0) {
                File file = new File(whiteListPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                /*
                 * 白名单写入租户key
                 */
                if (file.exists()) {
                    file = new File(whiteListPath + ext.getTenantAppKey());
                    if (!file.isFile()) {
                        if (!file.createNewFile()) {
                            throw new RuntimeException("白名单文件：" + file.getPath() + " 创建失败！");
                        }
                    }
                }

                ResponseOk.ResponseResult response = ResponseOk.result(result);
                String content = JSONObject.toJSONString(response);
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), false);
                try {
                    BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
                    bufferWritter.write(content);
                    bufferWritter.close();
                } finally {
                    fileWriter.close();
                }
            }
        } catch (Exception e) {
            log.error("fail to write WhiteListFile", e);
        }
    }

    /**
     * 说明: 查询白名单列表，只供agent拦截使用，只选择USE_YN=1的白名单
     *
     * @return 黑白名单列表
     * @author shulie
     */
    public Map<String, Object> queryBlackWhiteList(String appName, TenantCommonExt ext) {
        List<AgentWhiteList> agentWhites = agentListWhitelist(ext);
        List<Long> ids = agentWhites.stream().map(AgentWhiteList::getWlistId).collect(Collectors.toList());
        List<AgentWhiteList> agentWhiteLists = agentWhites.stream().distinct().collect(Collectors.toList());
        Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(30);
        // 获取所有白名单，是否有局部属性
        List<String> existWhite = Lists.newArrayList();
        Map<String, List<WhitelistResult>> whitelistMap;

        boolean isCheckDuplicateName = Boolean.parseBoolean(
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_WHITE_LIST_DUPLICATE_NAME_CHECK));
        if (isCheckDuplicateName) {
            List<String> armdString = agentWhiteLists.stream().map(AgentWhiteList::getInterfaceName).collect(
                Collectors.toList());
            existWhite = whiteListService.getExistWhite(armdString, Lists.newArrayList());
            // todo 这里再获取一次，感觉很多余，但是不改上面的逻辑，所有这里数据再次从新获取，之后可以重构下
            WhitelistSearchParam param = new WhitelistSearchParam();
            param.setTenantId(ext.getTenantId());
            param.setUseYn(1);
            List<WhitelistResult> results = whiteListDAO.getList(param);
            whitelistMap = results.stream().collect(
                Collectors.groupingBy(e -> e.getInterfaceName() + "@@" + e.getType()));
        } else {
            // 获取所有白名单，是否有全局属性
            WhitelistSearchParam param = new WhitelistSearchParam();
            param.setTenantId(ext.getTenantId());
            param.setIsGlobal(true);
            param.setUseYn(1);
            List<WhitelistResult> results = whiteListDAO.getList(param);
            whitelistMap = results.stream()
                .collect(Collectors.groupingBy(e -> WhitelistUtil.buildWhiteId(e.getType(), e.getInterfaceName())));
        }

        // 获取所有生效效应，是否有局部应用
        WhitelistEffectiveAppSearchParam searchParam = new WhitelistEffectiveAppSearchParam();
        searchParam.setTenantId(ext.getTenantId());
        // 只加开启的生效应用
        searchParam.setWlistIds(ids);
        List<WhitelistEffectiveAppResult> appResults = whitelistEffectiveAppDao.getList(searchParam);
        Map<String, List<WhitelistEffectiveAppResult>> appResultsMap = appResults.stream()
            .collect(Collectors.groupingBy(e -> WhitelistUtil.buildWhiteId(e.getType(), e.getInterfaceName())));

        List<Map<String, Object>> wListsResult = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(agentWhiteLists)) {
            for (AgentWhiteList agentWhiteList : agentWhiteLists) {
                String type = agentWhiteList.getType();
                String interfaceName = agentWhiteList.getInterfaceName();
                // 是否有全局
                String id = WhitelistUtil.buildWhiteId(agentWhiteList.getSourceType(), interfaceName);
                if ("2".equals(type)) {
                    if (StringUtils.contains(interfaceName, "#")) {
                        interfaceName = StringUtils.substringBefore(interfaceName, "#");
                    }
                }
                Map<String, Object> whiteItemNew = new HashMap<String, Object>();
                whiteItemNew.put("TYPE", type);
                whiteItemNew.put("INTERFACE_NAME", interfaceName);
                if (isCheckDuplicateName) {
                    if (existWhite.stream().filter(e -> e.equals(id)).count() == 1) {
                        List<WhitelistResult> list = whitelistMap.get(id);
                        if (CollectionUtils.isNotEmpty(list)) {
                            whiteItemNew.put("isGlobal", list.get(0).getIsGlobal());
                        } else {
                            whiteItemNew.put("isGlobal", true);
                        }
                    } else if (existWhite.stream().filter(e -> e.equals(id)).count() > 1) {
                        whiteItemNew.put("isGlobal", false);
                    } else {
                        whiteItemNew.put("isGlobal", true);
                    }

                } else {
                    // 是否有全局
                    List<WhitelistResult> list = whitelistMap.get(id);
                    whiteItemNew.put("isGlobal", CollectionUtils.isNotEmpty(list));
                }
                //生效应用
                List<WhitelistEffectiveAppResult> appLists = appResultsMap.get(id);
                whiteItemNew.put("appNames", CollectionUtils.isNotEmpty(appLists) ?
                    appLists.stream().map(WhitelistEffectiveAppResult::getEffectiveAppName).distinct()
                        .collect(Collectors.toList())
                    : Lists.newArrayList());
                wListsResult.add(whiteItemNew);
            }
        }
        // 新版黑名单
        List<AgentBlacklistVO> newBlacklist = this.getNewBlackList(ext);
        // 老版黑名单
        List<Map<String, Object>> blackList = this.getBlackList(ext);

        resultMap.put("wLists", wListsResult);
        resultMap.put("bLists", blackList);
        resultMap.put("newBlists", newBlacklist);
        return resultMap;
    }

    private List<Map<String, Object>> getBlackList(TenantCommonExt tenantCommonExt) {
        List<TBList> tbLists = tbListMntDao.getAllEnabledBlockList(tenantCommonExt.getTenantId(), tenantCommonExt.getEnvCode());
        if (CollectionUtils.isEmpty(tbLists)) {
            return Lists.newArrayList();
        }
        return tbLists.stream().map(tbList -> {
            Map<String, Object> map = new HashMap<>();
            map.put("REDIS_KEY", tbList.getRedisKey());
            return map;
        }).collect(Collectors.toList());
    }

    private List<AgentBlacklistVO> getNewBlackList(TenantCommonExt tenantCommonExt) {
        List<BlacklistResult> results = blackListDAO.getAllEnabledBlockList(null, tenantCommonExt);
        ApplicationQueryParam param = new ApplicationQueryParam();
        if (CollectionUtils.isEmpty(results)) {
            return Lists.newArrayList();
        }
        param.setTenantId(tenantCommonExt.getTenantId());
        param.setEnvCode(tenantCommonExt.getEnvCode());
        List<ApplicationDetailResult> detailResults = applicationDAO.getApplicationList(param);

        Map<Long, List<BlacklistResult>> redisMap = results.stream()
            .collect(Collectors.groupingBy(BlacklistResult::getApplicationId));

        Map<Long, List<ApplicationDetailResult>> detailResultMap = detailResults.stream()
            .collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationId));
        List<AgentBlacklistVO> vos = Lists.newArrayList();
        for (Long id : redisMap.keySet()) {
            List<ApplicationDetailResult> app = detailResultMap.get(id);
            if (CollectionUtils.isEmpty(app)) {
                continue;
            }
            AgentBlacklistVO vo = new AgentBlacklistVO();
            vo.setAppName(app.get(0).getApplicationName());
            List<BlacklistResult> blacklist = redisMap.get(id);
            if (CollectionUtils.isNotEmpty(blacklist)) {
                vo.setBlacklists(blacklist.stream().map(BlacklistResult::getRedisKey).collect(Collectors.toList()));
            } else {
                vo.setBlacklists(Lists.newArrayList());
            }
            vos.add(vo);
        }
        return vos;
    }

    private List<AgentWhiteList> agentListWhitelist(TenantCommonExt ext) {

        List<String> list = applicationDAO.queryIdsByNameAndTenant(Lists.newArrayList(),
            ext != null ? ext.getTenantId() : null, ext != null ? ext.getEnvCode() : null);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        List<Map<String, Object>> maps = whiteListDAO.getWhiteListByAppIds(list);
        return maps.stream().map(it -> {
                AgentWhiteList whiteListDTO = new AgentWhiteList();
                whiteListDTO.setInterfaceName((String)it.get("interfaceName"));
                whiteListDTO.setType(getType(Integer.parseInt((String)it.get("type"))));
                whiteListDTO.setSourceType((String)it.get("type"));
                // 过滤生效应用用
                whiteListDTO.setWlistId((Long)it.get("wlistId"));
                return whiteListDTO;
            }
        ).collect(Collectors.toList());
    }

    private String getType(int dbType) {
        String type;
        switch (dbType) {
            case 1:
                type = "http";
                break;
            case 2:
                type = "dubbo";
                break;
            case 3:
                type = "rabbitmq";
                break;
            default:
                type = "unknow";
                break;
        }
        return type;
    }

}
