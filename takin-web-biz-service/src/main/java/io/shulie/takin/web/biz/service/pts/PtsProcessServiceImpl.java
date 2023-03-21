package io.shulie.takin.web.biz.service.pts;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import io.shulie.takin.adapter.api.entrypoint.file.CloudFileApi;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.pts.PtsSceneRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsSceneResponse;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.common.enums.scene.SceneTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pts.PtsProcessDAO;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.util.Date;

/**
 * @author junshi
 * @ClassName PtsProcessServiceImpl
 * @Description
 * @createTime 2023年03月16日 15:26
 */
@Service
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessFlowDetailResponse saveProcess(PtsSceneRequest request) {
        request.setProcessName(calcProcessName(request.getId(), request.getProcessName()));
        String jsonApi = JSON.toJSONString(request);
        UploadResponse uploadResponse;
        try {
            String jmxString = PtsBuildTools.parseJmxString(jsonApi);
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
        return sceneService.parseScriptAndSave(businessFlowParseRequest);
    }

    @Override
    public PtsSceneResponse detailProcess(Long id) {
        BusinessFlowDetailResponse businessFlowDetail = sceneService.getBusinessFlowDetail(id);
        FileManageResponse scriptFile = businessFlowDetail.getScriptFile();
        if(scriptFile == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "没有业务流程id找到对应脚本！");
        }
        return PtsParseTools.parseJmxFile(scriptFile.getUploadPath());
    }

    private String calcProcessName(Long processId, String processName) {
        SceneQueryParam queryParam = new SceneQueryParam();
        queryParam.setSceneName(processName);
        if(!ptsProcessDAO.existProcessQueryByName(processId, queryParam)) {
            return processName;
        }
        return processName + "_" + DateUtil.formatDateTime(new Date());
    }
}
