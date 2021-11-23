package io.shulie.takin.web.biz.service.scriptmanage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.SceneManageConstant;
import com.pamirs.takin.common.exception.ApiException;
import com.pamirs.takin.common.util.parse.UrlUtil;
import com.pamirs.takin.entity.dao.linkmanage.TBusinessLinkManageTableMapper;
import com.pamirs.takin.entity.dao.linkmanage.TSceneLinkRelateMapper;
import com.pamirs.takin.entity.dao.linkmanage.TSceneMapper;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessActiveIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import com.pamirs.takin.entity.domain.entity.linkmanage.BusinessLinkManageTable;
import com.pamirs.takin.entity.domain.entity.linkmanage.Scene;
import io.shulie.amdb.common.enums.RpcType;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.common.UploadFileDTO;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileContentParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileCopyParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileCreateByStringParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileDeleteParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileZipParamReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.cloud.sdk.model.response.engine.EnginePluginDetailResp;
import io.shulie.takin.cloud.sdk.model.response.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.linux.LinuxHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.convert.performace.TraceManageResponseConvertor;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageCreateRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptTagCreateRefRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.SupportJmeterPluginNameRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.SupportJmeterPluginVersionRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.WebPartRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageActivityResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployActivityResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageSceneManageResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageXmlContentResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.SinglePluginRenderResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.SupportJmeterPluginNameResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.SupportJmeterPluginVersionResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.WebPartResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.biz.service.linkManage.LinkManageService;
import io.shulie.takin.web.biz.utils.business.script.ScriptManageUtil;
import io.shulie.takin.web.biz.utils.exception.ScriptManageExceptionUtil;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.FeaturesConstants;
import io.shulie.takin.web.common.constant.FileManageConstant;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.constant.ScriptManageConstant;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptManageDeployStatusEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.pojo.vo.file.FileExtendVO;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.common.util.FileUtil;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.common.vo.script.ScriptDeployFinishDebugVO;
import io.shulie.takin.web.data.dao.filemanage.FileManageDAO;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.dao.linkmanage.LinkManageDAO;
import io.shulie.takin.web.data.dao.scene.SceneLinkRelateDAO;
import io.shulie.takin.web.data.dao.script.ScriptDebugDAO;
import io.shulie.takin.web.data.dao.script.ScriptManageDeployDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptFileRefDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptManageDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptTagRefDAO;
import io.shulie.takin.web.data.dao.tagmanage.TagManageDAO;
import io.shulie.takin.web.data.param.filemanage.FileManageCreateParam;
import io.shulie.takin.web.data.param.linkmanage.LinkManageQueryParam;
import io.shulie.takin.web.data.param.scriptmanage.ScriptManageDeployCreateParam;
import io.shulie.takin.web.data.param.scriptmanage.ScriptManageDeployPageQueryParam;
import io.shulie.takin.web.data.param.tagmanage.TagManageParam;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.data.result.linkmange.LinkManageResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptDeployDetailResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptFileRefResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageDeployResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptTagRefResult;
import io.shulie.takin.web.data.result.tagmanage.TagManageResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.diff.api.DiffFileApi;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author zhaoyong
 */
@Component
@Slf4j
public class ScriptManageServiceImpl implements ScriptManageService {

    /**
     * 文件上传到 cloud 的地址
     * 就是 takin cloud 域名
     */
    private String fileUploadUrl;

    @Autowired
    private DiffFileApi fileApi;
    @Autowired
    private ScriptManageDAO scriptManageDAO;
    @Autowired
    private ScriptFileRefDAO scriptFileRefDAO;
    @Autowired
    private FileManageDAO fileManageDAO;
    @Autowired
    private ScriptTagRefDAO scriptTagRefDAO;
    @Autowired
    private TagManageDAO tagManageDAO;
    //FIXME 这里不要直接用mapper，用dao，而且mapper用新版本，不带T的， 带T的逐渐要删除
    @Resource
    private TBusinessLinkManageTableMapper tBusinessLinkManageTableMapper;
    //FIXME 这里不要直接用mapper，用dao，而且mapper用新版本，不带T的， 带T的逐渐要删除

    @Autowired
    private TSceneMapper tSceneMapper;
    @Autowired
    private LinkManageService linkManageService;
    @Autowired
    private SceneManageApi sceneManageApi;

    //FIXME 这里不要直接用mapper，用dao，而且mapper用新版本，不带T的， 带T的逐渐要删除
    @Autowired
    private TSceneLinkRelateMapper tSceneLinkRelateMapper;

    @Autowired
    private BusinessLinkManageDAO businessLinkManageDAO;
    @Autowired
    private LinkManageDAO linkManageDAO;

    @Autowired
    private ScriptDebugDAO scriptDebugDAO;

    @Autowired
    private ScriptManageDeployDAO scriptManageDeployDAO;

    @Autowired
    private SceneLinkRelateDAO sceneLinkRelateDAO;

