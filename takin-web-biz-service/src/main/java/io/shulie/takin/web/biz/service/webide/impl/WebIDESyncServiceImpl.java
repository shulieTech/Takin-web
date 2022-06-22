package io.shulie.takin.web.biz.service.webide.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.fastdebug.RequestTypeEnum;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugDoDebugRequest;
import io.shulie.takin.web.biz.pojo.request.webide.WebIDESyncScriptRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowThreadResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugResponse;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import io.shulie.takin.web.biz.service.webide.WebIDESyncService;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: 南风
 * @Date: 2022/6/20 1:52 下午
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

    @Autowired
    private ReportService reportService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncScript(WebIDESyncScriptRequest request) {
        List<ScriptDebugDoDebugRequest> scriptDeploys = new ArrayList<>();

        String url = request.getCallbackAddr();
        Integer workRecordId = request.getWorkRecordId();
        Boolean initData = true;
        try {
            List<WebIDESyncScriptRequest.ActivityFIle> flies = request.getFile();
            if (flies.size() > 0) {
                //todo 目前webIDE只会传jmx文件
                List<WebIDESyncScriptRequest.ActivityFIle> jmxs = flies.stream().
                        filter(t -> t.getType().equals(0))
                        .collect(Collectors.toList());

                jmxs.forEach(jmx -> {
                    //处理文件路径,改成控制台可用的路径
                    String path = jmx.getPath();
                    String uid = UUID.randomUUID().toString();
                    String sourcePath = tmpFilePath + "/" + uid + "/" + jmx.getName();
                    FileUtil.copy(path,sourcePath,false);

                    BusinessFlowParseRequest bus = new BusinessFlowParseRequest();
                    FileManageUpdateRequest file = new FileManageUpdateRequest();
                    file.setFileName(jmx.getName());
                    file.setFileType(jmx.getType());
                    file.setDownloadUrl(sourcePath);
                    file.setUploadId(uid);
                    file.setIsDeleted(0);
                    bus.setScriptFile(file);
                    //解析脚本
                    BusinessFlowDetailResponse parseScriptAndSave = sceneService.parseScriptAndSave(bus);
                    BusinessFlowDetailResponse detail = sceneService.getBusinessFlowDetail(parseScriptAndSave.getId());
                    if (Objects.nonNull(detail)) {
                        ScriptDebugDoDebugRequest scriptDebugDoDebug = new ScriptDebugDoDebugRequest();
                        scriptDebugDoDebug.setScriptDeployId(detail.getScriptDeployId());
                        scriptDebugDoDebug.setConcurrencyNum(request.getConcurrencyNum());
                        scriptDebugDoDebug.setRequestNum(request.getRequestNum());
                        scriptDeploys.add(scriptDebugDoDebug);

                        //todo 目前webIDE只会有一个脚本节点，后面多个脚本节点需要加入匹配逻辑
                        String xpathMd5 = detail.getScriptJmxNodeList().get(0).getValue();
                        BusinessFlowThreadResponse groupDetail = sceneService.getThreadGroupDetail(parseScriptAndSave.getId(),
                                xpathMd5);
                        if (Objects.nonNull(groupDetail)) {
                            List<ScriptJmxNode> threadScriptJmxNodes = groupDetail.getThreadScriptJmxNodes();
                            List<ScriptJmxNode> parseNodes = new ArrayList<>();
                            //递归解析出所有需要匹配的节点
                            parse(threadScriptJmxNodes, parseNodes);
                            if (parseNodes.size() > 0) {
                                //给节点匹配应用入口
                                List<WebIDESyncScriptRequest.ApplicationActivity> application = request.getApplication();
                                if (application.size() > 0) {
                                    //匹配
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

        } catch (Exception e) {
            log.error("[创建业务场景失败] e", e);
            initData = false;
        } finally {
            log.info("[创建业务场景] 回调");
            String msg = initData ? "创建业务场景成功" : "创建业务场景失败";
            callback(url, msg, workRecordId,"FATAL");
        }


        //启动调试
        if (scriptDeploys.size() > 0) {
            List<Long> debugIds = new ArrayList<>();
            scriptDeploys.forEach(item -> {
                ScriptDebugResponse debug = scriptDebugService.debug(item);
                debugIds.add(debug.getScriptDebugId());
            });


            debugIds.forEach(debugId -> {
                webIDESyncThreadPool.execute(() -> {
                    boolean loop = true;
                    do {
                        ScriptDebugDetailResponse debugDetail = scriptDebugService.getById(debugId);
                        log.info("[debug状态] 回调,debugId:{},debugDetail:{}",debugId, JSON.toJSONString(debugDetail));
                        callback(url, JSON.toJSONString(debugDetail), workRecordId,"");
                        if (Objects.isNull(debugDetail)) {
                            break;
                        }
                        if ( debugDetail.getStatus() == 5) {
                            //发送报告错误日志
                            Long cloudReportId = debugDetail.getCloudReportId();
                            ReportDetailOutput report = reportService.getReportByReportId(cloudReportId);
                            if(Objects.nonNull(report)){
                                String resourceId = report.getResourceId();
                                Long jobId = report.getJobId();
                                String errorFilePath = tmpFilePath+"/ptl/"+resourceId+"/"+jobId;
                                if(FileUtil.exist(errorFilePath)){
                                    String errorContext = FileUtil.readUtf8String(errorFilePath);
                                    log.info("[发送报告错误日志] resourceId:{},jobId:{}",resourceId,jobId);
                                    callback(url, errorContext, workRecordId,"ERROR");
                                }
                            }
                            loop = false;
                        }
                        if(debugDetail.getStatus() == 4 ){
                            loop = false;
                        }
                    } while (loop);
                });
            });
        }


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
                    request.setEntrance(activity.getMethod() + "|" + activity.getServiceName());
                    request.setActivityName(activity.getActivityName());
                    request.setSamplerType(node.getSamplerType());
                    request.setBusinessType(BusinessTypeEnum.NORMAL_BUSINESS.getType());
                    list.add(request);
                }
            }
        }
        return list;
    }


    private void callback(String url, String msg, Integer workRecordId,String level) {
        url = url + "?source=kzt&level="+level+"&work_record_id=" + workRecordId;
        new HttpRequest(url)
                .method(Method.POST)
                .contentType(RequestTypeEnum.TEXT.getDesc())
                .timeout(HttpGlobalConfig.getTimeout()).
                body(msg)
                .execute()
                .body();
    }
}
