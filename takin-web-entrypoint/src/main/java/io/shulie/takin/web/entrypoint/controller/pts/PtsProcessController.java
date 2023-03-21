package io.shulie.takin.web.entrypoint.controller.pts;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pts.IdRequest;
import io.shulie.takin.web.biz.pojo.request.pts.PtsActivityQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pts.PtsDebugRecordQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pts.PtsSceneRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsDebugRecordDetailResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsDebugRecordResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsSceneResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.pts.PtsParseTools;
import io.shulie.takin.web.biz.service.pts.PtsProcessService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author junshi
 * @ClassName PtsProcessController
 * @Description
 * @createTime 2023年03月15日 16:11
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "pts")
@Api(tags = "pts-脚本调试")
@Slf4j
public class PtsProcessController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PtsProcessService ptsProcessService;

    @Value("${script.temp.path}")
    private String tempPath;

    @ApiOperation("解析jmx文件")
    @PostMapping("/parse/jmx")
    public PtsSceneResponse parseJmx(MultipartFile file) {
        PtsSceneResponse sceneResponse = new PtsSceneResponse();
        if(file.isEmpty()) {
            sceneResponse.getMessage().add("上传文件不能为空");
            return sceneResponse;
        }
        String originalFilename = null;
        try {
            originalFilename = URLEncoder.encode(file.getOriginalFilename(), "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            sceneResponse.getMessage().add("解析文件名失败");
            log.error("解析文件名失败={}", e.getMessage());
            return sceneResponse;
        }
        if(originalFilename == null || !originalFilename.endsWith(".jmx")) {
            sceneResponse.getMessage().add("上传文件格式不正确，仅支持.jmx结尾的文件");
            return sceneResponse;
        }
        //保存文件到本地
        File destDir = new File(tempPath + "/pts");
        destDir.mkdirs();
        File destFile = new File(tempPath + "/pts/"+originalFilename);
        if(destFile.exists()) {
            destFile.delete();
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            sceneResponse.getMessage().add("上传文件失败");
            log.error("上传文件失败={}", e.getMessage());
            return sceneResponse;
        }
        //解析文件为对象
        return PtsParseTools.parseJmxFile(tempPath + "/pts/"+originalFilename);
    }

    @ApiOperation(value = "保存业务流程")
    @PostMapping("/process/add")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_CREATE2
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.CREATE
    )
    public BusinessFlowDetailResponse saveProcess(@Valid @RequestBody PtsSceneRequest request) {
        return ptsProcessService.saveProcess(request);
    }

    @ApiOperation("查询业PTS务活动列表")
    @GetMapping("/businessActivity/page/list")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ActivityListResponse> pageActivities(@Valid PtsActivityQueryRequest queryRequest) {
        ActivityQueryRequest request = new ActivityQueryRequest();
        return activityService.pageActivities(request);
    }

    @ApiOperation("PTS业务活动详情")
    @GetMapping("/businessActivity/detail")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
            needAuth = ActionTypeEnum.QUERY
    )
    public PtsSceneResponse detailActivity(@RequestParam(name = "id") Long id) {
        return ptsProcessService.detailProcess(id);
    }

    @ApiOperation("发起调试")
    @PostMapping("/process/debug")
    public void debugScene(@Valid @RequestBody IdRequest request) {

    }

    @ApiOperation("API调试列表")
    @GetMapping("/process/debug/record/list")
    public List<PtsDebugRecordResponse> debugRecordList(@Valid PtsDebugRecordQueryRequest request) {
        return null;
    }

    @ApiOperation("API调试详情")
    @GetMapping("/process/debug/record/detail")
    public PtsDebugRecordDetailResponse debugRecordDetail(@Valid IdRequest request) {
        return null;
    }
}