    @PostConstruct
    public void init() {
        fileUploadUrl = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_URL);
    }



    @Override
    public String getZipFileUrl(Long scriptDeployId) {
        ScriptManageDeployResult scriptManageDeployResult = scriptManageDAO.selectScriptManageDeployById(
            scriptDeployId);
        if (scriptManageDeployResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本不存在！");
        }
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployId(scriptDeployId);
        if (CollectionUtils.isEmpty(scriptFileRefResults)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "没有关联的文件！");
        }
        List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(
            Collectors.toList());
        List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(fileIds);
        //过滤掉大文件
        List<String> uploadPaths = fileManageResults.stream()
            .filter(t -> StringUtil.isNotBlank(t.getFileExtend()))
            .filter(t -> {
                JSONObject jsonObject = JsonUtil.json2bean(t.getFileExtend(), JSONObject.class);
                if (jsonObject != null) {
                    Integer bigFile = jsonObject.getInteger("isBigFile");
                    if (bigFile != null && bigFile.equals(1)) {
                        return false;
                    }
                }
                return true;
            }).map(FileManageResult::getUploadPath)
            .collect(Collectors.toList());
        String targetScriptPath = getTargetScriptPath(scriptManageDeployResult);
        String fileName = scriptManageDeployResult.getName() + ".zip";

        FileZipParamReq fileZipParamReq = new FileZipParamReq();
        fileZipParamReq.setSourcePaths(uploadPaths);
        fileZipParamReq.setTargetPath(targetScriptPath);
        fileZipParamReq.setIsCovered(false);
        fileZipParamReq.setZipFileName(fileName);
        Boolean result = fileApi.zipFile(fileZipParamReq);

        if (!result) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_THIRD_PARTY_ERROR, "takin-cloud文件打包下载失败！");
        }
        String url = null;
        try {
            url = fileUploadUrl + FileManageConstant.CLOUD_FILE_DOWN_LOAD_API + targetScriptPath + URLEncoder.encode(
                fileName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fileDir = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_USER_DATA_DIR) + WebPluginUtils
            .traceTenantCode() + Separator.Separator1.getValue();
        String[] cmds = {"curl", "-o", fileDir + "/" + fileName, "--create-dirs", "-OL", url};
        LinuxHelper.execCurl(cmds);
        return fileDir + "/" + fileName;
    }

    @Override
    public Long createScriptManage(ScriptManageDeployCreateRequest scriptManageDeployCreateRequest) {
        checkCreateScriptManageParam(scriptManageDeployCreateRequest);
        scriptManageDeployCreateRequest.setName(scriptManageDeployCreateRequest.getName().trim());
        List<ScriptManageResult> scriptManageResults = scriptManageDAO.selectScriptManageByName(
            scriptManageDeployCreateRequest.getName());
        if (CollectionUtils.isNotEmpty(scriptManageResults)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR,
                "脚本名称重复！重复脚本ID=" + scriptManageResults.get(0).getId());
        }
        uploadCreateScriptFile(scriptManageDeployCreateRequest.getFileManageCreateRequests());
        List<FileManageCreateRequest> scriptFile = scriptManageDeployCreateRequest.getFileManageCreateRequests()
            .stream().filter(o -> o.getFileType() == 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(scriptFile) || scriptFile.size() != 1) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本文件不唯一！");
        }
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH) + WebPluginUtils
            .traceTenantCode() + Separator.Separator1.getValue() + WebPluginUtils.traceEnvCode();
        ScriptCheckDTO scriptCheckDTO = checkAndUpdateScript(scriptManageDeployCreateRequest.getRefType(),
            scriptManageDeployCreateRequest.getRefValue(),
            tmpFilePath + "/" + scriptFile.get(0).getUploadId() + "/" + scriptFile.get(0).getFileName());
        if (scriptCheckDTO != null && !StringUtil.isBlank(scriptCheckDTO.getErrmsg())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, scriptCheckDTO.getErrmsg());
        }
        if (scriptCheckDTO != null && scriptCheckDTO.getIsHttp() != null && !scriptCheckDTO.getIsHttp()) {
            if (CollectionUtils.isEmpty(scriptManageDeployCreateRequest.getPluginConfigCreateRequests())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "存在不是http的业务活动，请上传并指定插件及其版本！");
            }
        }

        ScriptManageDeployCreateParam scriptManageDeployCreateParam = new ScriptManageDeployCreateParam();
        BeanUtils.copyProperties(scriptManageDeployCreateRequest, scriptManageDeployCreateParam);
        scriptManageDeployCreateParam.setStatus(0);
        scriptManageDeployCreateParam.setScriptVersion(1);

        if (CollectionUtils.isNotEmpty(scriptManageDeployCreateRequest.getPluginConfigCreateRequests())) {
            Map<String, Object> features = Maps.newHashMap();
            features.put(FeaturesConstants.PLUGIN_CONFIG,
                scriptManageDeployCreateRequest.getPluginConfigCreateRequests());
            scriptManageDeployCreateParam.setFeature(JSON.toJSONString(features));
        }
        ScriptManageDeployResult scriptManageDeployResult = null;
        try {
            scriptManageDeployResult = scriptManageDAO.createScriptManageDeploy(
                scriptManageDeployCreateParam);
            String targetScriptPath = getTargetScriptPath(scriptManageDeployResult);
            List<String> tmpFilePaths = scriptManageDeployCreateRequest.getFileManageCreateRequests().stream().map(
                o -> tmpFilePath + "/" + o.getUploadId() + "/" + o.getFileName()).collect(Collectors.toList());

            FileCopyParamReq fileCopyParamReq = new FileCopyParamReq();
            fileCopyParamReq.setTargetPath(targetScriptPath);
            fileCopyParamReq.setSourcePaths(tmpFilePaths);
            fileApi.copyFile(fileCopyParamReq);

            FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
            fileDeleteParamReq.setPaths(tmpFilePaths);
            fileApi.deleteFile(fileDeleteParamReq);

            List<FileManageCreateParam> fileManageCreateParams = getFileManageCreateParams(
                scriptManageDeployCreateRequest.getFileManageCreateRequests(), targetScriptPath);
            List<Long> fileIds = fileManageDAO.createFileManageList(fileManageCreateParams);
            scriptFileRefDAO.createScriptFileRefs(fileIds, scriptManageDeployResult.getId());
        } catch (Throwable e) {
            log.error("创建脚本出现未知异常", e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_ADD_ERROR, "创建脚本出现未知异常！请查看takin-web日志。");
        }

        return scriptManageDeployResult.getId();
    }

    /**
     * 如果前端有上传脚本文件内容，说明之前的文件是没有用了，所以如果是临时文件，将文件删除，重新创建临时文件
     */
    private void uploadCreateScriptFile(List<FileManageCreateRequest> fileManageCreateRequests) {
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH) +
            WebPluginUtils.traceTenantCode() + Separator.Separator1.getValue() + WebPluginUtils.traceEnvCode();
        if (CollectionUtils.isNotEmpty(fileManageCreateRequests)) {
            for (FileManageCreateRequest fileManageCreateRequest : fileManageCreateRequests) {
                if (fileManageCreateRequest.getIsDeleted() == 0 && fileManageCreateRequest.getFileType() == 0
                    && !StringUtil.isBlank(fileManageCreateRequest.getScriptContent())) {
                    if (!StringUtil.isBlank(fileManageCreateRequest.getUploadId())) {
                        String tempFile = tmpFilePath + "/" + fileManageCreateRequest.getUploadId() + "/"
                            + fileManageCreateRequest.getFileName();

                        FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
                        fileDeleteParamReq.setPaths(Collections.singletonList(tempFile));
                        fileApi.deleteFile(fileDeleteParamReq);
                    }
                    UUID uuid = UUID.randomUUID();
                    fileManageCreateRequest.setUploadId(uuid.toString());
                    String tempFile = tmpFilePath + "/" + fileManageCreateRequest.getUploadId() + "/"
                        + fileManageCreateRequest.getFileName();

                    FileCreateByStringParamReq fileCreateByStringParamReq = new FileCreateByStringParamReq();
                    fileCreateByStringParamReq.setFileContent(fileManageCreateRequest.getScriptContent());
                    fileCreateByStringParamReq.setFilePath(tempFile);
                    fileApi.createFileByPathAndString(fileCreateByStringParamReq);
                }
            }

        }
    }

    /**
     * 上传的脚本文件更新, 落盘
     *
     * @param fileManageCreateRequests 文件更新参数
     */
    private void uploadUpdateScriptFile(List<FileManageUpdateRequest> fileManageCreateRequests) {
        if (CollectionUtils.isEmpty(fileManageCreateRequests)) {
            return;
        }
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH) + WebPluginUtils
            .traceTenantCode() + Separator.Separator1.getValue()+WebPluginUtils.traceEnvCode();
        // 脚本文件遍历删除, 创建
        for (FileManageUpdateRequest fileManageUpdateRequest : fileManageCreateRequests) {
            if (StringUtil.isNotBlank(fileManageUpdateRequest.getScriptContent())) {
                // uploadId 不为空的, 删除
                if (StringUtil.isNotBlank(fileManageUpdateRequest.getUploadId())) {
                    String tempFile = tmpFilePath + "/" + fileManageUpdateRequest.getUploadId() + "/"
                        + fileManageUpdateRequest.getFileName();

                    FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
                    fileDeleteParamReq.setPaths(Collections.singletonList(tempFile));
                    fileApi.deleteFile(fileDeleteParamReq);
                }

                // 新建
                UUID uuid = UUID.randomUUID();
                fileManageUpdateRequest.setUploadId(uuid.toString());
                String tempFile = tmpFilePath + "/" + fileManageUpdateRequest.getUploadId() + "/"
                    + fileManageUpdateRequest.getFileName();

                if (fileManageUpdateRequest.getId() == null) {
                    log.warn("有新的脚本文件【{}】", fileManageUpdateRequest.getDownloadUrl());
                    FileCopyParamReq paramReq = new FileCopyParamReq();
                    paramReq.setTargetPath(tmpFilePath + "/" + fileManageUpdateRequest.getUploadId() + "/");
                    paramReq.setSourcePaths(Lists.newArrayList(fileManageUpdateRequest.getDownloadUrl()));
                    fileApi.copyFile(paramReq);
                } else {
                    FileCreateByStringParamReq fileCreateByStringParamReq = new FileCreateByStringParamReq();
                    fileCreateByStringParamReq.setFileContent(fileManageUpdateRequest.getScriptContent());
                    fileCreateByStringParamReq.setFilePath(tempFile);
                    fileApi.createFileByPathAndString(fileCreateByStringParamReq);
                }

            }
        }
    }

    @Override
    public ScriptCheckDTO checkAndUpdateScript(String refType, String refValue, String scriptFileUploadPath) {
        ScriptCheckDTO dto = new ScriptCheckDTO();
        if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_SCRIPT_CHECK)) {
            return dto;
        }

        ScriptCheckAndUpdateReq scriptCheckAndUpdateReq = new ScriptCheckAndUpdateReq();
        List<String> requestUrls = new ArrayList<>();
        scriptCheckAndUpdateReq.setRequest(requestUrls);
        scriptCheckAndUpdateReq.setUploadPath(scriptFileUploadPath);
        scriptCheckAndUpdateReq.setAbsolutePath(true);
        scriptCheckAndUpdateReq.setUpdate(true);
        List<BusinessLinkResult> businessActivityList = getBusinessActivity(refType, refValue);
        if (CollectionUtils.isEmpty(businessActivityList)) {
            dto.setErrmsg("找不到关联的业务活动！");
            return dto;
        }
        for (BusinessLinkResult data : businessActivityList) {
            if (StringUtil.isBlank(data.getEntrace())) {
                continue;
            }
            EntranceJoinEntity entranceJoinEntity;
            if (ActivityUtil.isNormalBusiness(data.getType())) {
                entranceJoinEntity = ActivityUtil.covertEntrance(data.getEntrace());
                if (!entranceJoinEntity.getRpcType().equals(RpcType.TYPE_WEB_SERVER + "")) {
                    dto.setIsHttp(false);
                }
            } else {
                entranceJoinEntity = ActivityUtil.covertVirtualEntrance(data.getEntrace());
            }
            String convertUrl = UrlUtil.convertUrl(entranceJoinEntity);
            requestUrls.add(convertUrl);
        }
        ResponseResult<ScriptCheckResp> scriptCheckResp = sceneManageApi.checkAndUpdateScript(scriptCheckAndUpdateReq);

        if (scriptCheckResp == null) {
            log.error("cloud检查并更新脚本出错：{}", scriptFileUploadPath);
            dto.setErrmsg("cloud检查并更新脚本出错：" + scriptFileUploadPath);
            return dto;
        }
        if (scriptCheckResp.getData() != null && CollectionUtils.isNotEmpty(scriptCheckResp.getData().getErrorMsg())) {
            StringBuilder stringBuilder = new StringBuilder();
            scriptCheckResp.getData().getErrorMsg().forEach(errorMsg -> stringBuilder.append(errorMsg).append("|"));
            stringBuilder.substring(0, stringBuilder.length() - 1);
            dto.setErrmsg(stringBuilder.toString());
        }
        return dto;
    }

    private List<BusinessLinkResult> getBusinessActivity(String refType, String refValue) {
        if (ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE.equals(refType)) {
            return businessLinkManageDAO.getListByIds(Collections.singletonList(Long.valueOf(refValue)));
        }
        if (ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE.equals(refType)) {
            List<SceneLinkRelateResult> sceneLinkRelates = sceneLinkRelateDAO.selectBySceneId(Long.valueOf(refValue));
            if (CollectionUtils.isNotEmpty(sceneLinkRelates)) {
                List<Long> businessActivityIds = sceneLinkRelates.stream().map(o -> Long.valueOf(o.getBusinessLinkId()))
                    .collect(Collectors.toList());
                //return tBusinessLinkManageTableMapper.selectBussinessLinkByIdList(businessActivityIds);
                return businessLinkManageDAO.getListByIds(businessActivityIds);
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateScriptManage(ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest) {
        // 参数校验
        this.checkUpdateScriptManageParam(scriptManageDeployUpdateRequest);
        // 名称去除空格
        scriptManageDeployUpdateRequest.setName(scriptManageDeployUpdateRequest.getName().trim());

        // 脚本文件
        List<FileManageUpdateRequest> scriptFile =
            scriptManageDeployUpdateRequest.getFileManageUpdateRequests().stream()
                .filter(o -> o.getIsDeleted() == 0 && FileTypeEnum.SCRIPT.getCode().equals(o.getFileType()))
                .collect(Collectors.toList());
        // 验证
        ScriptManageExceptionUtil.isUpdateValidError(CollectionUtils.isEmpty(scriptFile)
            || scriptFile.size() != 1, "脚本文件不唯一!");

        // 更新的脚本文件落盘
        this.uploadUpdateScriptFile(scriptFile);

        // 验证
        ScriptManageExceptionUtil.isUpdateValidError(CollectionUtils.isEmpty(scriptFile) || scriptFile.size() != 1,
            "脚本文件不唯一!");

        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH) + WebPluginUtils
            .traceTenantCode() + Separator.Separator1.getValue()+WebPluginUtils.traceEnvCode();
        // 脚本文件 url
        String scriptFileUrl;
        if (scriptFile.get(0).getId() == null) {
            scriptFileUrl = tmpFilePath + "/" + scriptFile.get(0).getUploadId() + "/" + scriptFile.get(0).getFileName();
        } else {
            List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(
                Collections.singletonList(scriptFile.get(0).getId()));
            if (CollectionUtils.isEmpty(fileManageResults)) {
                log.error("已经存在的文件丢失，fileId:{}", scriptFile.get(0).getId());
                throw ScriptManageExceptionUtil.getUpdateValidError("脚本文件不唯一!");
            }
            scriptFileUrl = fileManageResults.get(0).getUploadPath();
        }

        // cloud 那边, 检查, 更新脚本
        ScriptCheckDTO scriptCheckDTO = this.checkAndUpdateScript(scriptManageDeployUpdateRequest.getRefType(),
            scriptManageDeployUpdateRequest.getRefValue(), scriptFileUrl);

        // 判断错误信息
        if (scriptCheckDTO != null && !StringUtil.isBlank(scriptCheckDTO.getErrmsg())) {
            throw ScriptManageExceptionUtil.getUpdateValidError(scriptCheckDTO.getErrmsg());
        }

        if (scriptCheckDTO != null && scriptCheckDTO.getIsHttp() != null && !scriptCheckDTO.getIsHttp()) {
            ScriptManageExceptionUtil.isCreateValidError(
                CollectionUtils.isEmpty(scriptManageDeployUpdateRequest.getPluginConfigUpdateRequests()),
                "存在不是http的业务活动，但没有传插件!");
        }

        // 更新脚本, 创建新的脚本实例
        ScriptManageDeployResult scriptManageDeployResult = this.updateScriptAndCreateScriptDeployAndGet(
            scriptManageDeployUpdateRequest);
        String targetScriptPath = this.getTargetScriptPath(scriptManageDeployUpdateRequest, scriptManageDeployResult);

        // 这里, 脚本文件, 附件, 都放在一起了
        List<FileManageUpdateRequest> addFileManageUpdateRequests = scriptManageDeployUpdateRequest
            .getFileManageUpdateRequests().stream()
            .filter(o -> o.getIsDeleted() == 0).collect(Collectors.toList());

        // 删除大文件的记录
        Integer ifCoverBigFile = scriptManageDeployUpdateRequest.getIfCoverBigFile();
        if (ifCoverBigFile != null && ifCoverBigFile.equals(1)) {
            addFileManageUpdateRequests = addFileManageUpdateRequests.stream().filter(
                t -> t.getIsBigFile() == null || !t.getIsBigFile().equals(1)).collect(Collectors.toList());
        }

        // 插入文件记录所需参数
        List<FileManageCreateParam> fileManageCreateParams = getFileManageCreateParamsByUpdateReq(
            addFileManageUpdateRequests, targetScriptPath);

        // 创建文件记录, 获得文件ids
        List<Long> fileIds = fileManageDAO.createFileManageList(fileManageCreateParams);

        // 更新 cloud 上, 该脚本id
        // 脚本发布id
        Long oldScriptDeployId = scriptManageDeployUpdateRequest.getId();
        Long newScriptDeployId = scriptManageDeployResult.getId();
        // 注意: 两方的事务, 没有, 不能保证操作的原子性
        this.updateCloudFileByScriptId(oldScriptDeployId, newScriptDeployId, scriptManageDeployResult.getType(),
            fileManageCreateParams);

        // 文件ids与脚本发布id做关联
        scriptFileRefDAO.createScriptFileRefs(fileIds, newScriptDeployId);
    }

    /**
     * 更新 scriptId 关联的所有场景下的脚本文件名称
     *
     * @param oldScriptDeployId 老的脚本发布id
     * @param newScriptDeployId 新的脚本发布id
     * @param scriptType        脚本类型
     * @param files             所有文件
     */
    private void updateCloudFileByScriptId(Long oldScriptDeployId, Long newScriptDeployId,
        Integer scriptType, List<FileManageCreateParam> files) {
        if (oldScriptDeployId == null || CollectionUtils.isEmpty(files)) {
            return;
        }

        // 参数转换
        CloudUpdateSceneFileRequest request = new CloudUpdateSceneFileRequest();
        request.setOldScriptId(oldScriptDeployId);
        request.setNewScriptId(newScriptDeployId);
        request.setScriptType(scriptType);

        List<UploadFileDTO> uploadFiles = files.stream().map(file -> {
            UploadFileDTO uploadFileDTO = new UploadFileDTO();
            BeanUtils.copyProperties(file, uploadFileDTO);
            uploadFileDTO.setIsDeleted(0);
            String fileExtend = file.getFileExtend();
            if (StringUtils.isNotBlank(fileExtend)) {
                FileExtendVO fileExtendVO = JSONUtil.toBean(fileExtend, FileExtendVO.class);
                uploadFileDTO.setIsSplit(fileExtendVO.getIsSplit());
            }
            uploadFileDTO.setUploadedData(0L);
            uploadFileDTO.setUploadTime(DateUtil.format(file.getUploadTime(), AppConstants.DATE_FORMAT_STRING));
            return uploadFileDTO;
        }).collect(Collectors.toList());
        request.setUploadFiles(uploadFiles);

        // cloud 更新
        ResponseResult<Object> response = sceneManageApi.updateSceneFileByScriptId(request);
        if (!response.getSuccess()) {
            log.error("脚本更新 --> 对应的 cloud 场景, 脚本文件更新失败, 错误信息: {}", JSONUtil.toJsonStr(response));
            throw ScriptManageExceptionUtil.getUpdateValidError(
                String.format("对应的 cloud 脚本更新失败, 错误信息: %s", response.getError().getSolution()));
        }
    }

    /**
     * 获得目标脚本路径
     *
     * @param scriptManageDeployUpdateRequest 脚本更新所需参数
     * @param scriptManageDeployResult        脚本实例
     * @return 目标脚本路径
     */
    private String getTargetScriptPath(ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest,
        ScriptManageDeployResult scriptManageDeployResult) {
        List<String> sourcePaths = new ArrayList<>();
        // 脚本路径前缀 目录 + 脚本id + 版本
        String targetScriptPath = this.getTargetScriptPath(scriptManageDeployResult);
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH) + WebPluginUtils
            .traceTenantCode() + Separator.Separator1.getValue()+WebPluginUtils.traceEnvCode();
        List<String> tmpFilePaths = scriptManageDeployUpdateRequest.getFileManageUpdateRequests().stream().filter(
            o -> o.getIsDeleted() == 0
                && !StringUtil.isBlank(o.getUploadId())).map(
            o -> tmpFilePath + "/" + o.getUploadId() + "/" + o.getFileName()).collect(Collectors.toList());
        List<Long> existFileIds = scriptManageDeployUpdateRequest.getFileManageUpdateRequests().stream().filter(
            o -> o.getIsDeleted() == 0
                && StringUtil.isBlank(o.getUploadId()) && o.getId() != null).map(FileManageUpdateRequest::getId)
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(existFileIds)) {
            List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(existFileIds);
            fileManageResults = fileManageResults.stream().filter(t -> {
                //保留不是大文件的数据
                JSONObject json = JSON.parseObject(t.getFileExtend());
                Integer bigFile = json.getInteger("isBigFile");
                boolean isBigFile = bigFile != null && bigFile.equals(1);
                return !isBigFile;
            }).collect(Collectors.toList());
            List<String> uploadPaths = fileManageResults.stream().map(FileManageResult::getUploadPath).collect(
                Collectors.toList());
            sourcePaths.addAll(uploadPaths);
        }

        if (CollectionUtils.isNotEmpty(tmpFilePaths)) {
            sourcePaths.addAll(tmpFilePaths);
        }

        //特别注意 有一个文件复制失败 则在这个文件后面执行的文件也会失败！！
        FileCopyParamReq fileCopyParamReq = new FileCopyParamReq();
        fileCopyParamReq.setTargetPath(targetScriptPath);
        fileCopyParamReq.setSourcePaths(sourcePaths);
        fileApi.copyFile(fileCopyParamReq);
        if (CollectionUtils.isNotEmpty(tmpFilePaths)) {
            FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
            fileDeleteParamReq.setPaths(tmpFilePaths);
            fileApi.deleteFile(fileDeleteParamReq);
        }
        return targetScriptPath;
    }

    /**
     * 更新脚本, 创建新的脚本实例, 获得脚本实例结果
     *
     * @param scriptManageDeployUpdateRequest 创建, 更新, 所需参数
     * @return 获得脚本实例结果
     */
    private ScriptManageDeployResult updateScriptAndCreateScriptDeployAndGet(
        ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest) {
        // 脚本发布id
        Long scriptDeployId = scriptManageDeployUpdateRequest.getId();
        // 通过脚本发布id获得脚本实例
        ScriptManageDeployResult oldScriptManageDeployResult = scriptManageDAO.selectScriptManageDeployById(
            scriptDeployId);
        ScriptManageExceptionUtil.isUpdateValidError(oldScriptManageDeployResult == null, "更新的脚本实例不存在!");

        // 脚本id, 不是脚本发布id
        Long scriptId = oldScriptManageDeployResult.getScriptId();
        // 脚本获得
        ScriptManageResult scriptManageResult = scriptManageDAO.selectScriptManageById(scriptId);
        ScriptManageExceptionUtil.isUpdateValidError(scriptManageResult == null, "更新的脚本实例对应的脚本不存在!");

        // 声明脚本创建参数
        ScriptManageDeployCreateParam scriptManageDeployCreateParam = new ScriptManageDeployCreateParam();
        BeanUtils.copyProperties(scriptManageDeployUpdateRequest, scriptManageDeployCreateParam);
        scriptManageDeployCreateParam.setScriptId(scriptId);
        scriptManageDeployCreateParam.setStatus(ScriptManageDeployStatusEnum.NEW.getCode());

        // 版本 + 1
        // 注意: 没有锁定的话, 版本号数据不安全
        int scriptNewVersion = scriptManageResult.getScriptVersion() + 1;
        scriptManageDeployCreateParam.setScriptVersion(scriptNewVersion);

        //更新脚本状态，将脚本实例更新为历史状态
        scriptManageDAO.updateScriptVersion(scriptId, scriptNewVersion);

        // 创建新的脚本实例
        Map<String, Object> features = Maps.newHashMap();
        features.put(FeaturesConstants.PLUGIN_CONFIG, scriptManageDeployUpdateRequest.getPluginConfigUpdateRequests());
        scriptManageDeployCreateParam.setFeature(JSON.toJSONString(features));
        return scriptManageDAO.createScriptManageDeploy(scriptManageDeployCreateParam);
    }

    @Override
    public List<ScriptManageSceneManageResponse> getAllScenes(String businessFlowName) {
        List<ScriptManageSceneManageResponse> scriptManageSceneManageResponses = new ArrayList<>();
        try {
            List<BusinessFlowIdAndNameDto> businessFlowIdAndNameDtos = linkManageService.businessFlowIdFuzzSearch(
                businessFlowName);
            if (CollectionUtils.isNotEmpty(businessFlowIdAndNameDtos)) {
                scriptManageSceneManageResponses = businessFlowIdAndNameDtos.stream().map(businessFlowIdAndNameDto -> {
                    ScriptManageSceneManageResponse scriptManageSceneManageResponse
                        = new ScriptManageSceneManageResponse();
                    scriptManageSceneManageResponse.setId(businessFlowIdAndNameDto.getId());
                    scriptManageSceneManageResponse.setSceneName(businessFlowIdAndNameDto.getBusinessFlowName());
                    return scriptManageSceneManageResponse;

                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("查询业务流程异常");
        }
        if (CollectionUtils.isNotEmpty(scriptManageSceneManageResponses)) {
            List<String> systemProcessIds = scriptManageSceneManageResponses.stream().map(
                ScriptManageSceneManageResponse::getId).collect(Collectors.toList());
            List<Integer> status = new ArrayList<>();
            status.add(0);
            status.add(1);
            List<ScriptManageDeployResult> scriptManageDeployResults = scriptManageDAO.selectByRefIdsAndType(
                systemProcessIds, ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE, status);
            if (CollectionUtils.isNotEmpty(scriptManageDeployResults)) {
                Map<String, List<ScriptManageDeployResult>> systemProcessScriptMap = scriptManageDeployResults.stream()
                    .collect(Collectors.groupingBy(ScriptManageDeployResult::getRefValue));
                scriptManageSceneManageResponses.forEach(scriptManageSceneManageResponse -> {
                    List<ScriptManageDeployResult> scriptManageDeploys = systemProcessScriptMap.get(
                        scriptManageSceneManageResponse.getId());
                    scriptManageSceneManageResponse.setScriptManageDeployResponses(
                        getScriptManageDeployResponses(scriptManageDeploys));
                });
            }
        }
        return scriptManageSceneManageResponses;
    }

    private List<ScriptManageDeployActivityResponse> getScriptManageDeployResponses(
        List<ScriptManageDeployResult> scriptManageDeployResults) {
        if (CollectionUtils.isNotEmpty(scriptManageDeployResults)) {
            List<ScriptManageDeployActivityResponse> scriptManageDeployActivityResponses = new ArrayList<>();
            Map<Long, List<ScriptManageDeployResult>> collect = scriptManageDeployResults.stream().collect(
                Collectors.groupingBy(ScriptManageDeployResult::getScriptId));
            collect.forEach((k, v) -> {
                ScriptManageDeployResult scriptManageDeploy = v.stream().max(
                    Comparator.comparing(ScriptManageDeployResult::getScriptVersion)).get();
                ScriptManageDeployActivityResponse scriptManageDeployActivityResponse
                    = new ScriptManageDeployActivityResponse();
                scriptManageDeployActivityResponse.setId(scriptManageDeploy.getId());
                scriptManageDeployActivityResponse.setName(
                    scriptManageDeploy.getName() + " 版本" + scriptManageDeploy.getScriptVersion());
                scriptManageDeployActivityResponses.add(scriptManageDeployActivityResponse);
            });
            return scriptManageDeployActivityResponses;
        }
        return null;
    }

    @Override
    public List<ScriptManageActivityResponse> listAllActivities(String activityName) {
        // 业务活动的id和名字
        // 如果业务活动名称不存在的话, 获得该登录用户下的所有业务链路
        List<BusinessActiveIdAndNameDto> businessActiveIdAndNameList = linkManageService.businessActiveNameFuzzSearch(
            activityName);
        if (CollectionUtils.isEmpty(businessActiveIdAndNameList)) {
            return Collections.emptyList();
        }

        // 转换
        List<ScriptManageActivityResponse> scriptManageActivityResponses = businessActiveIdAndNameList.stream()
            .map(businessActiveIdAndNameDto -> {
                ScriptManageActivityResponse scriptManageActivityResponse = new ScriptManageActivityResponse();
                scriptManageActivityResponse.setId(businessActiveIdAndNameDto.getId());
                scriptManageActivityResponse.setBusinessActiveName(businessActiveIdAndNameDto.getBusinessActiveName());
                return scriptManageActivityResponse;
            }).collect(Collectors.toList());

        // 获得所有业务活动id
        List<String> activityIds = scriptManageActivityResponses.stream()
            .map(ScriptManageActivityResponse::getId)
            .collect(Collectors.toList());
        if (activityIds.isEmpty()) {
            return scriptManageActivityResponses;
        }

        // 脚本实例的状态, 新建, 和 调试通过的
        List<Integer> scriptManageDeployStatusList = Arrays.asList(ScriptManageDeployStatusEnum.NEW.getCode(),
            ScriptManageDeployStatusEnum.PASS.getCode());

        // 查询出业务链路对应的脚本实例列表
        List<ScriptManageDeployResult> scriptManageDeployResults = scriptManageDAO.selectByRefIdsAndType(
            activityIds, ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE, scriptManageDeployStatusList);
        if (CollectionUtils.isEmpty(scriptManageDeployResults)) {
            return scriptManageActivityResponses;
        }

        // 脚本实例, 根据业务活动id, 分组
        Map<String, List<ScriptManageDeployResult>> activityScriptMap = scriptManageDeployResults.stream()
            .filter(scriptManageDeploy -> StringUtils.isNotBlank(scriptManageDeploy.getRefValue()))
            .collect(Collectors.groupingBy(ScriptManageDeployResult::getRefValue));
        // 业务活动赋值脚本实例
        scriptManageActivityResponses.forEach(scriptManageActivityResponse -> {
            List<ScriptManageDeployResult> scriptManageDeploys = activityScriptMap.get(
                scriptManageActivityResponse.getId());
            scriptManageActivityResponse.setScriptManageDeployResponses(
                getScriptManageDeployResponses(scriptManageDeploys));
        });

        return scriptManageActivityResponses;
    }

    @Override
    public String explainScriptFile(String scriptFileUploadPath) {
        FileContentParamReq req = new FileContentParamReq();
        req.setPaths(Collections.singletonList(scriptFileUploadPath));
        return fileApi.getFileManageContextPath(req);
    }

    @Override
    public String getFileDownLoadUrl(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return "";
        }
        String[] file = filePath.split("/");
        String fileName = file[file.length - 1];
        String url = null;
        try {
            url = fileUploadUrl + FileManageConstant.CLOUD_FILE_DOWN_LOAD_API + URLEncoder.encode(filePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String fileDir = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_USER_DATA_DIR) + WebPluginUtils
            .traceTenantCode() + Separator.Separator1.getValue();
        String[] cmds = {"curl", "-o", fileDir + "/" + fileName, "--create-dirs", "-OL", url};
        LinuxHelper.execCurl(cmds);
        return fileDir + "/" + fileName;
    }

    @Override
    public List<ScriptManageDeployResponse> listScriptDeployByScriptId(Long scriptId) {
        return TraceManageResponseConvertor.INSTANCE.ofListScriptManageDeployResponse(
            scriptManageDAO.selectScriptManageDeployByScriptId(scriptId));
    }

    @Override
    public String rollbackScriptDeploy(Long scriptDeployId) {
        ScriptManageDeployResult scriptManageDeployResult = scriptManageDAO.selectScriptManageDeployById(
            scriptDeployId);
        ScriptManageResult scriptManageResult = scriptManageDAO.selectScriptManageById(
            scriptManageDeployResult.getScriptId());
        if (scriptManageDeployResult.getScriptVersion().equals(scriptManageResult.getScriptVersion())) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_ROLLBACK_VALID_ERROR, "当前脚本实例已是最新版本！");
        }
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployId(
            scriptManageDeployResult.getId());
        if (CollectionUtils.isEmpty(scriptFileRefResults)) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_ROLLBACK_VALID_ERROR, "该脚本实例没有关联文件！");
        }
        List<Long> scriptFileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(
            Collectors.toList());
        List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(scriptFileIds);
        ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest = getScriptManageDeployUpdateRequest(
            scriptManageDeployResult, fileManageResults);
        updateScriptManage(scriptManageDeployUpdateRequest);
        return scriptManageResult.getName();
    }

    @Override
    public List<ScriptManageXmlContentResponse> getScriptManageDeployXmlContent(List<Long> scriptManageDeployIds) {
        List<ScriptManageXmlContentResponse> results = new ArrayList<>();
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployIds(
            scriptManageDeployIds);
        if (CollectionUtils.isNotEmpty(scriptFileRefResults)) {
            Map<Long, List<ScriptFileRefResult>> collect = scriptFileRefResults.stream().collect(
                Collectors.groupingBy(ScriptFileRefResult::getScriptDeployId));
            collect.forEach((k, v) -> {
                ScriptManageXmlContentResponse scriptManageXmlContentResponse = new ScriptManageXmlContentResponse();
                scriptManageXmlContentResponse.setScriptManageDeployId(k);
                List<Long> fileIds = v.stream().map(ScriptFileRefResult::getFileId).collect(Collectors.toList());
                List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(fileIds);
                List<FileManageResult> scriptFiles = fileManageResults.stream().filter(o -> o.getFileType() == 0)
                    .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(scriptFiles)) {
                    scriptManageXmlContentResponse.setContent(explainScriptFile(scriptFiles.get(0).getUploadPath()));
                }
                results.add(scriptManageXmlContentResponse);
            });

        }
        return results;
    }

    @Override
    public WebPartResponse bigFileSyncRecord(WebPartRequest partRequest) {
        WebPartResponse response = new WebPartResponse();
        Long takinScriptId = partRequest.getTakinScriptId();
        if (takinScriptId == null) {
            throw new IllegalArgumentException("takinScriptId 参数不能为空！");
        }
        String originalName = partRequest.getOriginalName();
        if (StrUtil.isBlank(originalName)) {
            throw new IllegalArgumentException("originalName 参数不能为空！");
        }
        // 根据脚本id查询
        List<ScriptManageDeployResult> scriptManageDeployList = scriptManageDAO.selectScriptManageDeployByScriptId(
            takinScriptId);
        if (CollectionUtils.isEmpty(scriptManageDeployList) || Objects.isNull(scriptManageDeployList.get(0).getId())) {
            throw new IllegalArgumentException("查询takin-web脚本实例数据为空！");
        }
        // 获取版本号id
        Long scriptDeployId = scriptManageDeployList.get(0).getId();

        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployId(scriptDeployId);
        List<FileManageResult> fileManageResults;
        if (scriptFileRefResults == null) {
            fileManageResults = addScriptFile(partRequest, scriptDeployId);
            response.setFileManageResults(fileManageResults);
            return response;
        }

        List<Long> scriptFileIds = scriptFileRefResults.stream()
            .map(ScriptFileRefResult::getFileId).collect(Collectors.toList());
        fileManageResults = fileManageDAO.selectFileManageByIds(scriptFileIds);
        AtomicReference<FileManageResult> manageResult = new AtomicReference<>();
        if (CollectionUtils.isNotEmpty(fileManageResults)) {
            // 检测文件是否已经存在
            fileManageResults.forEach(result -> {
                if (result.getFileName().equals(originalName)) {
                    manageResult.set(result);
                }
            });

            // 文件路径填充
            // 获取已经存在的第一个文件, 拿到其路径
            FileManageResult file = fileManageResults.get(0);
            String uploadPath = file.getUploadPath();
            String filePath = partRequest.getFilePath();
            if (StringUtils.isNotBlank(uploadPath) && uploadPath.lastIndexOf(File.separator) > 0
                && StringUtils.isNotBlank(filePath) && filePath.lastIndexOf(File.separator) > -1) {
                // 截取倒数第一个 / 后的, 拼接上传的路径, set
                partRequest.setFilePath(uploadPath.substring(0, uploadPath.lastIndexOf(File.separator) + 1)
                    + filePath.substring(filePath.lastIndexOf(File.separator) + 1));
            }
        }

        //文件已存在，则更新文件数据
        if (manageResult.get() != null) {
            //删除本地文件数据
            List<Long> ids = Collections.singletonList(manageResult.get().getId());
            fileManageDAO.deleteByIds(ids);
            //删除本地关联关系
            List<Long> refResultIds = scriptFileRefResults.stream()
                .filter(refResult -> refResult.getFileId().equals(manageResult.get().getId()))
                .map(ScriptFileRefResult::getId).collect(Collectors.toList());
            scriptFileRefDAO.deleteByIds(refResultIds);
        }

        fileManageResults = addScriptFile(partRequest, scriptDeployId);
        response.setFileManageResults(fileManageResults);
        return response;
    }

    @Override
    public List<BusinessLinkResult> listBusinessActivityByScriptDeployId(Long scriptDeployId) {
        List<Long> businessIds = this.listBusinessActivityIdsByScriptDeployId(scriptDeployId);
        return businessIds.isEmpty() ? Collections.emptyList()
            : businessLinkManageDAO.selectBussinessLinkByIdList(businessIds);
    }

    @Override
    public List<Long> listBusinessActivityIdsByScriptDeployId(Long scriptDeployId) {
        // 根据脚本实例id获得业务活动或者业务流程id
        ScriptManageDeployResult scriptDeploy = scriptManageDeployDAO.getById(scriptDeployId);
        if (scriptDeploy == null) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "场景关联脚本不存在!");
        }

        String refType = scriptDeploy.getRefType();
        // 获得业务活动列表
        // 1 1的话, 直接查业务活动表, 2的话, 查web的scene表, 然后
        if (ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE.equals(refType)) {
            return sceneLinkRelateDAO.listBusinessLinkIdsByBusinessFlowId(
                Long.valueOf(scriptDeploy.getRefValue()));
        }

        return Collections.singletonList(Long.valueOf(scriptDeploy.getRefValue()));
    }

    @Override
    public String getZipFileNameByScriptDeployId(Long scriptDeployId) {
        // 查询脚本是否存在
        ScriptDeployDetailResult scriptDeploy = scriptManageDAO.getScriptDeployByDeployId(scriptDeployId);
        Assert.notNull(scriptDeploy, "脚本不存在！");

        // 脚本对应的列表
        List<String> filePathList = scriptManageDAO.listFilePathByScriptDeployId(scriptDeployId);
        Assert.notEmpty(filePathList, "脚本文件不存在！");
        String aFile = filePathList.get(0);
        File file = new File(aFile);
        Assert.isTrue(file.exists(), "脚本文件不存在！");
        File parentFile = file.getParentFile();

        // 根据脚本名称组装
        String absoluteZipName = String.format("%s%s%s.%s", parentFile.getParent(),
            File.separator, scriptDeploy.getName(), ProbeConstants.FILE_TYPE_ZIP);
        ZipUtil.zip(parentFile.getAbsolutePath(), absoluteZipName);
        return absoluteZipName;
    }

    private List<FileManageResult> addScriptFile(WebPartRequest partRequest, Long takinScriptId) {
        //插入新的数据到数据库
        // 创建文件记录, 获得文件ids
        List<FileManageCreateParam> fileManageCreateParams = new ArrayList<>();
        FileManageCreateParam fileParams = new FileManageCreateParam();
        fileParams.setFileName(partRequest.getOriginalName());
        fileParams.setFileSize(String.valueOf(partRequest.getFileLength()));
        setFileExtend(partRequest, fileParams);
        fileParams.setIsSplit(partRequest.getIsSplit() == null ? 0 : partRequest.getIsSplit().intValue());
        fileParams.setFileType(1);
        fileParams.setUploadPath(partRequest.getFilePath());
        fileManageCreateParams.add(fileParams);
        List<Long> fileIds = fileManageDAO.createFileManageList(fileManageCreateParams);
        // 文件ids与脚本发布id做关联
        scriptFileRefDAO.createScriptFileRefs(fileIds, takinScriptId);
        return fileManageDAO.selectFileManageByIds(fileIds);
    }

    private void setFileExtend(WebPartRequest partRequest, FileManageCreateParam fileParams) {
        Map<String, Object> fileExtend = new HashMap<>();
        if (partRequest.getIsSplit() != null) {
            fileExtend.put("isSplit", partRequest.getIsSplit());
        }
        if (partRequest.getIsOrderSplit() != null) {
            fileExtend.put("isOrderSplit", partRequest.getIsOrderSplit());
        }
        if (partRequest.getDataCount() != null) {
            fileExtend.put("dataCount", partRequest.getDataCount());
        }
        //        //大文件不显示数据行数
        //        fileExtend.put("dataCount", null);
        //大文件标识
        fileExtend.put("isBigFile", "1");
        fileParams.setFileExtend(JsonHelper.bean2Json(fileExtend));
    }

    private ScriptManageDeployUpdateRequest getScriptManageDeployUpdateRequest(
        ScriptManageDeployResult scriptManageDeployResult, List<FileManageResult> fileManageResults) {
        ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest = new ScriptManageDeployUpdateRequest();
        scriptManageDeployUpdateRequest.setId(scriptManageDeployResult.getId());
        scriptManageDeployUpdateRequest.setName(scriptManageDeployResult.getName());
        scriptManageDeployUpdateRequest.setRefType(scriptManageDeployResult.getRefType());
        scriptManageDeployUpdateRequest.setRefValue(scriptManageDeployResult.getRefValue());
        scriptManageDeployUpdateRequest.setType(scriptManageDeployResult.getType());
        List<FileManageUpdateRequest> fileManageUpdateRequests = new ArrayList<>();
        fileManageResults.forEach(fileManageResult -> {
            FileManageUpdateRequest fileManageUpdateRequest = new FileManageUpdateRequest();
            fileManageUpdateRequest.setId(fileManageResult.getId());
            fileManageUpdateRequest.setFileName(fileManageResult.getFileName());
            fileManageUpdateRequest.setFileSize(fileManageResult.getFileSize());
            fileManageUpdateRequest.setFileType(fileManageResult.getFileType());
            if (fileManageResult.getFileExtend() != null) {
                Map<String, Object> stringObjectMap = JsonHelper.json2Map(fileManageResult.getFileExtend(),
                    String.class, Object.class);
                if (stringObjectMap != null && stringObjectMap.get("dataCount") != null && !StringUtils.isBlank(
                    stringObjectMap.get("dataCount").toString())) {
                    fileManageUpdateRequest.setDataCount(Long.valueOf(stringObjectMap.get("dataCount").toString()));
                }
                if (stringObjectMap != null && stringObjectMap.get("isSplit") != null && !StringUtils.isBlank(
                    stringObjectMap.get("isSplit").toString())) {
                    fileManageUpdateRequest.setIsSplit(Integer.valueOf(stringObjectMap.get("isSplit").toString()));
                }
                if (stringObjectMap != null && stringObjectMap.get("isOrderSplit") != null && !StringUtils.isBlank(
                    stringObjectMap.get("isOrderSplit").toString())) {
                    fileManageUpdateRequest.setIsOrderSplit(
                        Integer.valueOf(stringObjectMap.get("isOrderSplit").toString()));
                }
            }
            fileManageUpdateRequest.setIsDeleted(fileManageResult.getIsDeleted());
            fileManageUpdateRequest.setUploadTime(fileManageResult.getUploadTime());
            fileManageUpdateRequests.add(fileManageUpdateRequest);
        });
        scriptManageDeployUpdateRequest.setFileManageUpdateRequests(fileManageUpdateRequests);
        return scriptManageDeployUpdateRequest;
    }

    @Override
    public List<SupportJmeterPluginNameResponse> getSupportJmeterPluginNameList(
        SupportJmeterPluginNameRequest nameRequest) {
        List<SupportJmeterPluginNameResponse> nameResponseList = Lists.newArrayList();
        String refType = nameRequest.getRelatedType();
        String refValue = nameRequest.getRelatedId();
        //获取所有业务活动
        List<BusinessLinkResult> businessActivityList = getBusinessActivity(refType, refValue);
        if (CollectionUtils.isEmpty(businessActivityList)) {
            log.error("未查询到业务活动信息:[refType:{},refValue:{}]", refType, refValue);
            return nameResponseList;
        }
        //如果选择的是虚拟业务活动
        List<String> typeList;
        BusinessLinkResult linkResult = businessActivityList.get(0);
        Integer linkResultType = linkResult.getType();
        if (ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE.equals(refType) && linkResultType != null && linkResultType
            .equals(BusinessTypeEnum.VIRTUAL_BUSINESS.getType())) {
            typeList = Lists.newArrayList(linkResult.getServerMiddlewareType());
            if (CollectionUtils.isEmpty(typeList)) {
                log.error("虚拟业务活动，未查询到类型信息:[businessActivityList:{}]", JSON.toJSONString(businessActivityList));
                return nameResponseList;
            }
        } else {
            //获取所有系统流程id
            List<Long> businessLinkIdList = businessActivityList.stream().map(BusinessLinkResult::getLinkId).collect(
                Collectors.toList());
            List<BusinessLinkResult> businessLinkResultList = businessLinkManageDAO.getListByIds(businessLinkIdList);
            List<Long> techLinkIdList = businessLinkResultList.stream()
                .map(BusinessLinkResult::getRelatedTechLink)
                .map(Long::parseLong)
                .collect(Collectors.toList());
            //获取所有系统流程入口类型
            LinkManageQueryParam queryParam = new LinkManageQueryParam();
            queryParam.setLinkIdList(techLinkIdList);
            List<LinkManageResult> linkManageResultList = linkManageDAO.selectList(queryParam);
            if (CollectionUtils.isEmpty(linkManageResultList)) {
                log.error("未查询到服务信息:[techLinkIdList:{}]", JSON.toJSONString(techLinkIdList));
                return nameResponseList;
            }
            typeList = linkManageResultList.stream()
                .map(LinkManageResult::getFeatures)
                .map(features -> JSON.parseObject(features, Map.class))
                .map(featuresObj -> featuresObj.get(FeaturesConstants.SERVER_MIDDLEWARE_TYPE_KEY))
                .map(String::valueOf)
                .distinct()
                .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(typeList)) {
                log.error("未查询到类型信息:[techLinkIdList:{}]", JSON.toJSONString(techLinkIdList));
                return nameResponseList;
            }
        }
        EnginePluginFetchWrapperReq fetchWrapperReq = new EnginePluginFetchWrapperReq();
        fetchWrapperReq.setPluginTypes(typeList);
        ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> responseResult
            = sceneManageApi.listEnginePlugins(fetchWrapperReq);
        Map<String, List<EnginePluginSimpleInfoResp>> dataMap = responseResult.getData();
        if (Objects.isNull(responseResult) || dataMap.isEmpty()) {
            log.error("未查询到插件信息:[typeList:{}]", JSON.toJSONString(typeList));
            return nameResponseList;
        }
        nameResponseList = typeList.stream().map(type -> {
            SupportJmeterPluginNameResponse nameResponse = new SupportJmeterPluginNameResponse();
            nameResponse.setType(type);
            type = type.toLowerCase();
            if (dataMap.containsKey(type)) {
                List<EnginePluginSimpleInfoResp> pluginSimpleInfoResps = dataMap.get(type);
                if (CollectionUtils.isNotEmpty(pluginSimpleInfoResps)) {
                    List<SinglePluginRenderResponse> singlePluginRenderResponseList;
                    singlePluginRenderResponseList = pluginSimpleInfoResps.stream().map(enginePluginSimpleInfoResp -> {
                        SinglePluginRenderResponse renderResponse = new SinglePluginRenderResponse();
                        renderResponse.setLabel(enginePluginSimpleInfoResp.getPluginName());
                        renderResponse.setValue(enginePluginSimpleInfoResp.getPluginId());
                        return renderResponse;
                    }).collect(Collectors.toList());
                    nameResponse.setSinglePluginRenderResponseList(singlePluginRenderResponseList);
                }
            }
            return nameResponse;
        }).collect(Collectors.toList());
        //脚本管理-新增-关联业务  防止后端返回的singlePluginRenderResponseList为空 造成页面跳转到空白页
        nameResponseList = nameResponseList.stream().filter(
            t -> CollectionUtil.isNotEmpty(t.getSinglePluginRenderResponseList())).collect(Collectors.toList());
        return nameResponseList;
    }

    @Override
    public SupportJmeterPluginVersionResponse getSupportJmeterPluginVersionList(
        SupportJmeterPluginVersionRequest versionRequest) {
        Long pluginId = versionRequest.getPluginId();
        EnginePluginDetailsWrapperReq wrapperReq = new EnginePluginDetailsWrapperReq();
        wrapperReq.setPluginId(pluginId);
        ResponseResult<EnginePluginDetailResp> responseResult = sceneManageApi.getEnginePluginDetails(wrapperReq);
        EnginePluginDetailResp detailResp = responseResult.getData();
        if (!Objects.isNull(detailResp)) {
            SupportJmeterPluginVersionResponse versionResponse = new SupportJmeterPluginVersionResponse();
            versionResponse.setVersionList(detailResp.getSupportedVersions());
            return versionResponse;
        }
        return null;
    }

    private List<FileManageCreateParam> getFileManageCreateParamsByUpdateReq(
        List<FileManageUpdateRequest> fileManageUpdateRequests, String targetScriptPath) {
        return fileManageUpdateRequests.stream().map(fileManageUpdateRequest -> {
            FileManageCreateParam fileManageCreateParam = new FileManageCreateParam();
            fileManageCreateParam.setFileName(fileManageUpdateRequest.getFileName());
            fileManageCreateParam.setFileSize(fileManageUpdateRequest.getFileSize());
            fileManageCreateParam.setFileType(fileManageUpdateRequest.getFileType());
            Map<String, Object> fileExtend = new HashMap<>();
            fileExtend.put("dataCount", fileManageUpdateRequest.getDataCount());
            fileExtend.put("isSplit", fileManageUpdateRequest.getIsSplit());
            fileExtend.put("isOrderSplit", fileManageUpdateRequest.getIsOrderSplit());
            fileExtend.put("isBigFile", fileManageUpdateRequest.getIsBigFile());
            fileManageCreateParam.setFileExtend(JsonHelper.bean2Json(fileExtend));
            fileManageCreateParam.setUploadPath(targetScriptPath + fileManageUpdateRequest.getFileName());
            fileManageCreateParam.setUploadTime(fileManageUpdateRequest.getUploadTime());
            return fileManageCreateParam;
        }).collect(Collectors.toList());

    }

    private List<FileManageCreateParam> getFileManageCreateParams(
        List<FileManageCreateRequest> fileManageCreateRequests, String targetScriptPath) {
        return fileManageCreateRequests.stream().map(fileManageCreateRequest -> {
            FileManageCreateParam fileManageCreateParam = new FileManageCreateParam();
            fileManageCreateParam.setFileName(fileManageCreateRequest.getFileName());
            fileManageCreateParam.setFileSize(fileManageCreateRequest.getFileSize());
            fileManageCreateParam.setFileType(fileManageCreateRequest.getFileType());
            Map<String, Object> fileExtend = new HashMap<>();
            fileExtend.put("dataCount", fileManageCreateRequest.getDataCount());
            fileExtend.put("isSplit", fileManageCreateRequest.getIsSplit());
            fileExtend.put("isOrderSplit", fileManageCreateRequest.getIsOrderSplit());
            fileManageCreateParam.setFileExtend(JsonHelper.bean2Json(fileExtend));
            fileManageCreateParam.setUploadPath(targetScriptPath + fileManageCreateRequest.getFileName());
            fileManageCreateParam.setUploadTime(fileManageCreateRequest.getUploadTime());
            return fileManageCreateParam;
        }).collect(Collectors.toList());

    }

    @Override
    public PagingList<ScriptManageDeployResponse> pageQueryScriptManage(
        ScriptManageDeployPageQueryRequest scriptManageDeployPageQueryRequest) {
        ScriptManageDeployPageQueryParam scriptManageDeployPageQueryParam = new ScriptManageDeployPageQueryParam();
        if (scriptManageDeployPageQueryRequest != null) {
            BeanUtils.copyProperties(scriptManageDeployPageQueryRequest, scriptManageDeployPageQueryParam);
            if (!StringUtil.isBlank(scriptManageDeployPageQueryRequest.getBusinessActivityId())) {
                scriptManageDeployPageQueryParam.setRefType(ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE);
                scriptManageDeployPageQueryParam.setRefValue(
                    scriptManageDeployPageQueryRequest.getBusinessActivityId());
            }
            if (!StringUtil.isBlank(scriptManageDeployPageQueryRequest.getBusinessFlowId())) {
                scriptManageDeployPageQueryParam.setRefType(ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE);
                scriptManageDeployPageQueryParam.setRefValue(scriptManageDeployPageQueryRequest.getBusinessFlowId());
            }
            if (!StringUtil.isBlank(scriptManageDeployPageQueryRequest.getBusinessActivityId()) && !StringUtil
                .isBlank(scriptManageDeployPageQueryRequest.getBusinessFlowId())) {
                return PagingList.empty();
            }
            if (!CollectionUtils.isEmpty(scriptManageDeployPageQueryRequest.getTagIds())) {
                List<ScriptTagRefResult> scriptTagRefResults = scriptTagRefDAO.selectScriptTagRefByTagIds(
                    scriptManageDeployPageQueryRequest.getTagIds());
                if (CollectionUtils.isEmpty(scriptTagRefResults)) {
                    //标签中没有查到关联数据，直接返回空
                    return PagingList.empty();
                }
                Map<Long, List<ScriptTagRefResult>> scriptTagRefMap = scriptTagRefResults.stream().collect(
                    Collectors.groupingBy(ScriptTagRefResult::getScriptId));
                List<Long> scriptIds = new ArrayList<>();
                for (Map.Entry<Long, List<ScriptTagRefResult>> entry : scriptTagRefMap.entrySet()) {
                    if (entry.getValue().size() == scriptManageDeployPageQueryRequest.getTagIds().size()) {
                        scriptIds.add(entry.getKey());
                    }
                }
                if (CollectionUtils.isEmpty(scriptIds)) {
                    return PagingList.empty();
                }
                scriptManageDeployPageQueryParam.setScriptIds(scriptIds);
            }

            if (!Objects.isNull(scriptManageDeployPageQueryRequest.getScriptId())) {
                List<Long> scriptDeployIds = new ArrayList<>();
                scriptDeployIds.add(scriptManageDeployPageQueryRequest.getScriptId());
                scriptManageDeployPageQueryParam.setScriptDeployIds(scriptDeployIds);
            }
        }
        scriptManageDeployPageQueryParam.setCurrent(scriptManageDeployPageQueryRequest.getCurrent());
        scriptManageDeployPageQueryParam.setPageSize(scriptManageDeployPageQueryRequest.getPageSize());
        scriptManageDeployPageQueryParam.setScriptType(0);
        List<Long> userIdList = WebPluginUtils.getQueryAllowUserIdList();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            scriptManageDeployPageQueryParam.setUserIdList(userIdList);
        }
        PagingList<ScriptManageDeployResult> scriptManageDeployResults = scriptManageDAO
            .pageQueryRecentScriptManageDeploy(
                scriptManageDeployPageQueryParam);
        if (CollectionUtils.isEmpty(scriptManageDeployResults.getList())) {
            return PagingList.of(Lists.newArrayList(), scriptManageDeployResults.getTotal());
        }
        //用户ids
        List<Long> userIds = scriptManageDeployResults.getList().stream()
            .map(ScriptManageDeployResult::getUserId).filter(Objects::nonNull)
            .collect(Collectors.toList());
        //用户信息Map key:userId  value:user对象
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        List<Long> allowUpdateUserIdList = WebPluginUtils.getUpdateAllowUserIdList();
        List<Long> allowDeleteUserIdList = WebPluginUtils.getDeleteAllowUserIdList();
        List<Long> allowDownloadUserIdList = WebPluginUtils.getDownloadAllowUserIdList();
        List<ScriptManageDeployResponse> scriptManageDeployResponses = scriptManageDeployResults.getList().stream().map(
            scriptManageDeployResult -> {
                ScriptManageDeployResponse scriptManageDeployResponse = new ScriptManageDeployResponse();
                BeanUtils.copyProperties(scriptManageDeployResult, scriptManageDeployResponse);

                if (CollectionUtils.isNotEmpty(allowUpdateUserIdList)) {
                    scriptManageDeployResponse.setCanEdit(
                        allowUpdateUserIdList.contains(scriptManageDeployResult.getUserId()));
                }
                if (CollectionUtils.isNotEmpty(allowDeleteUserIdList)) {
                    scriptManageDeployResponse.setCanRemove(
                        allowDeleteUserIdList.contains(scriptManageDeployResult.getUserId()));
                }
                if (CollectionUtils.isNotEmpty(allowDownloadUserIdList)) {
                    scriptManageDeployResponse.setCanDownload(
                        allowDownloadUserIdList.contains(scriptManageDeployResult.getUserId()));
                }
                //负责人id
                scriptManageDeployResponse.setUserId(scriptManageDeployResult.getUserId());
                //负责人名称
                String userName = Optional.ofNullable(userMap.get(scriptManageDeployResult.getUserId()))
                    .map(UserExt::getName).orElse("");
                scriptManageDeployResponse.setUserName(userName);
                return scriptManageDeployResponse;
            }).collect(Collectors.toList());
        setFileList(scriptManageDeployResponses);
        setTagList(scriptManageDeployResponses);
        setRefName(scriptManageDeployResponses, scriptManageDeployResults);

        // canDebug 赋值
        this.setupCanDebug(scriptManageDeployResponses);

        return PagingList.of(scriptManageDeployResponses, scriptManageDeployResults.getTotal());
    }

    /**
     * 脚本发布列表赋值 canDebug 字段
     *
     * @param scriptDeployList 脚本发布列表
     */
    private void setupCanDebug(List<ScriptManageDeployResponse> scriptDeployList) {
        // 收集脚本发布ids
        if (CollectionUtils.isEmpty(scriptDeployList)) {
            return;
        }

        List<Long> scriptDeployIds = scriptDeployList.stream()
            .map(ScriptManageDeployResponse::getId).collect(Collectors.toList());
        if (scriptDeployIds.isEmpty()) {
            return;
        }

        // 查询出脚本发布对应的是否可调试
        List<ScriptDeployFinishDebugVO> scriptDeployFinishDebugResultList =
            scriptDebugDAO.listScriptDeployFinishDebugResult(scriptDeployIds);
        if (scriptDeployFinishDebugResultList.isEmpty()) {
            return;
        }

        Map<Long, ScriptDeployFinishDebugVO> scriptDeployIdAboutResult = scriptDeployFinishDebugResultList.stream()
            .collect(Collectors.toMap(ScriptDeployFinishDebugVO::getScriptDeployId, Function.identity()));
        // 匹配是否可以
        for (ScriptManageDeployResponse scriptDeploy : scriptDeployList) {
            ScriptDeployFinishDebugVO result = scriptDeployIdAboutResult.get(scriptDeploy.getId());
            if (result != null) {
                scriptDeploy.setCanDebug(result.isFinished());
            }
        }
    }

    private void setTagList(List<ScriptManageDeployResponse> scriptManageDeployResponses) {
        if (scriptManageDeployResponses == null || CollectionUtils.isEmpty(scriptManageDeployResponses)) {
            return;
        }
        List<Long> scriptIds = scriptManageDeployResponses.stream().map(ScriptManageDeployResponse::getScriptId)
            .collect(Collectors.toList());
        List<ScriptTagRefResult> scriptTagRefResults = scriptTagRefDAO.selectScriptTagRefByScriptIds(scriptIds);
        if (CollectionUtils.isEmpty(scriptTagRefResults)) {
            return;
        }
        List<Long> tagIds = scriptTagRefResults.stream().map(ScriptTagRefResult::getTagId).collect(Collectors.toList());
        List<TagManageResult> tagManageResults = tagManageDAO.selectScriptTagsByIds(tagIds);
        if (CollectionUtils.isEmpty(tagManageResults)) {
            return;
        }
        Map<Long, List<ScriptTagRefResult>> scriptTagRefMap = scriptTagRefResults.stream().collect(
            Collectors.groupingBy(ScriptTagRefResult::getScriptId));
        List<TagManageResponse> tagManageResponses = tagManageResults.stream().map(tagManageResult -> {
            TagManageResponse tagManageResponse = new TagManageResponse();
            tagManageResponse.setId(tagManageResult.getId());
            tagManageResponse.setTagName(tagManageResult.getTagName());
            return tagManageResponse;
        }).collect(Collectors.toList());
        Map<Long, TagManageResponse> tagManageResponseMap = tagManageResponses.stream().collect(
            Collectors.toMap(TagManageResponse::getId, a -> a, (k1, k2) -> k1));
        scriptManageDeployResponses.forEach(scriptManageDeployResponse -> {
            List<ScriptTagRefResult> scriptTagRefList = scriptTagRefMap.get(scriptManageDeployResponse.getScriptId());
            if (CollectionUtils.isEmpty(scriptTagRefList)) {
                return;
            }
            List<TagManageResponse> resultTagManageResponses = scriptTagRefList.stream().map(
                scriptTagRefResult -> tagManageResponseMap.get(scriptTagRefResult.getTagId())).collect(
                Collectors.toList());
            if (CollectionUtils.isEmpty(resultTagManageResponses)) {
                return;
            }
            scriptManageDeployResponse.setTagManageResponses(resultTagManageResponses);
        });
    }

    private void setFileList(List<ScriptManageDeployResponse> scriptManageDeployResponses) {
        if (scriptManageDeployResponses == null || CollectionUtils.isEmpty(scriptManageDeployResponses)) {
            return;
        }
        List<Long> scriptDeployIds = scriptManageDeployResponses.stream().map(ScriptManageDeployResponse::getId)
            .collect(Collectors.toList());
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployIds(
            scriptDeployIds);
        if (CollectionUtils.isEmpty(scriptFileRefResults)) {
            return;
        }
        List<Long> fileIdList = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(
            Collectors.toList());
        List<FileManageResponse> fileManageResponses = getFileManageResponseByFileIds(fileIdList);
        if (CollectionUtils.isEmpty(fileManageResponses)) {
            return;
        }
        Map<Long, List<ScriptFileRefResult>> scriptFileRefMap = scriptFileRefResults.stream().collect(
            Collectors.groupingBy(ScriptFileRefResult::getScriptDeployId));
        Map<Long, FileManageResponse> fileManageResultMap = fileManageResponses.stream().collect(
            Collectors.toMap(FileManageResponse::getId, a -> a, (k1, k2) -> k1));
        scriptManageDeployResponses.forEach(scriptManageDeployResponse -> {
            List<ScriptFileRefResult> scriptFileRefList = scriptFileRefMap.get(scriptManageDeployResponse.getId());
            if (CollectionUtils.isEmpty(scriptFileRefList)) {
                return;
            }
            List<FileManageResponse> resultFileManageResponses = scriptFileRefList.stream().map(
                scriptFileRefResult -> fileManageResultMap.get(scriptFileRefResult.getFileId())).collect(
                Collectors.toList());
            if (CollectionUtils.isEmpty(resultFileManageResponses)) {
                return;
            }
            scriptManageDeployResponse.setFileManageResponseList(resultFileManageResponses);
        });
    }

    @Override
    public void deleteScriptManage(Long scriptDeployId) {
        ScriptManageDeployResult scriptManageDeployResult = scriptManageDAO.selectScriptManageDeployById(
            scriptDeployId);
        if (scriptManageDeployResult == null) {
            return;
        }
        ResponseResult<List<SceneManageListResp>> sceneManageList = sceneManageApi.getSceneManageList(new ContextExt());
        if (sceneManageList == null || !sceneManageList.getSuccess()) {
            String errorMessage = "";
            // TODO: 解除阻塞,需要正确实现.
            //ErrorInfo error = sceneManageList.getError();
            //if (error != null) {
            //    errorMessage = error.getMsg();
            //}

            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR,
                "查询场景列表失败！" + errorMessage);
        }

        List<ScriptManageDeployResult> existScriptManageDeployResults = scriptManageDAO
            .selectScriptManageDeployByScriptId(scriptManageDeployResult.getScriptId());
        Map<Long, ScriptManageDeployResult> existScriptManageDeployResultMap = existScriptManageDeployResults.stream()
            .collect(Collectors.toMap(ScriptManageDeployResult::getId, o -> o, (k1, k2) -> k1));
        StringBuilder sb = new StringBuilder();
        StringBuilder debuggerStr = new StringBuilder();
        if (CollectionUtils.isNotEmpty(sceneManageList.getData())) {
            for (SceneManageListResp sceneManageListResp : sceneManageList.getData()) {
                if (!StringUtil.isBlank(sceneManageListResp.getFeatures())) {
                    Map<String, Object> featuresMap = JsonHelper.json2Map(sceneManageListResp.getFeatures(),
                        String.class, Object.class);
                    if (featuresMap.get(SceneManageConstant.FEATURES_SCRIPT_ID) != null) {
                        if (existScriptManageDeployResultMap.get(
                            Long.valueOf(featuresMap.get(SceneManageConstant.FEATURES_SCRIPT_ID).toString())) != null) {
                            // added by junshao 调试场景不参与判断
                            if (sceneManageListResp.getSceneName().contains("SCENE_MANAGER_TRY_RUN") ||
                                sceneManageListResp.getSceneName().contains("SCENE_MANAGER_FLOW_DEBUG")) {
                                debuggerStr.append(sceneManageListResp.getId()).append(",");
                            } else {
                                sb.append(sceneManageListResp.getSceneName()).append("、");
                            }
                        }
                    }
                }
            }
        }
        if (!StringUtil.isBlank(sb.toString())) {
            sb.deleteCharAt(sb.lastIndexOf("、"));
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR,
                "该脚本被以下场景引用，请取消引用后再删除:" + sb);
        }
        // added by junshao 有调试场景则调用cloud接口删除
        if (!StringUtil.isBlank(debuggerStr.toString())) {
            debuggerStr.deleteCharAt(debuggerStr.lastIndexOf(","));
            Arrays.asList(debuggerStr.toString().split(",")).forEach(a -> {
                SceneManageDeleteReq sceneManageDeleteReq = new SceneManageDeleteReq();
                sceneManageDeleteReq.setId(Long.valueOf(a));
                sceneManageApi.deleteScene(sceneManageDeleteReq);
            });
        }
        scriptManageDAO.deleteScriptManageAndDeploy(scriptManageDeployResult.getScriptId());
        List<Long> existScriptManageDeployIds = existScriptManageDeployResults.stream().map(
            ScriptManageDeployResult::getId).collect(Collectors.toList());
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployIds(
            existScriptManageDeployIds);
        if (CollectionUtils.isNotEmpty(scriptFileRefResults)) {
            List<Long> scriptFileRefIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getId).collect(
                Collectors.toList());
            scriptFileRefDAO.deleteByIds(scriptFileRefIds);
            List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(
                Collectors.toList());
            List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(fileIds);
            List<String> filePaths = fileManageResults.stream().map(FileManageResult::getUploadPath).collect(
                Collectors.toList());

            FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
            fileDeleteParamReq.setPaths(filePaths);
            fileApi.deleteFile(fileDeleteParamReq);

            fileManageDAO.deleteByIds(fileIds);
        }
        scriptTagRefDAO.deleteByScriptId(scriptManageDeployResult.getScriptId());

    }

    @Override
    public void createScriptTagRef(ScriptTagCreateRefRequest scriptTagCreateRefRequest) {
        if (scriptTagCreateRefRequest == null || scriptTagCreateRefRequest.getScriptDeployId() == null) {
            return;
        }
        if (scriptTagCreateRefRequest.getTagNames().size() > 10) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "每个脚本关联标签数不能超过10");
        }
        List<String> collect = scriptTagCreateRefRequest.getTagNames().stream().filter(o -> o.length() > 10).collect(
            Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "存在脚本名称长度超过10");
        }
        ScriptManageDeployResult scriptManageDeployResult = scriptManageDAO.selectScriptManageDeployById(
            scriptTagCreateRefRequest.getScriptDeployId());
        if (scriptManageDeployResult != null) {
            scriptTagRefDAO.deleteByScriptId(scriptManageDeployResult.getScriptId());
            if (CollectionUtils.isNotEmpty(scriptTagCreateRefRequest.getTagNames())) {
                List<TagManageParam> tagManageParams = scriptTagCreateRefRequest.getTagNames().stream().distinct().map(
                    tagName -> {
                        TagManageParam tagManageParam = new TagManageParam();
                        tagManageParam.setTagName(tagName);
                        //默认为可用状态
                        tagManageParam.setTagStatus(0);
                        //默认为脚本类型
                        tagManageParam.setTagType(0);
                        return tagManageParam;
                    }).collect(Collectors.toList());
                List<Long> tagIds = tagManageDAO.addScriptTags(tagManageParams, 0);
                scriptTagRefDAO.addScriptTagRef(tagIds, scriptManageDeployResult.getScriptId());
            }
        }

    }

    @Override
    public List<TagManageResponse> queryScriptTagList() {
        List<TagManageResult> tagManageResults = tagManageDAO.selectAllScript();
        if (CollectionUtils.isNotEmpty(tagManageResults)) {
            return tagManageResults.stream().map(tagManageResult -> {
                TagManageResponse tagManageResponse = new TagManageResponse();
                tagManageResponse.setId(tagManageResult.getId());
                tagManageResponse.setTagName(tagManageResult.getTagName());
                return tagManageResponse;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public ScriptManageDeployDetailResponse getScriptManageDeployDetail(Long scriptDeployId) {
        ScriptManageDeployDetailResponse result = new ScriptManageDeployDetailResponse();
        ScriptManageDeployResult scriptManageDeployResult = scriptManageDAO.selectScriptManageDeployById(
            scriptDeployId);
        if (scriptManageDeployResult == null) {
            return null;
        }
        BeanUtils.copyProperties(scriptManageDeployResult, result);
        setRefName(result, scriptManageDeployResult);

        // 相关文件赋值
        this.setFileList(result);

        // 插件配置列表
        result.setPluginConfigDetailResponseList(
            ScriptManageUtil.listPluginConfigs(scriptManageDeployResult.getFeature()));
        return result;
    }

    /**
     * 批量设置关联名称
     */
    private void setRefName(List<ScriptManageDeployResponse> scriptManageDeployResponses,
        PagingList<ScriptManageDeployResult> scriptManageDeployResults) {
        if (scriptManageDeployResults == null || CollectionUtils.isEmpty(scriptManageDeployResults.getList())
            || CollectionUtils.isEmpty(scriptManageDeployResponses)) {
            return;
        }
        Map<String, List<ScriptManageDeployResult>> refTypeMap = scriptManageDeployResults.getList()
            .stream()
            .filter(item -> item.getRefType() != null)
            .collect(
                Collectors.groupingBy(ScriptManageDeployResult::getRefType));
        if (refTypeMap == null || refTypeMap.size() <= 0) {
            return;
        }
        Map<Long, ScriptManageDeployResult> longScriptManageDeployResultMap = scriptManageDeployResults.getList()
            .stream().collect(Collectors.toMap(ScriptManageDeployResult::getId, a -> a, (k1, k2) -> k1));
        Map<Long, Scene> businessFlowMap = new HashMap<>();
        Map<Long, BusinessLinkManageTable> businessActivityMap = new HashMap<>();
        for (Map.Entry<String, List<ScriptManageDeployResult>> entry : refTypeMap.entrySet()) {
            //关联了业务流程
            if (ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE.equals(entry.getKey())) {
                List<Long> businessFlowIds = entry.getValue().stream().map(
                    scriptManageDeployResult -> Long.parseLong(scriptManageDeployResult.getRefValue())).collect(
                    Collectors.toList());
                List<Scene> scenes = tSceneMapper.selectBusinessFlowNameByIds(businessFlowIds);
                if (CollectionUtils.isNotEmpty(scenes)) {
                    businessFlowMap = scenes.stream().collect(Collectors.toMap(Scene::getId, a -> a, (k1, k2) -> k1));
                }
            }
            //关联了业务活动
            if (ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE.equals(entry.getKey())) {
                List<Long> businessActivityIds = entry.getValue().stream().map(
                    scriptManageDeployResult -> Long.parseLong(scriptManageDeployResult.getRefValue())).collect(
                    Collectors.toList());
                List<BusinessLinkManageTable> businessLinkManageTables = tBusinessLinkManageTableMapper
                    .selectBussinessLinkByIdList(businessActivityIds);
                if (CollectionUtils.isNotEmpty(businessLinkManageTables)) {
                    businessActivityMap = businessLinkManageTables.stream().collect(
                        Collectors.toMap(BusinessLinkManageTable::getLinkId, a -> a, (k1, k2) -> k1));
                }
            }
        }
        Map<Long, Scene> finalBusinessFlowMap = businessFlowMap;
        Map<Long, BusinessLinkManageTable> finalBusinessActivityMap = businessActivityMap;
        scriptManageDeployResponses.forEach(scriptManageDeployResponse -> {
            ScriptManageDeployResult scriptManageDeployResult = longScriptManageDeployResultMap.get(
                scriptManageDeployResponse.getId());
            if (ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE.equals(scriptManageDeployResponse.getRefType())) {
                Scene scene = finalBusinessFlowMap.get(Long.parseLong(scriptManageDeployResult.getRefValue()));
                if (scene != null) {
                    scriptManageDeployResponse.setRefName(scene.getSceneName());
                }
            }
            if (ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE.equals(scriptManageDeployResponse.getRefType())) {
                BusinessLinkManageTable businessLinkManageTable = finalBusinessActivityMap.get(
                    Long.parseLong(scriptManageDeployResult.getRefValue()));
                if (businessLinkManageTable != null) {
                    scriptManageDeployResponse.setRefName(businessLinkManageTable.getLinkName());
                }

            }
        });
    }

    @Override
    public void setFileList(ScriptManageDeployDetailResponse result) {
        if (result == null || result.getId() == null) {
            return;
        }
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployId(result.getId());
        if (CollectionUtils.isEmpty(scriptFileRefResults)) {
            log.info("不存在关联的文件id");
            return;
        }
        List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(
            Collectors.toList());
        List<FileManageResponse> totalFileList = getFileManageResponseByFileIds(fileIds);
        if (CollectionUtils.isNotEmpty(totalFileList)) {
            List<FileManageResponse> fileManageResponseList = totalFileList.stream().filter(
                f -> f.getFileType().equals(FileTypeEnum.SCRIPT.getCode()) || f.getFileType()
                    .equals(FileTypeEnum.DATA.getCode())).collect(
                Collectors.toList());
            List<FileManageResponse> attachmentManageResponseList = totalFileList.stream().filter(
                f -> f.getFileType().equals(FileTypeEnum.ATTACHMENT.getCode())).collect(
                Collectors.toList());
            result.setFileManageResponseList(fileManageResponseList);
            result.setAttachmentManageResponseList(attachmentManageResponseList);
            if (CollectionUtils.isNotEmpty(fileManageResponseList)) {
                List<FileManageResponse> list = fileManageResponseList.stream().filter(
                    resp -> null != resp.getIsBigFile() && resp.getIsBigFile() == 1).collect(Collectors.toList());
                result.setHasBigFile(CollectionUtils.isNotEmpty(list) ? 1 : 0);
            }
        }
    }

    private List<FileManageResponse> getFileManageResponseByFileIds(List<Long> fileIds) {
        List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(fileIds);
        List<FileManageResponse> fileManageResponses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fileManageResults)) {
            for (FileManageResult fileManageResult : fileManageResults) {
                FileManageResponse fileManageResponse = new FileManageResponse();
                BeanUtils.copyProperties(fileManageResult, fileManageResponse);
                if (StringUtils.isNotEmpty(fileManageResult.getFileExtend())) {
                    Map<String, Object> stringObjectMap = JsonHelper.json2Map(fileManageResult.getFileExtend(),
                        String.class, Object.class);
                    if (stringObjectMap != null && stringObjectMap.get("dataCount") != null && !StringUtil.isBlank(
                        stringObjectMap.get("dataCount").toString())) {
                        fileManageResponse.setDataCount(Long.valueOf(stringObjectMap.get("dataCount").toString()));
                    }
                    if (stringObjectMap != null && stringObjectMap.get("isSplit") != null && !StringUtil.isBlank(
                        stringObjectMap.get("isSplit").toString())) {
                        fileManageResponse.setIsSplit(Integer.valueOf(stringObjectMap.get("isSplit").toString()));
                    }
                    if (stringObjectMap != null && stringObjectMap.get("isOrderSplit") != null && !StringUtil.isBlank(
                        stringObjectMap.get("isOrderSplit").toString())) {
                        fileManageResponse.setIsOrderSplit(
                            Integer.valueOf(stringObjectMap.get("isOrderSplit").toString()));
                    }
                    fileManageResponse.setIsBigFile(0);
                    if (stringObjectMap != null && stringObjectMap.get("isBigFile") != null && !StringUtil.isBlank(
                        stringObjectMap.get("isBigFile").toString())) {
                        fileManageResponse.setIsBigFile(Integer.valueOf(stringObjectMap.get("isBigFile").toString()));
                    }
                }
                String uploadUrl = fileManageResult.getUploadPath();
                fileManageResponse.setUploadPath(uploadUrl);
                fileManageResponses.add(fileManageResponse);
            }
            return fileManageResponses;
        }
        return null;
    }

    /**
     * 设置关联的业务活动或业务流程
     */
    private void setRefName(ScriptManageDeployDetailResponse result,
        ScriptManageDeployResult scriptManageDeployResult) {
        if (StringUtils.isEmpty(scriptManageDeployResult.getRefValue())) {
            return;
        }
        //关联了系统流程
        if (ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE.equals(scriptManageDeployResult.getRefType())) {
            long businessId = Long.parseLong(scriptManageDeployResult.getRefValue());
            List<Scene> scenes = tSceneMapper.selectBusinessFlowNameByIds(Collections.singletonList(businessId));
            if (CollectionUtils.isNotEmpty(scenes)) {
                result.setRefName(scenes.get(0).getSceneName());
            }
        }
        //关联了业务活动
        if (ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE.equals(scriptManageDeployResult.getRefType())) {
            long businessId = Long.parseLong(scriptManageDeployResult.getRefValue());
            List<BusinessLinkManageTable> businessLinkManageTables = tBusinessLinkManageTableMapper
                .selectBussinessLinkByIdList(Collections.singletonList(businessId));
            if (CollectionUtils.isNotEmpty(businessLinkManageTables)) {
                result.setRefName(businessLinkManageTables.get(0).getLinkName());
            }
        }
    }

    /**
     * 创建脚本参数校验
     */
    private void checkCreateScriptManageParam(ScriptManageDeployCreateRequest scriptManageDeployCreateRequest) {
        if (scriptManageDeployCreateRequest == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "入参为空！");
        }
        if (StringUtil.isBlank(scriptManageDeployCreateRequest.getName())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本名称为空！");
        }
        if (StringUtil.isBlank(scriptManageDeployCreateRequest.getRefType())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本关联类型为空！");
        }
        if (StringUtil.isBlank(scriptManageDeployCreateRequest.getRefValue())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本关联值为空！");
        }
        if (scriptManageDeployCreateRequest.getType() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "脚本类型为空！");
        }
        if (CollectionUtils.isEmpty(scriptManageDeployCreateRequest.getFileManageCreateRequests())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "文件列表为空！");
        }
        boolean existJmx = false;
        for (FileManageCreateRequest fileManageCreateRequest : scriptManageDeployCreateRequest
            .getFileManageCreateRequests()) {
            if (StringUtil.isBlank(fileManageCreateRequest.getFileName())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "文件列表中存在文件名为空！");
            }
            if (fileManageCreateRequest.getFileName().length() > 64) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "文件列表中存在文件名长度大于64！");
            }
            if (fileManageCreateRequest.getFileType() == null) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR,
                    "文件列表中存在文件类型为空！fileName=" + fileManageCreateRequest.getFileName());
            }
            if (fileManageCreateRequest.getFileName().contains(" ")) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "文件名包含空格！");
            }
            if (fileManageCreateRequest.getFileType() == 0 && fileManageCreateRequest.getIsDeleted() == 0) {
                existJmx = true;
            }
            fileManageCreateRequest.setFileName(FileUtil.replaceFileName(fileManageCreateRequest.getFileName()));
        }
        if (!existJmx) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "文件列表中不存在脚本文件！");
        }
    }

    /**
     * 更新入参判断
     *
     * @param scriptManageDeployUpdateRequest 更新入参
     */
    private void checkUpdateScriptManageParam(ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest) {
        ScriptManageExceptionUtil.isUpdateValidError(scriptManageDeployUpdateRequest == null, "入参为空!");
        ScriptManageExceptionUtil.isUpdateValidError(scriptManageDeployUpdateRequest.getId() == null, "脚本id为空!");
        ScriptManageExceptionUtil.isUpdateValidError(StringUtil.isBlank(scriptManageDeployUpdateRequest.getName()),
            "脚本名称为空!");
        ScriptManageExceptionUtil.isUpdateValidError(StringUtil.isBlank(scriptManageDeployUpdateRequest.getRefType()),
            "脚本关联类型为空!");
        ScriptManageExceptionUtil.isUpdateValidError(StringUtil.isBlank(scriptManageDeployUpdateRequest.getRefValue()),
            "脚本关联值为空!");
        ScriptManageExceptionUtil.isUpdateValidError(scriptManageDeployUpdateRequest.getType() == null, "脚本类型为空!");
        ScriptManageExceptionUtil.isUpdateValidError(
            CollectionUtils.isEmpty(scriptManageDeployUpdateRequest.getFileManageUpdateRequests()), "文件列表为空!");

        boolean existJmx = false;
        for (FileManageUpdateRequest fileManageUpdateRequest : scriptManageDeployUpdateRequest
            .getFileManageUpdateRequests()) {

            ScriptManageExceptionUtil.isUpdateValidError(StringUtil.isBlank(fileManageUpdateRequest.getFileName()),
                "文件列表中存在文件名为空!");
            ScriptManageExceptionUtil.isUpdateValidError(fileManageUpdateRequest.getFileName().length() > 64,
                "文件列表中存在文件名长度大于64!");
            ScriptManageExceptionUtil.isUpdateValidError(fileManageUpdateRequest.getFileName().contains(" "),
                "文件名包含空格!");
            ScriptManageExceptionUtil.isUpdateValidError(fileManageUpdateRequest.getFileType() == null,
                "文件列表中存在文件类型为空！fileName=" + fileManageUpdateRequest.getFileName());

            if (fileManageUpdateRequest.getFileType() == 0 && fileManageUpdateRequest.getIsDeleted() == 0) {
                existJmx = true;
            }
            fileManageUpdateRequest.setFileName(FileUtil.replaceFileName(fileManageUpdateRequest.getFileName()));
        }

        ScriptManageExceptionUtil.isUpdateValidError(!existJmx, "文件列表中不存在脚本文件!");
    }

    /**
     * 脚本路径前缀 目录 + 脚本id + 版本
     *
     * @param scriptManageDeployResult 脚本实例
     * @return 路径前缀
     */
    private String getTargetScriptPath(ScriptManageDeployResult scriptManageDeployResult) {
        return String.format("%s/%s/%s/",
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_SCRIPT_PATH)+WebPluginUtils.traceTenantCode()+Separator.Separator1.getValue()+WebPluginUtils.traceEnvCode(),
            scriptManageDeployResult.getScriptId(), scriptManageDeployResult.getScriptVersion());
    }

}
