package io.shulie.takin.web.biz.service.webide.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.fastdebug.RequestTypeEnum;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityResultQueryRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptDebugRequestRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugDoDebugRequest;
import io.shulie.takin.web.biz.pojo.request.webide.WebIDESyncScriptRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityInfoResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityNameResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListWebIDEResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowThreadResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugRequestListResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.linkmanage.LinkManageService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import io.shulie.takin.web.biz.service.webide.WebIDESyncService;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptDebugStatusEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.data.dao.linkmanage.SceneDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMntMapper;
import io.shulie.takin.web.data.mapper.mysql.WebIdeSyncScriptMapper;
import io.shulie.takin.web.data.model.mysql.WebIdeSyncScriptEntity;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: ??????
 * @Date: 2022/6/20 1:52 ??????
 */
@Service
@Slf4j
public class WebIDESyncServiceImpl implements WebIDESyncService {

    @Resource
    private ThreadPoolExecutor webIDESyncThreadPool;

    @Resource
    private SceneService sceneService;

    @Autowired
    private ScriptDebugService scriptDebugService;

    @Value("${file.upload.tmp.path:/tmp/takin/}")
    private String tmpFilePath;


    @Value("${takin.data.path}")
    private String baseNfsPath;

    @Autowired
    private ReportService reportService;

    @Resource
    private WebIdeSyncScriptMapper webIdeSyncScriptMapper;

    @Resource
    private ActivityService activityService;

    @Resource
    private LinkManageService linkManageService;

    @Resource
    private SceneDAO sceneDAO;

