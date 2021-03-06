package io.shulie.takin.web.biz.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.LinkDetectionConstants;
import com.pamirs.takin.common.constant.PressureOperateEnum;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.entity.domain.vo.TDataBuild;
import com.pamirs.takin.entity.domain.vo.TLinkDetection;
import io.shulie.takin.web.biz.common.CommonService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class PressureReadyService extends CommonService {

    public static void main(String[] args) {
        String url = "https://192.168.122.26:9090/sto-web-taobaotrack/track/taoBaoTrack!trackForJson.action";
        String newUrl = "";
        if (url.startsWith("http://")) {
            newUrl = url.substring(0, 7);
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            newUrl = url.substring(0, 8);
            url = url.substring(8);
        } else {
            // TODO error
        }
        int index = -1;
        if ((index = url.indexOf("/")) != -1) {
            String ip = url.substring(0, url.indexOf("/"));
            System.out.println(newUrl + ip);
        }

    }

    //=================================================== ????????????   ===================================================

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * ??????: ??????????????????
     *
     * @param applicationIds ????????????id
     * @author shulie
     * @date 2018/7/4 19:12
     */
    public void debugSwitch(List<Map<String, Object>> switchList, PressureOperateEnum pressureOperateEnum)
        throws TakinModuleException {
        if (switchList.isEmpty()) {
            throw new TakinModuleException(TakinErrorEnum.BUILDDATA_DEBUG_SWITCH_PARAMLACK);
        }

        ConcurrentMap<String, List<Map<String, Object>>> debugSwitch = switchList.stream().distinct().collect(
            Collectors.groupingByConcurrent(map -> map.get("debugSwitch").toString()));
        debugSwitch.forEach((debugSwitchString, mapList) -> {
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("applicationIdLists",
                mapList.stream().map(map -> map.get("applicationId").toString()).collect(Collectors.toList()));
            if ("on".equals(debugSwitchString)) {
                paramMap.put("status", "2");
            } else if ("off".equals(debugSwitchString)) {
                paramMap.put("status", "0");
            }
            switch (pressureOperateEnum) {
                case DATA_BUILD_DEBUG_SWITCH:
                    TDataBuildDao.debugSwitchUpdate(paramMap);
                    return;
                case PRESSURE_CHECK_DEBUG_SWITCH:
                    TLinkDetectionDao.debugSwitchUpdateByCheck(paramMap);
                    return;
            }
        });

    }

    /**
     * ??????: ????????????????????????????????????
     *
     * @param paramMap ??????????????????,??????????????????,????????????,???????????????
     * @return ??????, ???????????????????????????, ??????????????????????????????????????????
     * @author shulie
     */
    public PageInfo<TDataBuild> queryBuildinfo(Map<String, Object> paramMap) {
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TDataBuild> queryBuildinfo = TDataBuildDao.queryBuildinfo(paramMap);

        return new PageInfo<TDataBuild>(queryBuildinfo.isEmpty() ? Lists.newArrayList() : queryBuildinfo);
    }

    /**
     * ??????: ????????????????????????
     *
     * @param map ????????????
     * @throws TakinModuleException
     * @author shulie
     */
    public void updateScriptExcuteStatus(Map<String, Object> map) {
        TDataBuildDao.updateScriptExcuteStatus(map);
    }

    /**
     * ??????: ???????????????????????????id????????????????????????
     *
     * @param applicationName ????????????
     * @param scriptTpye      ????????????
     * @return ????????????????????????????????????????????????
     * @author shulie
     * @version v1.0
     * @Date:
     */
    public Map<String, Object> queryScriptExcuteStatus(String applicationId, String scriptType) {
        Map<String, Object> queryScriptExcuteStatus = TDataBuildDao.queryScriptExcuteStatus(applicationId, scriptType);

        String lastSuccessTime = MapUtils.getString(queryScriptExcuteStatus, switchFunLastSuccessTime(scriptType));
        Date transferTime = DateUtils.transferTime(lastSuccessTime);
        queryScriptExcuteStatus.put(switchFunLastSuccessTime(scriptType), lastSuccessTime);
        return switchCaseFromMap(queryScriptExcuteStatus);
    }

    /**
     * ??????????????????
     *
     * @return
     * @version v1.0
     * @Date:
     */
    private String switchFunLastSuccessTime(String scriptType) {
        switch (scriptType) {
            case "1":
                return "DDL_LAST_SUCCESS_TIME";

            case "2":
                return "CACHE_LAST_SUCCESS_TIME";

            case "3":
                return "READY_LAST_SUCCESS_TIME";

            case "4":
                return "BASIC_LAST_SUCCESS_TIME";

            case "5":
                return "CLEAN_LAST_SUCCESS_TIME";
        }
        return scriptType;
    }

    /**
     * ??????: ??????????????????????????????
     *
     * @author shulie
     * @date 2018/6/16 12:26
     * @version v1.0
     * @Date:
     */
    public void executeScriptPreCheck(Map<String, Object> paraMap) throws TakinModuleException {
        String applicationId = MapUtils.getString(paraMap, "applicationId");
        String scriptType = MapUtils.getString(paraMap, "scriptType");
        if (StringUtils.isEmpty(applicationId) && StringUtils.isEmpty(scriptType)) {
            throw new TakinModuleException(TakinErrorEnum.BUILDDATA_EXECUTE_SCRIPT_PARAMLACK);
        }
        Map<String, Object> cacheExpTimeMap = applicationDAO.queryCacheExpTime(applicationId);
        if ("0".equals(MapUtils.getString(cacheExpTimeMap, LinkDetectionConstants.CACHE_EXP_TIME)) && "2".equals(
            scriptType)) {
            throw new TakinModuleException(TakinErrorEnum.BUILDDATA_EXECUTE_SCRIPTPATH_NOTCACHE);
        }
        //????????????????????????????????????(1)
        updateScriptStatus(applicationId, scriptType, "1");
        runShellTaskExecutor.execute(executeScript(applicationId, scriptType));
    }

    /**
     * ??????: ????????????
     *
     * @param applicationId ??????id
     * @param scriptType    ????????????
     * @throws InterruptedException ??????
     * @throws IOException ??????
     * @throws TakinModuleException
     * @author shulie
     */

    public Runnable executeScript(String applicationId, String scriptType) {
        String scriptPath = applicationDAO.selectScriptPath(applicationId, scriptType);
        String applicationName = applicationDAO.selectApplicationName(applicationId);
        return () -> {
            if (StringUtils.isEmpty(scriptPath)) {
                updateScriptStatus(applicationId, scriptType, "3");
            } else {
                asynExecuteScriptThreadPool.execute(asyncExcuteScript(scriptPath, applicationId, scriptType));
            }
        };
    }

    /**
     * ??????: ????????????????????????????????????(1)
     *
     * @param applicationId ??????id
     * @param scriptType    ????????????
     * @author shulie
     * @version v1.0
     * @Date:
     */
    private void updateScriptStatus(String applicationId, String scriptType, String resultStatus) {
        Map<String, Object> scriptStatusMap = Maps.newHashMap();
        scriptStatusMap.put("applicationId", applicationId);
        scriptStatusMap.put("resultStatus", resultStatus);
        scriptStatusMap.put("scriptType", scriptType);
        TDataBuildDao.updateScriptExcuteStatus(scriptStatusMap);
    }

    //=================================================== ????????????   ===================================================

    /**
     * ??????: ????????????????????????
     *
     * @param scriptPath ????????????
     * @throws Exception ??????
     * @author shulie
     * @version v1.0
     * @Date:
     */

    public Runnable asyncExcuteScript(String scriptPath, String applicationId, String scriptType) {

        return () -> {
            Process ps = null;
            InputStream inputStream = null;
            ByteArrayOutputStream bytes = null;
            try {

                ps = Runtime.getRuntime().exec(getBasePath() + scriptPath);
                ps.waitFor(); // ????????????????????????????????????
                inputStream = ps.getErrorStream();
                bytes = new ByteArrayOutputStream();
                byte[] bs = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(bs)) > 0) {
                    bytes.write(bs, 0, len);
                }
                LOGGER.info("ERROR INFO : " + new String(bytes.toByteArray()));
                String result = new String(bytes.toByteArray());
                if (StringUtils.isBlank(result)) {
                    updateScriptStatus(applicationId, scriptType, "2");
                }
            } catch (Exception e) {
                Map<String, Object> scriptParamMap = Maps.newHashMap();
                scriptParamMap.put("resultStatus", "3");
                scriptParamMap.put("scriptType", scriptType);
                scriptParamMap.put("applicationId", applicationId);
                TDataBuildDao.updateScriptExcuteStatus(scriptParamMap);
                LOGGER.error("??????????????????????????????", e);
            } finally {
                closeAll(null, inputStream, bytes, ps);
            }
        };
    }

    /**
     * ??????: ????????????????????????
     *
     * @param linkName ????????????
     * @throws TakinModuleException
     * @author shulie
     * @version v1.0
     * @Date:
     */
    public void batchClean(String applicationIds) {
        Arrays.stream(applicationIds.split(",")).filter(e -> StringUtils.isNotEmpty(e)).distinct().forEach(
            applicationId -> runShellTaskExecutor.execute(executeScript(applicationId, "5")));
    }

    /**
     * ??????: ??????????????????????????????
     *
     * @param applicationId ??????id
     * @return ??????????????????
     * @author shulie
     */
    public Map<String, Object> pressureTestCheck(String applicationId) {
        return TLinkDetectionDao.pressureTestCheck(applicationId);
    }

    /**
     * ??????: ??????????????????????????????(??????????????????)
     *
     * @param linkName        ????????????
     * @param applicationName ????????????
     * @return ??????????????????
     * @author shulie
     */
    public PageInfo<TLinkDetection> queryChecklist(Map<String, Object> paramMap) {
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TLinkDetection> checklist = TLinkDetectionDao.queryChecklist(paramMap);

        return new PageInfo<TLinkDetection>(checklist.isEmpty() ? Lists.newArrayList() : checklist);
    }

    /**
     * ??????: ???????????????????????????????????????
     *
     * @param applicationId ??????id
     * @return ?????????????????????????????????
     * @throws TakinModuleException
     * @author shulie
     */
    public Callable<Map<String, Object>> queryCheckShadowlib(String applicationId) {

        Map<String, Object> shadowLibMap = TDataBuildDao.queryCheckShadowlib(applicationId);

        return new Callable<Map<String, Object>>() {

            @Override
            public Map<String, Object> call() throws Exception {

                if (shadowLibMap == null || shadowLibMap.isEmpty()) {

                    LOGGER.error("????????????id??????????????????????????????");
                    Map<String, Object> newHashMap = Maps.newHashMap();
                    newHashMap.put("applicationId", applicationId);
                    newHashMap.put("success", "N");
                    newHashMap.put("errorMsg", "????????????id??????????????????????????????");
                    TLinkDetectionDao.updateShadowLibResult(JSON.toJSONString(newHashMap), "3", applicationId);
                    return newHashMap;
                }

                if (StringUtils.equals("2", MapUtils.getString(shadowLibMap, LinkDetectionConstants.DDL_BUILD_STATUS))
                    && StringUtils.equals("2",
                    MapUtils.getString(shadowLibMap, LinkDetectionConstants.READY_BUILD_STATUS))
                    && StringUtils.equals("2",
                    MapUtils.getString(shadowLibMap, LinkDetectionConstants.BASIC_BUILD_STATUS))
                ) { // 2??????????????????
                    // ????????????????????????????????????????????????????????????
                    Map<String, Object> resultMap = new HashMap<String, Object>();
                    resultMap.put("success", "Y");
                    resultMap.putAll(shadowLibMap);
                    TLinkDetectionDao.updateShadowLibResult("", "2", applicationId);
                    return resultMap;
                } else {
                    //????????????????????????????????????????????????
                    Map<String, Object> resultMap = new HashMap<String, Object>();
                    resultMap.put("success", "N");
                    resultMap.putAll(shadowLibMap);
                    TLinkDetectionDao.updateShadowLibResult(JSON.toJSONString(resultMap), "3", applicationId);
                    return resultMap;
                }
            }
        };
    }

    /**
     * ??????: ???????????????????????????(??????dubbo???http)
     *
     * @param applicationName ????????????
     * @return ?????????????????????
     * @throws TakinModuleException
     * @author shulie
     */
    public Callable<Map<String, Object>> queryCheckWList(String applicationId) {

        return new Callable<Map<String, Object>>() {

            @Override
            public Map<String, Object> call() throws Exception {

                List<Map<String, Object>> wlists = TLinkDetectionDao.queryWLisByApplicationId(applicationId);

                if (wlists == null || wlists.isEmpty()) {
                    LOGGER.error("????????????id???????????????????????????");
                    Map<String, Object> newHashMap = Maps.newHashMap();
                    newHashMap.put("applicationId", applicationId);
                    newHashMap.put("success", "N");
                    newHashMap.put("errorMsg", "????????????id???????????????????????????");
                    TLinkDetectionDao.updateWlistErrorContent(JSON.toJSONString(newHashMap), "3", applicationId);
                    return newHashMap;
                }

                Map<String, Object> resultMap = Maps.newHashMap();

                List<Map<String, Object>> wListResultList = Lists.newArrayList();
                Map<String, Object> wListResultMap = Maps.newHashMap();

                List<Map<String, Object>> dubboResultList = Lists.newArrayList();
                Map<String, Object> dubboResultMap = Maps.newHashMap();

                Map<String, Object> exceptionMap = Maps.newHashMap();
                List<Map<String, Object>> httpExceptionList = Lists.newArrayList();
                List<Map<String, Object>> dubboExceptionList = Lists.newArrayList();
                List<String> httpExceptionInterfaceList = Lists.newArrayList();
                List<String> dubboExceptionInterfaceList = Lists.newArrayList();
                boolean exceptionFlag = false;

                Map<String, Object> httpExceptionMap = Maps.newHashMap();
                httpExceptionMap.put("applicationId", applicationId);
                httpExceptionMap.put("applicationName", wlists.get(0).get("applicationName"));
                httpExceptionMap.put("type", "1");
                httpExceptionMap.put("typeName", "http");
                httpExceptionMap.put("errorMsg", "????????????????????????http??????,??????????????????,?????????????????????");

                Map<String, Object> dubboExceptionMap = Maps.newHashMap();
                dubboExceptionMap.put("applicationId", applicationId);
                dubboExceptionMap.put("applicationName", wlists.get(0).get("applicationName"));
                dubboExceptionMap.put("type", "2");
                dubboExceptionMap.put("typeName", "dubbo");
                dubboExceptionMap.put("errorMsg", "????????????????????????dubbo??????,??????????????????,?????????????????????");

                for (Map<String, Object> map : wlists) {
                    String type = MapUtils.getString(map, LinkDetectionConstants.TYPE); //http
                    String interfaceName = MapUtils.getString(map, LinkDetectionConstants.INTERFACE_NAME);
                    String applicationName = MapUtils.getString(map, LinkDetectionConstants.APPLICATION_NAME);

                    if (StringUtils.isNotEmpty(interfaceName) && "1".equals(type)) {
                        String url = interfaceName;
                        String newUrl = "";
                        if (url.startsWith("http://")) {
                            newUrl = url.substring(0, 7);
                            url = url.substring(7);
                        } else if (url.startsWith("https://")) {
                            newUrl = url.substring(0, 8);
                            url = url.substring(8);
                        } else {
                            continue;
                        }
                        int index = -1;

                        String interfaceCall = "";

                        //??????????????????
                        String appName = "";
                        if ((index = url.indexOf("/")) != -1) {
                            String ip = url.substring(0, url.indexOf("/"));
                            interfaceCall = newUrl + ip;
                            int nameCount = appearNumber(url, "/");
                            if (0 != nameCount) {
                                int appIndex = url.indexOf("/") + 1;
                                if (1 == nameCount) {
                                    if (url.contains("?")) {
                                        appName = url.substring(appIndex, url.indexOf("?"));
                                    } else {
                                        appName = url.substring(appIndex);
                                    }
                                } else if (nameCount > 1) {
                                    appName = url.substring(appIndex, url.indexOf("/", url.indexOf("/") + 1));
                                }
                            }
                        }
                        Map<String, Object> responseMap = Maps.newHashMap();

                        String httpInterfaceNameOld = "";
                        try {
                            httpInterfaceNameOld = interfaceCall + "/maxPlanckcheckTestY";
                            responseMap = restTemplate.getForObject(httpInterfaceNameOld, Map.class);
                        } catch (Exception e) {
                            LOGGER.error(
                                httpInterfaceNameOld + " PressureReadyService.queryCheckWList ????????????????????????,??????????????????{}", e);
                        }

                        if (!"Y".equals(MapUtils.getString(responseMap, "success"))) {
                            httpInterfaceNameOld = interfaceCall + "?maxPlanckcheckTest=Y";
                            try {
                                responseMap = restTemplate.getForObject(httpInterfaceNameOld, Map.class);
                            } catch (Exception e) {
                                LOGGER.error(
                                    httpInterfaceNameOld + " PressureReadyService.queryCheckWList ????????????????????????,??????????????????{}",
                                    e);
                                httpExceptionMap.put("errorMsg", httpExceptionMap.get("errorMsg") + e.getMessage());
                            }
                        }

                        if (!"Y".equals(MapUtils.getString(responseMap, "success"))) {
                            httpInterfaceNameOld = interfaceCall + "/" + appName + "/maxPlanckcheckTestY";
                            try {
                                responseMap = restTemplate.getForObject(httpInterfaceNameOld, Map.class);
                            } catch (Exception e) {
                                LOGGER.error(
                                    httpInterfaceNameOld + " PressureReadyService.queryCheckWList ????????????????????????,??????????????????{}",
                                    e);
                                httpExceptionMap.put("errorMsg", httpExceptionMap.get("errorMsg") + e.getMessage());
                            }
                        }

                        if (!"Y".equals(MapUtils.getString(responseMap, "success"))) {
                            httpInterfaceNameOld = interfaceCall + "/" + appName + "?maxPlanckcheckTest=Y";
                            try {
                                responseMap = restTemplate.getForObject(httpInterfaceNameOld, Map.class);
                            } catch (Exception e) {
                                LOGGER.error(
                                    httpInterfaceNameOld + " PressureReadyService.queryCheckWList ????????????????????????,??????????????????{}",
                                    e);
                                httpExceptionInterfaceList.add(interfaceName);
                                exceptionFlag = true;
                                httpExceptionMap.put("errorMsg", httpExceptionMap.get("errorMsg") + e.getMessage());
                                continue;
                            }
                        }

                        if (!"Y".equals(MapUtils.getString(responseMap, "success"))) {
                            wListResultMap.put("applicationId", applicationId);
                            wListResultMap.put("applicationName", applicationName);
                            wListResultMap.put("interfaceName", interfaceName);
                            wListResultMap.put("type", "1");
                            wListResultMap.put("typeName", "http");
                            wListResultMap.putAll(responseMap);
                            wListResultList.add(wListResultMap);
                        }
                        wListResultMap = Maps.newHashMap();
                    } else if (StringUtils.isNotEmpty(interfaceName) && "2".equals(type)) { //dubbo
                        String dubboInterfaceName = StringUtils.substringBeforeLast(interfaceName, applicationName)
                            + applicationName + "/maxPlanckcheckTestY";
                        Map<String, Object> responseMap = Maps.newHashMap();
                        try {
                            responseMap = restTemplate.getForObject(dubboInterfaceName, Map.class);
                        } catch (Exception e) {
                            LOGGER.error("PressureReadyService.queryCheckWList ????????????????????????,??????????????????{}", e);
                            //                            dubboExceptionInterfaceList.add(interfaceName);
                            //                            exceptionFlag = true;
                            //                            continue;
                        }

                        if (!"Y".equals(MapUtils.getString(responseMap, "success"))) {
                            String dubboInterfaceNameOld = StringUtils.substringBeforeLast(interfaceName,
                                applicationName) + applicationName + "?maxPlanckcheckTest=Y";
                            try {
                                responseMap = restTemplate.getForObject(dubboInterfaceNameOld, Map.class);
                            } catch (Exception e) {
                                LOGGER.error("PressureReadyService.queryCheckWList ????????????????????????,??????????????????{}", e);
                                dubboExceptionInterfaceList.add(interfaceName);
                                exceptionFlag = true;
                                continue;

                            }
                        }

                        if (!"Y".equals(MapUtils.getString(responseMap, "success"))) {
                            dubboResultMap.put("applicationId", applicationId);
                            dubboResultMap.put("applicationName", applicationName);
                            dubboResultMap.put("interfaceName", interfaceName);
                            dubboResultMap.put("type", "2");
                            dubboResultMap.put("typeName", "dubbo");
                            dubboResultMap.putAll(responseMap);
                            dubboResultList.add(dubboResultMap);
                        }
                        dubboResultMap = Maps.newHashMap();
                    }
                }

                httpExceptionMap.put("interfaceName", httpExceptionInterfaceList);
                dubboExceptionMap.put("interfaceName", dubboExceptionInterfaceList);

                if ((!wListResultList.isEmpty() || !dubboResultList.isEmpty()) && !exceptionFlag) {
                    resultMap.put("http", wListResultList);
                    resultMap.put("dubbo", dubboResultList);
                    resultMap.put("success", "N");
                    TLinkDetectionDao.updateWlistErrorContent(JSON.toJSONString(resultMap), "3", applicationId);
                    return resultMap;
                } else if (exceptionFlag) {
                    // ????????????
                    List<Object> httpExceptionResultList = Lists.newArrayList();
                    httpExceptionResultList.add(httpExceptionMap);
                    List<Object> dubboExceptionResultList = Lists.newArrayList();
                    dubboExceptionResultList.add(dubboExceptionMap);

                    exceptionMap.put("http",
                        httpExceptionInterfaceList.isEmpty() ? Lists.newArrayList() : httpExceptionResultList);
                    exceptionMap.put("dubbo",
                        dubboExceptionInterfaceList.isEmpty() ? Lists.newArrayList() : dubboExceptionResultList);
                    exceptionMap.put("success", "N");
                    TLinkDetectionDao.updateWlistErrorContent(JSON.toJSONString(exceptionMap), "3", applicationId);
                    return exceptionMap;
                } else {
                    Map<String, Object> newHashMap = Maps.newHashMap();
                    newHashMap.put("applicationId", applicationId);
                    newHashMap.put("applicationName",
                        MapUtils.getString(wlists.get(0), LinkDetectionConstants.APPLICATION_NAME));
                    newHashMap.put("success", "Y");
                    TLinkDetectionDao.updateWlistErrorContent("", "2", applicationId);
                    return newHashMap;
                }
            }
        };
    }

    /**
     * ??????: ??????????????????
     *
     * @param applicationName ????????????
     * @return ????????????????????????
     * @throws TakinModuleException
     * @author shulie
     */
    public Callable<Map<String, Object>> queryCheckCache(String applicationId) {

        Map<String, Object> cacheStatusMap = TDataBuildDao.queryCacheStatus(applicationId);

        return new Callable<Map<String, Object>>() {

            @Override
            public Map<String, Object> call() throws Exception {

                if (cacheStatusMap == null || cacheStatusMap.isEmpty()) {
                    //?????????????????????????????????????????????????????????
                    Map<String, Object> newHashMap = Maps.newHashMap();
                    newHashMap.put("applicationId", applicationId);
                    newHashMap.put("success", "N");
                    newHashMap.put("errorMsg", "????????????id???????????????????????????????????????????????????????????????");
                    TLinkDetectionDao.updateCacheResult(JSON.toJSONString(cacheStatusMap), "3", applicationId);
                    return newHashMap;
                }
                String applicationName = MapUtils.getString(cacheStatusMap, LinkDetectionConstants.APPLICATION_NAME);
                Map<String, Object> cacheExpTimeMap = applicationDAO.queryCacheExpTime(applicationId);
                String cacheExpTimeS = MapUtils.getString(cacheExpTimeMap, LinkDetectionConstants.CACHE_EXP_TIME);

                if ("0".equals(cacheExpTimeS)) {
                    Map<String, Object> cacheResultMap = Maps.newHashMap();
                    cacheResultMap.put("applicationId", applicationId);
                    cacheResultMap.put("applicationName", applicationName);
                    cacheResultMap.put("timeLeft", "+???");
                    cacheResultMap.put("cacheExecuteStatus", 2);
                    cacheResultMap.put("lastSuccessTime",
                        MapUtils.getString(cacheStatusMap, LinkDetectionConstants.CACHE_LAST_SUCCESS_TIME));
                    cacheResultMap.put("success", "Y");
                    TLinkDetectionDao.updateCacheResult("", "2", applicationId);
                    return cacheResultMap;
                }

                long cacheExpTime = Long.valueOf(cacheExpTimeS) * 1000;
                String cacheStatus = MapUtils.getString(cacheStatusMap, LinkDetectionConstants.CACHE_BUILD_STATUS);
                String cacheExcuteTime = MapUtils.getString(cacheStatusMap,
                    LinkDetectionConstants.CACHE_LAST_SUCCESS_TIME);

                long cacheLastSuccessSeconds = 0L;
                if (StringUtils.isNotEmpty(cacheExcuteTime)) {
                    cacheLastSuccessSeconds = TimeUnit.SECONDS.toSeconds(
                        DateUtils.transferTime(cacheExcuteTime).getTime());
                }
                long nowSeconds = TimeUnit.SECONDS.toSeconds(new Date().getTime());

                Map<String, Object> cacheResultMap = Maps.newHashMap();
                cacheResultMap.put("applicationId", applicationId);
                cacheResultMap.put("applicationName", applicationName);
                cacheResultMap.put("cacheExecuteStatus", cacheStatus);
                cacheResultMap.put("lastSuccessTime", cacheExcuteTime);
                cacheResultMap.put("timeLeft", (cacheExpTime - (nowSeconds - cacheLastSuccessSeconds)) / 1000 + "s");
                if (StringUtils.equals("2", cacheStatus)) { //2 ??????????????????????????????
                    cacheResultMap.put("success", nowSeconds - cacheLastSuccessSeconds < cacheExpTime ? "Y" : "N");
                } else {
                    cacheResultMap.put("success", "N");
                }

                if (StringUtils.equals("Y", MapUtils.getString(cacheResultMap, "success"))) {
                    TLinkDetectionDao.updateCacheResult("", "2", applicationId);
                } else {
                    TLinkDetectionDao.updateCacheResult(JSON.toJSONString(cacheResultMap), "3", applicationId);
                }
                return cacheResultMap;
            }
        };
    }

    /**
     * ??????: ????????????id????????????
     *
     * @param linkName ????????????
     * @return ??????????????????????????????
     * @throws TakinModuleException
     * @throws Exception
     * @author shulie
     */
    public Map<String, Object> batchCheck(String applicationIds) throws TakinModuleException, Exception {

        Map<String, Object> resultMap = Maps.newHashMap();

        Collection<Callable<Map<String, Object>>> shadowlibWorkTasks = Collections.synchronizedCollection(
            new ArrayList<Callable<Map<String, Object>>>());
        Collection<Callable<Map<String, Object>>> cacheWorkTasks = Collections.synchronizedCollection(
            new ArrayList<Callable<Map<String, Object>>>());
        Collection<Callable<Map<String, Object>>> wListWorkTasks = Collections.synchronizedCollection(
            new ArrayList<Callable<Map<String, Object>>>());

        List<String> applicationIdsList = Arrays.stream(applicationIds.split(",")).filter(
            e -> StringUtils.isNotEmpty(e)).distinct().collect(Collectors.toList());

        for (String applicationId : applicationIdsList) {
            shadowlibWorkTasks.add(queryCheckShadowlib(applicationId)); // ?????????????????????????????????
            cacheWorkTasks.add(queryCheckCache(applicationId)); // ????????????
            wListWorkTasks.add(queryCheckWList(applicationId)); // ???????????????
        }

        List<Map<String, Object>> shadowlibWorktask = executeWorktask(runShellTaskExecutor, shadowlibWorkTasks);
        List<Map<String, Object>> cacheWorktask = executeWorktask(runShellTaskExecutor, cacheWorkTasks);
        List<Map<String, Object>> wListWorktask = executeWorktask(runShellTaskExecutor, wListWorkTasks);

        boolean flag = true;
        for (Map<String, Object> shadowlibWorkMap : shadowlibWorktask) {
            if (shadowlibWorkMap.containsValue("N")) {
                flag = false;
                break;
            }
        }

        for (Map<String, Object> cacheWorkMap : cacheWorktask) {
            if (cacheWorkMap.containsValue("N")) {
                flag = false;
                break;
            }
        }

        for (Map<String, Object> wlistWorkMap : wListWorktask) {
            if (wlistWorkMap.containsValue("N")) {
                flag = false;
                break;
            }
        }

        if (flag) {
            Map<String, Object> newHashMap = Maps.newHashMap();
            newHashMap.put("success", "Y");
            return newHashMap;
        } else {
            resultMap.put("shadowlibCheck", shadowlibWorktask);
            resultMap.put("cacheCheck", cacheWorktask);
            resultMap.put("wlistCheck", wListWorktask);
            resultMap.put("success", "N");
            return resultMap;
        }
    }

}
