package io.shulie.takin.web.biz.service.pts;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.common.util.FileUtils;
import io.shulie.takin.adapter.api.entrypoint.file.CloudFileApi;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.pts.PtsSceneRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.pts.*;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.common.enums.scene.SceneTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pts.PtsProcessDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Value("${apache.jmeter.path:/data/apps/apache-jmeter-5.4.1}")
    private String apacheJmeterPath;

    private final String PTS_KEY ="TAKIN:web:pts.processDetail:%s";

    private final String PTS_DEBUG_KEY ="TAKIN:web:pts.processDebug:%s";

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
    public ResponseResult debugProcess(Long id) {
        //调试中，
        String key = String.format(PTS_DEBUG_KEY, id);
        if(!redisTemplate.opsForValue().setIfAbsent(key, "1", 65, TimeUnit.SECONDS)) {
            return ResponseResult.fail("发起调试失败：业务活动已在调试中", "");
        }
        //调用jmeter命令，运行jmx脚本；
        BusinessFlowDetailResponse businessFlowDetail = getJmxPathById(id);
        String uploadPath = businessFlowDetail.getScriptFile().getUploadPath();
        String cmdDir = uploadPath.substring(0, uploadPath.lastIndexOf("/"));
        String fileName = uploadPath.substring(uploadPath.lastIndexOf("/") + 1);
        StringBuffer command = new StringBuffer("cd " + cmdDir);
        command.append(" && rm -rf jmeter.log");
        command.append(" && rm -rf result.xml");
        command.append(" && \\cp * " + apacheJmeterPath +"/lib/ext");
        command.append(" && "+apacheJmeterPath+"/bin/jmeter -n -t " + cmdDir+"/"+fileName);
        File file = new File(cmdDir + "/" + fileName);
        log.info("Check File Exist={}",  file.exists());
        ptsDebugAsync.runJmeterCommand(command.toString());
        log.info("PTS调试，执行启动jmeter命令={}", command);
        return ResponseResult.success(command.toString());
    }

    @Override
    public PtsDebugResponse getDebugRecord(Long id) {
        PtsDebugResponse response = new PtsDebugResponse();
        try {
            BusinessFlowDetailResponse businessFlowDetail = getJmxPathById(id);
            String uploadPath = businessFlowDetail.getScriptFile().getUploadPath();
            //判断文件是否已存在
            String cmdDir = uploadPath.substring(0, uploadPath.lastIndexOf("/"));
            File resultFile = new File(cmdDir + "/result.xml");
            boolean hasResult = false;
            long startTime = System.currentTimeMillis();
            //最多查询35s，前端1min超时，要小于前端超时时间
            while (!hasResult && (System.currentTimeMillis() - startTime) < 35 * 1000) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                File logFile = new File(cmdDir + "/jmeter.log");
                if (logFile.exists()) {
                    //读取日志文件，如果出现结尾关键字 "Summariser: summary =]"  标识已结束
                    Map<String, String> readMap = FileUtils.readTextFileContentAndException(logFile);
                    String exception = readMap.get("exception");
                    if (StringUtils.isNotBlank(exception)) {
                        response.setHasException(true);
                        response.setExceptionMessage("调试存在异常：" + exception + "，点击【查看日志】看详情");
                    }
                    hasResult = Boolean.parseBoolean(readMap.get("hasSummary"));
                }
            }
            response.setHasResult(true);
            List<PtsDebugRecordResponse> recordList = new ArrayList<>();
            List<PtsDebugRecordDetailResponse> detailList = PtsParseResultToObjectTools.parseResultFile(resultFile, 0);
            for (PtsDebugRecordDetailResponse detail : detailList) {
                PtsDebugRecordResponse record = new PtsDebugRecordResponse();
                record.setApiName(StringUtils.isNotBlank(detail.getGeneral().getRequestUrl()) ? detail.getGeneral().getRequestUrl() : detail.getApiName());
                record.setRequestTime(detail.getRequestTime());
                record.setRequestCost(detail.getRequestCost() != null ? detail.getRequestCost() + "ms" : "-");
                record.setResponseStatus(detail.getResponseStatus());
                record.setResponseCode(detail.getGeneral().getResponseCode());
                record.setDetail(detail);
                recordList.add(record);
            }
            //如果解析result.xml有数据，标识无异常
            if (CollectionUtils.isNotEmpty(detailList)) {
                response.setHasException(false);
            }
            response.setRecords(recordList);
        } catch (Exception e) {
            log.error("getDebugRecord Error={}", e.getMessage());
        } finally {
            String key = String.format(PTS_DEBUG_KEY, id);
            redisTemplate.opsForValue().set(key, "1", 1, TimeUnit.SECONDS);
        }
        return response;
    }

    @Override
    public String getDebugLog(Long id) {
        BusinessFlowDetailResponse businessFlowDetail = getJmxPathById(id);
        String uploadPath = businessFlowDetail.getScriptFile().getUploadPath();
        String cmdDir = uploadPath.substring(0, uploadPath.lastIndexOf("/"));
        File file = new File(cmdDir + "/jmeter.log");
        if(!file.exists()) {
            return "日志文件不存在";
        }
        return com.pamirs.takin.common.util.FileUtils.readTextFileContentAndException(file).get("content");
    }
    private BusinessFlowDetailResponse getJmxPathById(Long id) {
        String key = String.format(PTS_KEY, id);
        Object value = redisTemplate.opsForValue().get(key);
        BusinessFlowDetailResponse businessFlowDetail;
        if(value != null) {
            businessFlowDetail = JSON.parseObject((String) value, BusinessFlowDetailResponse.class);
        } else {
            businessFlowDetail = sceneService.getBusinessFlowDetail(id);
            FileManageResponse scriptFile = businessFlowDetail.getScriptFile();
            if (scriptFile == null) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "没有业务流程id找到对应脚本！");
            }
            redisTemplate.opsForValue().set(key, JSON.toJSONString(businessFlowDetail), 10, TimeUnit.SECONDS);
        }
        return businessFlowDetail;
    }
}
