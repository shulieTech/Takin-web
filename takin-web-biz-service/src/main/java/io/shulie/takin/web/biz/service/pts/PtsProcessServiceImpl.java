package io.shulie.takin.web.biz.service.pts;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.common.util.FileUtils;
import io.shulie.takin.adapter.api.entrypoint.file.CloudFileApi;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.pts.PtsSceneRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.pts.*;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.LinuxHelper;
import io.shulie.takin.web.common.enums.scene.SceneTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pts.PtsProcessDAO;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.str.StringTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author junshi
 * @ClassName PtsProcessServiceImpl
 * @Description
 * @createTime 2023年03月16日 15:26
 */
@Service
@Slf4j
public class PtsProcessServiceImpl implements PtsProcessService{

    @Autowired
    private PtsProcessDAO ptsProcessDAO;

    @Autowired
    private ScriptManageService scriptManageService;

    @Autowired
    private PtsApiMatchService ptsApiMatchService;

    @Autowired
    private CloudFileApi cloudFileApi;

    @Autowired
    private SceneService sceneService;

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Resource
    private PtsDebugAsync ptsDebugAsync;

    /**
     * 缓存 业务里程id 对应 jmx的请求路径信息
     */
    private static final String PTS_KEY = "TAKIN.WEB.pts.process.%s";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessFlowDetailResponse saveProcess(PtsSceneRequest request) {
        request.setProcessName(request.getProcessName().replace(" ", ""));
        String jsonApi = JSON.toJSONString(request);
        UploadResponse uploadResponse;
        try {
            String jmxString = PtsBuildJsonToJmxTextTools.parseJmxString(jsonApi);
            //将jmxstring写入文件，并返回地址
            uploadResponse = cloudFileApi.saveJmxStringToFile(request.getProcessName(), jmxString);
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_ADD_ERROR,
                    "创建PTS脚本失败！" + e.getMessage());
        }
        BusinessFlowParseRequest businessFlowParseRequest = new BusinessFlowParseRequest();
        businessFlowParseRequest.setId(request.getId());
        businessFlowParseRequest.setSource(SceneTypeEnum.PTS_UPLOAD_SCENE.getType());
        FileManageUpdateRequest updateRequest = new FileManageUpdateRequest();
        BeanUtils.copyProperties(uploadResponse, updateRequest);
        businessFlowParseRequest.setScriptFile(updateRequest);
        BusinessFlowDetailResponse detailResponse = sceneService.parseScriptAndSave(businessFlowParseRequest);
        //更新业务流程名称 存在id，且新旧名称不一样
        if(request.getId() != null && !detailResponse.getBusinessProcessName().equals(request.getProcessName())) {
            BusinessFlowUpdateRequest nameUpdateRequest = new BusinessFlowUpdateRequest();
            nameUpdateRequest.setId(request.getId());
            nameUpdateRequest.setSceneName(request.getProcessName());
            sceneService.updateBusinessFlow(nameUpdateRequest);
        }
        detailResponse.setBusinessProcessName(request.getProcessName());
        /**
         * 自动匹配业务活动信息
         */
        sceneService.autoMatchActivity(detailResponse.getId());
        /**
         * 有新增|修改jmx信息，删除redis缓存数据
         */
        String key = String.format(PTS_KEY, detailResponse.getId());
        redisTemplate.delete(key);
        return detailResponse;
    }

    @Override
    public PtsSceneResponse detailProcess(Long id) {
        BusinessFlowDetailResponse businessFlowDetail = sceneService.getBusinessFlowDetail(id);
        FileManageResponse scriptFile = businessFlowDetail.getScriptFile();
        if(scriptFile == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "没有业务流程id找到对应脚本！");
        }
        PtsSceneResponse sceneResponse = PtsParseJmxToObjectTools.parseJmxFile(scriptFile.getUploadPath(), false);
        sceneResponse.setId(id);
        sceneResponse.setProcessName(businessFlowDetail.getBusinessProcessName());
        return sceneResponse;
    }

    @Override
    public String debugProcess(Long id) {
        //调用jmeter命令，运行jmx脚本；
        String uploadPath = getJmxPathById(id);
        String cmdDir = uploadPath.substring(0, uploadPath.lastIndexOf("/"));
        String fileName = uploadPath.substring(uploadPath.lastIndexOf("/") + 1);
        String command = "cd " + cmdDir + " && rm -rf jmeter.log result.xml && jmeter -n -t " + fileName;
        ptsDebugAsync.runJmeterCommand(command);
        log.info("PTS调试，执行启动jmeter命令={}", command);
        return command;
    }

    @Override
    public PtsDebugResponse getDebugRecord(Long id) {
        String uploadPath = getJmxPathById(id);
        PtsDebugResponse response = new PtsDebugResponse();
        //判断文件是否已存在
        String cmdDir = uploadPath.substring(0, uploadPath.lastIndexOf("/"));
        File file = new File(cmdDir + "/result.xml");
        if(!file.exists()) {
            response.setHasResult(false);
            return response;
        }
        response.setHasResult(true);
        Long queryTimestamp = System.currentTimeMillis();
        List<PtsDebugRecordResponse> recordList = new ArrayList<>();
        /**
         * 存在有文件，但没内容的情况
         * 多查询几次
         */
        File logFile = new File(cmdDir + "/jmeter.log");
        while(!response.getHasException() && recordList.size() == 0 && System.currentTimeMillis() - queryTimestamp < 6000) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                log.error("getDebugRecord InterruptedException={}", e.getMessage());
            }
            if(logFile.exists()) {
              String exception = FileUtils.readTextFileContentAndException(logFile).get("exception");
              if(StringUtils.isNotBlank(exception)) {
                  response.setHasException(true);
                  response.setExceptionMessage("调试异常："+exception+"，点击【查看日志】看详情");
              }
            }
            List<PtsDebugRecordDetailResponse> detailList = PtsParseResultToObjectTools.parseResultFile(file);
            for (PtsDebugRecordDetailResponse detail : detailList) {
                PtsDebugRecordResponse record = new PtsDebugRecordResponse();
                record.setApiName(StringUtils.isNotBlank(detail.getGeneral().getRequestUrl()) ? detail.getGeneral().getRequestUrl() : detail.getApiName());
                record.setRequestTime(detail.getRequestTime());
                record.setRequestCost(detail.getRequestCost() != null ? detail.getRequestCost() + "ms" : "-");
                record.setResponseStatus(detail.getResponseStatus());
                record.setResponseCode(detail.getGeneral().getResponseCode());
                /**
                 * 详情信息
                 */
                record.setDetail(detail);
                recordList.add(record);
            }
        }
        response.setRecords(recordList);
        return response;
    }

    @Override
    public String getDebugLog(Long id) {
        String uploadPath = getJmxPathById(id);
        String cmdDir = uploadPath.substring(0, uploadPath.lastIndexOf("/"));
        File file = new File(cmdDir + "/jmeter.log");
        if(!file.exists()) {
            return "日志文件不存在";
        }
        return com.pamirs.takin.common.util.FileUtils.readTextFileContentAndException(file).get("content");
    }

//    private String calcProcessName(Long processId, String processName) {
//        String name = StringUtils.replace(processName, " ", "");
//        SceneQueryParam queryParam = new SceneQueryParam();
//        queryParam.setSceneName(name);
//        if(!ptsProcessDAO.existProcessQueryByName(processId, queryParam)) {
//            return name;
//        }
//        return name + "_" + DateUtil.format(new Date(), "yyMMddHHmm");
//    }

    private String getJmxPathById(Long id) {
        String key = String.format(PTS_KEY, id);
        Object value = redisTemplate.opsForValue().get(key);
        if(null != value) {
            return (String) value;
        }
        BusinessFlowDetailResponse businessFlowDetail = sceneService.getBusinessFlowDetail(id);
        FileManageResponse scriptFile = businessFlowDetail.getScriptFile();
        if(scriptFile == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "没有业务流程id找到对应脚本！");
        }
        redisTemplate.opsForValue().set(key, scriptFile.getUploadPath(), 10, TimeUnit.MINUTES);
        return scriptFile.getUploadPath();
    }
}