    @Resource
    private ApplicationMntMapper mntMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncScript(WebIDESyncScriptRequest request) {
        List<ScriptDebugDoDebugRequest> scriptDeploys = new ArrayList<>();

        String url = request.getCallbackAddr();
        Long workRecordId = request.getWorkRecordId();
        boolean initData = false;
        WebIdeSyncScriptEntity entity = new WebIdeSyncScriptEntity();
        entity.setWorkRecordId(workRecordId);
        entity.setRequest(JSON.toJSONString(request));
        try {
            log.info("[webIDE????????????] workRecordId:{}", workRecordId);
            List<WebIDESyncScriptRequest.ActivityFIle> flies = request.getFile();
            if (flies.size() > 0) {
                //todo ??????webIDE?????????jmx??????
                List<WebIDESyncScriptRequest.ActivityFIle> jmxs = flies.stream().
                        filter(t -> t.getType().equals(0))
                        .collect(Collectors.toList());

                jmxs.forEach(jmx -> {
                    //??????????????????,??????????????????????????????
                    String path = baseNfsPath + "/" + jmx.getPath();
                    String uid = UUID.randomUUID().toString();
                    String sourcePath = tmpFilePath + "/" + uid + "/" + jmx.getName();
                    FileUtil.copy(path, sourcePath, false);

                    BusinessFlowParseRequest bus = new BusinessFlowParseRequest();
                    FileManageUpdateRequest file = new FileManageUpdateRequest();
                    file.setFileName(jmx.getName());
                    file.setFileType(jmx.getType());
                    file.setDownloadUrl(sourcePath);
                    file.setUploadId(uid);
                    file.setIsDeleted(0);
                    bus.setScriptFile(file);
                    //????????????
                    BusinessFlowDetailResponse parseScriptAndSave = sceneService.parseScriptAndSave(bus);
                    BusinessFlowDetailResponse detail = sceneService.getBusinessFlowDetail(parseScriptAndSave.getId());
                    if (Objects.nonNull(detail)) {
                        entity.setBusinessFlowId(parseScriptAndSave.getId());
                        entity.setScriptDeployId(detail.getScriptDeployId());
                        ScriptDebugDoDebugRequest scriptDebugDoDebug = new ScriptDebugDoDebugRequest();
                        scriptDebugDoDebug.setScriptDeployId(detail.getScriptDeployId());
                        scriptDebugDoDebug.setConcurrencyNum(request.getConcurrencyNum());
                        scriptDebugDoDebug.setRequestNum(request.getRequestNum());
                        scriptDeploys.add(scriptDebugDoDebug);

                        //todo ??????webIDE??????????????????????????????????????????????????????????????????????????????
                        String xpathMd5 = detail.getScriptJmxNodeList().get(0).getValue();
                        BusinessFlowThreadResponse groupDetail = sceneService.getThreadGroupDetail(parseScriptAndSave.getId(),
                                xpathMd5);
                        if (Objects.nonNull(groupDetail)) {
                            List<ScriptJmxNode> threadScriptJmxNodes = groupDetail.getThreadScriptJmxNodes();
                            List<ScriptJmxNode> parseNodes = new ArrayList<>();
                            //??????????????????????????????????????????
                            parse(threadScriptJmxNodes, parseNodes);
                            if (parseNodes.size() > 0) {
                                //???????????????????????????
                                List<WebIDESyncScriptRequest.ApplicationActivity> application = request.getApplication();
                                if (application.size() > 0) {
                                    //??????
                                    List<SceneLinkRelateRequest> matchList = matchBuild(application, parseNodes,
                                            parseScriptAndSave.getId());

                                    if (matchList.size() > 0) {
                                        matchList.forEach(t -> sceneService.matchActivity(t));
                                    }
                                }
                            }
                        }
                    }
                });
            }
            initData = true;
        } catch (Exception e) {
            log.error("[????????????????????????] workRecordId:{},e", workRecordId, e);
            entity.setErrorMsg(e.toString());
            entity.setErrorStage("???????????????????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } finally {
            log.info("[??????????????????] ??????");
            String msg = initData ? "????????????????????????" : "????????????????????????";
            entity.setIsError(initData ? 0 : 1);
            String level = initData ? "INFO" : "FATAL";
            callback(url, msg, workRecordId, level);
        }


        //????????????
        if (initData && scriptDeploys.size() > 0) {

            List<Long> debugIds = new ArrayList<>();
            scriptDeploys.forEach(item -> {
                boolean debugFlag = false;
                String errorMsg = "";
                try {
                    ScriptDebugResponse debug = scriptDebugService.debug(item);
                    if (debug.getScriptDebugId() != null) {
                        entity.setScriptDebugId(debug.getScriptDebugId());
                        debugIds.add(debug.getScriptDebugId());
                        debugFlag = true;
                    } else {
                        log.error("[??????????????????] workRecordId:{},error:{}", workRecordId, debug.getErrorMessages().get(0));
                        entity.setErrorMsg(debug.getErrorMessages().get(0));
                        entity.setErrorStage("??????????????????");
                        errorMsg = entity.getErrorMsg();
                    }


                } catch (Exception e) {
                    log.error("[??????????????????] workRecordId:{},e", workRecordId, e);
                    entity.setErrorMsg(e.toString());
                    entity.setErrorStage("??????????????????");
                    errorMsg = e.getMessage();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                } finally {
                    String msg = debugFlag ? "??????????????????" : "??????????????????, ????????????:{" + errorMsg + "}";
                    log.info("[??????????????????] workRecordId,:{},?????? :{}", workRecordId, msg);
                    String level = debugFlag ? "INFO" : "FATAL";
                    callback(url, msg, workRecordId, level);
                    if(!debugFlag){
                        delScene(entity.getBusinessFlowId());
                    }
                }
            });


            debugIds.forEach(debugId -> {
                webIDESyncThreadPool.execute(() -> {
                    boolean loop = true;
                    do {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ScriptDebugDetailResponse debugDetail = scriptDebugService.getById(debugId);
                        log.info("[debug????????????] workRecordId:{},debugId:{}", workRecordId, debugId);
                        if (Objects.isNull(debugDetail)) {
                            break;
                        }
                        String level = "INFO";
                        String msg = ScriptDebugStatusEnum.getDesc(debugDetail.getStatus());
                        if (debugDetail.getStatus() == 5) {
                            level = "ERROR";
                            msg = msg + ", ??????????????????:{" + debugDetail.getRemark() + "}";
                            //????????????????????????
                            Long cloudReportId = debugDetail.getCloudReportId();
                            ReportDetailOutput report = reportService.getReportByReportId(cloudReportId);
                            if (Objects.nonNull(report)) {
                                String resourceId = report.getResourceId();
                                Long jobId = report.getJobId();
                                String errorFilePath = tmpFilePath + "/ptl/" + resourceId + "/" + jobId;
                                if (FileUtil.exist(errorFilePath)) {
                                    String errorContext = FileUtil.readUtf8String(errorFilePath);
                                    log.info("[????????????????????????] workRecordId:{},resourceId:{},jobId:{}", workRecordId, resourceId, jobId);
                                    callback(url, errorContext, workRecordId, level);
                                }
                            }
                            loop = false;
                        }
                        if (debugDetail.getStatus() == 4) {
                            loop = false;
                            PageScriptDebugRequestRequest req = new PageScriptDebugRequestRequest();
                            req.setScriptDebugId(debugId);
                            req.setCurrent(0);
                            req.setPageSize(10);
                            PagingList<ScriptDebugRequestListResponse> pageDetail = scriptDebugService.pageScriptDebugRequest(req);
                            if (pageDetail != null) {
                                List<ScriptDebugRequestListResponse> list = pageDetail.getList();
                                msg += ", ????????????: {" + JSON.toJSONString(list) + "}";
                            }

                        }
                        callback(url, msg, workRecordId, level);
                    } while (loop);
                    delScene(entity.getBusinessFlowId());
                });
            });
        }


        webIDESyncThreadPool.execute(() -> saveSyncDetail(entity));
    }


    private void parse(List<ScriptJmxNode> threadScriptJmxNodes, List<ScriptJmxNode> list) {
        if (threadScriptJmxNodes.size() > 0) {
            threadScriptJmxNodes.forEach(item -> {
                if (item.getChildren() != null) {
                    parse(item.getChildren(), list);
                } else {
                    list.add(item);
                }
            });
        }
    }

    private List<SceneLinkRelateRequest> matchBuild(List<WebIDESyncScriptRequest.ApplicationActivity> activityInfo,
                                                    List<ScriptJmxNode> parseNodes, Long id) {
        List<SceneLinkRelateRequest> list = new ArrayList<>();
        for (WebIDESyncScriptRequest.ApplicationActivity activity : activityInfo) {
            for (ScriptJmxNode node : parseNodes) {
                if (node.getRequestPath().equals((activity.getMethod() + "|" + activity.getServiceName()))) {
                    SceneLinkRelateRequest request = new SceneLinkRelateRequest();
                    request.setBusinessFlowId(id);
                    request.setIdentification(node.getIdentification());
                    request.setXpathMd5(node.getXpathMd5());
                    request.setTestName(node.getTestName());
                    request.setApplicationName(activity.getApplicationName());
                    request.setEntrance(activity.getMethod() + "|" + activity.getServiceName() + "|" + activity.getRpcType());
                    request.setActivityName(activity.getActivityName());
                    request.setSamplerType(node.getSamplerType());
                    request.setBusinessType(BusinessTypeEnum.NORMAL_BUSINESS.getType());
                    ActivityResultQueryRequest activityQuery = new ActivityResultQueryRequest();
                    activityQuery.setApplicationName(request.getApplicationName());
                    activityQuery.setEntrancePath(request.getEntrance());

                    List<ActivityListResponse> responses = activityService.queryNormalActivities(activityQuery);
                    if (responses.size() > 0) {
                        request.setBusinessActivityId(responses.get(0).getActivityId());
                    }
                    list.add(request);
                }
            }
        }
        return list;
    }


    private void callback(String url, String msg, Long workRecordId, String level) {
        url = url + "?source=Takin?????????&level=" + level + "&work_record_id=" + workRecordId;
        new HttpRequest(url)
                .method(Method.POST)
                .contentType(RequestTypeEnum.TEXT.getDesc())
                .timeout(HttpGlobalConfig.getTimeout()).
                body(msg)
                .execute()
                .body();
    }

    private void delScene(Long businessFlowId){
        SceneResult sceneDetail = sceneDAO.getSceneDetail(businessFlowId);
        if(Objects.isNull(sceneDetail)){
            return;
        }
        SceneUpdateParam update = new SceneUpdateParam();
        update.setId(businessFlowId);
        update.setIsDeleted(1);
        sceneDAO.update(update);
    }

    private void saveSyncDetail(WebIdeSyncScriptEntity entity) {
        webIdeSyncScriptMapper.insert(entity);
    }


    @Override
    public PagingList<BusinessFlowListWebIDEResponse> sceneList(BusinessFlowPageQueryRequest queryRequest) {
        PagingList<BusinessFlowListResponse> list = sceneService.getBusinessFlowList(queryRequest);
        List<BusinessFlowListResponse> flowList = list.getList();
        if (flowList.isEmpty()) {
            return PagingList.of(new ArrayList<>(), list.getTotal());
        }
        List<BusinessFlowListWebIDEResponse> collect = flowList.stream().map(item -> {
            List<BusinessActivityNameResponse> activesByFlowId = linkManageService.getBusinessActiveByFlowId(item.getId());
            BusinessFlowListWebIDEResponse convert = Convert.convert(BusinessFlowListWebIDEResponse.class, item);
            convert.setVirtualNum(0);
            convert.setNormalNum(0);
            if (CollectionUtils.isEmpty(activesByFlowId)) {
                return convert;
            }
            Map<Integer, List<BusinessActivityNameResponse>> map = CollStreamUtil.groupByKey(activesByFlowId, BusinessActivityNameResponse::getType);
            if (map.containsKey(BusinessTypeEnum.NORMAL_BUSINESS.getType())) {
                convert.setNormalNum(map.get(BusinessTypeEnum.NORMAL_BUSINESS.getType()).size());
            }
            if (map.containsKey(BusinessTypeEnum.VIRTUAL_BUSINESS.getType())) {
                convert.setNormalNum(map.get(BusinessTypeEnum.VIRTUAL_BUSINESS.getType()).size());
            }

            return convert;
        }).collect(Collectors.toList());
        return PagingList.of(collect, list.getTotal());
    }

    @Override
    public List<BusinessActivityInfoResponse> activityList(Long businessFlowId) {
        List<BusinessActivityNameResponse> activesByFlowId = linkManageService.getBusinessActiveByFlowId(businessFlowId);
        if(CollectionUtils.isEmpty(activesByFlowId)){
            return new ArrayList<>();
        }

        return activesByFlowId.stream().map(item -> {
            BusinessActivityInfoResponse convert = Convert.convert(BusinessActivityInfoResponse.class, item);
            convert.setActivityId(item.getBusinessActivityId());
            convert.setActivityName(item.getBusinessActivityName());
            if(Objects.nonNull(item.getApplicationId()) && StringUtils.isBlank(item.getApplicationName())){
                convert.setApplicationName(mntMapper.selectApplicationName(String.valueOf(item.getApplicationId())));
            }
            if(StringUtils.isBlank(convert.getEntrace())){
                return convert;
            }
            ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(convert.getEntrace());
            convert.setServiceName(entranceJoinEntity.getServiceName());
            convert.setMethodName(entranceJoinEntity.getMethodName());
            return convert;
        }).collect(Collectors.toList());
    }
}
