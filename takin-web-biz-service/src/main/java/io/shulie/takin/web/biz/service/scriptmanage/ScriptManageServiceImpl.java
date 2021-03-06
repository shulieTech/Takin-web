package io.shulie.takin.web.biz.service.scriptmanage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import com.pamirs.takin.entity.dao.linkmanage.TSceneMapper;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessActiveIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import com.pamirs.takin.entity.domain.entity.linkmanage.BusinessLinkManageTable;
import com.pamirs.takin.entity.domain.entity.linkmanage.Scene;
import io.shulie.amdb.common.enums.RpcType;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.common.UploadFileDTO;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileContentParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileCopyParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileCreateByStringParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileDeleteParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileZipParamReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginDetailResp;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.PathFormatForTest;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.linux.LinuxHelper;
import io.shulie.takin.utils.security.MD5Utils;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.cache.agentimpl.FileManageSignCache;
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
import io.shulie.takin.web.biz.service.linkmanage.LinkManageService;
import io.shulie.takin.web.biz.utils.business.script.ScriptManageUtil;
import io.shulie.takin.web.biz.utils.exception.ScriptManageExceptionUtil;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.FeaturesConstants;
import io.shulie.takin.web.common.constant.FileManageConstant;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.constant.ScriptManageConstant;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptMVersionEnum;
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
import io.shulie.takin.web.data.mapper.mysql.FileManageMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
     * ??????????????? cloud ?????????
     * ?????? takin cloud ??????
     */
    @Value("${file.upload.url:}")
    private String fileUploadUrl;
    @Value("${script.check:true}")
    private Boolean scriptCheck;

    @Resource
    private DiffFileApi fileApi;
    @Resource
    private ScriptManageDAO scriptManageDAO;
    @Resource
    private ScriptFileRefDAO scriptFileRefDAO;
    @Resource
    private FileManageDAO fileManageDAO;
    @Resource
    private FileManageMapper fileManageMapper;
    @Resource
    private ScriptTagRefDAO scriptTagRefDAO;
    @Resource
    private TSceneMapper tSceneMapper;
    @Resource
    private LinkManageService linkManageService;
    @Resource
    private SceneManageApi sceneManageApi;
    @Resource
    private BusinessLinkManageDAO businessLinkManageDAO;
    @Resource
    private LinkManageDAO linkManageDAO;
    @Resource
    private ScriptDebugDAO scriptDebugDAO;
    @Resource
    private ScriptManageDeployDAO scriptManageDeployDAO;
    @Resource
    private SceneLinkRelateDAO sceneLinkRelateDAO;

    @Autowired
    RedisTemplate redisTemplate;

    //TODO ?????????????????????mapper??????dao?????????mapper?????????????????????T?????? ???T??????????????????
    @Resource
    private TagManageDAO tagManageDAO;
    @Resource
    private TBusinessLinkManageTableMapper tBusinessLinkManageTableMapper;

    @PostConstruct
    public void init() {
        fileUploadUrl = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_URL);
    }

    @Override
    public String getZipFileUrl(Long scriptDeployId) {
        ScriptManageDeployResult scriptManageDeployResult = scriptManageDAO.selectScriptManageDeployById(
            scriptDeployId);
        if (scriptManageDeployResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "??????????????????");
        }
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployId(scriptDeployId);
        if (CollectionUtils.isEmpty(scriptFileRefResults)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "????????????????????????");
        }
        List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(
            Collectors.toList());
        List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(fileIds);
        //??????????????????
        List<String> uploadPaths = fileManageResults.stream()
            .filter(t -> StringUtil.isNotBlank(t.getFileExtend()))
            .filter(t -> {
                JSONObject jsonObject = JsonUtil.json2Bean(t.getFileExtend(), JSONObject.class);
                if (jsonObject != null) {
                    Integer bigFile = jsonObject.getInteger("isBigFile");
                    return bigFile == null || !bigFile.equals(1);
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
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_THIRD_PARTY_ERROR, "takin-cloud???????????????????????????");
        }
        String url = null;
        try {
            url = fileUploadUrl + FileManageConstant.CLOUD_FILE_DOWN_LOAD_API + targetScriptPath + URLEncoder.encode(
                fileName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fileDir = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_USER_DATA_DIR);
        String[] cmdArray = {"curl", "-o", fileDir + "/" + fileName, "--create-dirs", "-OL", url};
        LinuxHelper.execCurl(cmdArray);
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
                "?????????????????????????????????ID=" + scriptManageResults.get(0).getId());
        }
        uploadCreateScriptFile(scriptManageDeployCreateRequest.getFileManageCreateRequests());
        List<FileManageCreateRequest> scriptFile = scriptManageDeployCreateRequest.getFileManageCreateRequests()
            .stream().filter(o -> o.getFileType() == 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(scriptFile) || scriptFile.size() != 1) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "????????????????????????");
        }
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH);
        ScriptCheckDTO scriptCheckDTO = checkAndUpdateScript(scriptManageDeployCreateRequest.getRefType(),
            scriptManageDeployCreateRequest.getRefValue(), scriptManageDeployCreateRequest.getMVersion(),
            tmpFilePath + "/" + scriptFile.get(0).getUploadId() + "/" + scriptFile.get(0).getFileName());
        if (scriptCheckDTO != null && !StringUtil.isBlank(scriptCheckDTO.getErrmsg())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, scriptCheckDTO.getErrmsg());
        }
        if (scriptCheckDTO != null && scriptCheckDTO.getIsHttp() != null && !scriptCheckDTO.getIsHttp()) {
            if (CollectionUtils.isEmpty(scriptManageDeployCreateRequest.getPluginConfigCreateRequests())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "????????????http?????????????????????????????????????????????????????????");
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
        ScriptManageDeployResult scriptManageDeployResult;
        try {
            scriptManageDeployResult = scriptManageDAO.createScriptManageDeploy(
                scriptManageDeployCreateParam);
            String targetScriptPath = getTargetScriptPath(scriptManageDeployResult);
            List<String> tmpFilePaths = scriptManageDeployCreateRequest.getFileManageCreateRequests().stream().map(
                o -> (tmpFilePath + "/" + o.getUploadId() + "/" + o.getFileName())
                        .replaceAll("//","/")
                        .replaceAll("\\/\\/","/")).collect(Collectors.toList());
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
            log.error("??????????????????????????????", e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_ADD_ERROR, "??????????????????????????????????????????takin-web?????????");
        }

        return scriptManageDeployResult.getId();
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void uploadCreateScriptFile(List<FileManageCreateRequest> fileManageCreateRequests) {
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH);
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
     * ???????????????????????????, ??????
     *
     * @param fileManageCreateRequests ??????????????????
     */
    private void uploadUpdateScriptFile(List<FileManageUpdateRequest> fileManageCreateRequests) {
        if (CollectionUtils.isEmpty(fileManageCreateRequests)) {
            return;
        }
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH);
        // ????????????????????????, ??????
        for (FileManageUpdateRequest fileManageUpdateRequest : fileManageCreateRequests) {
            if (StringUtil.isNotBlank(fileManageUpdateRequest.getScriptContent())) {
                // uploadId ????????????, ??????
                if (StringUtil.isNotBlank(fileManageUpdateRequest.getUploadId())) {
                    String tempFile = tmpFilePath + "/" + fileManageUpdateRequest.getUploadId() + "/"
                        + fileManageUpdateRequest.getFileName();

                    FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
                    fileDeleteParamReq.setPaths(Collections.singletonList(tempFile));
                    fileApi.deleteFile(fileDeleteParamReq);
                }

                // ??????
                UUID uuid = UUID.randomUUID();
                fileManageUpdateRequest.setUploadId(uuid.toString());
                String tempFile = tmpFilePath + "/" + fileManageUpdateRequest.getUploadId() + "/"
                    + fileManageUpdateRequest.getFileName();

                if (fileManageUpdateRequest.getId() == null) {
                    log.warn("????????????????????????{}???", fileManageUpdateRequest.getDownloadUrl());
                    FileCopyParamReq paramReq = new FileCopyParamReq();
                    paramReq.setTargetPath(tmpFilePath + "/" + fileManageUpdateRequest.getUploadId() + "/");
                    paramReq.setSourcePaths(Lists.newArrayList(fileManageUpdateRequest.getDownloadUrl()));
                    fileApi.copyFile(paramReq);
                } else {
                    FileCreateByStringParamReq fileCreateByStringParamReq = new FileCreateByStringParamReq();
                    fileCreateByStringParamReq.setFileContent(fileManageUpdateRequest.getScriptContent());
                    fileCreateByStringParamReq.setFilePath(tempFile);
                    String fileMd5 = fileApi.createFileByPathAndString(fileCreateByStringParamReq);
                    // ???????????????MD5???
                    if (fileManageUpdateRequest.getId() != null && !StrUtil.isBlank(fileMd5)) {
                        fileManageMapper.updateById(new FileManageEntity() {{
                            setId(fileManageUpdateRequest.getId());
                            setMd5(fileMd5);
                        }});
                    }
                }

            }
        }
    }

    @Override
    public ScriptCheckDTO checkAndUpdateScript(String refType, String refValue, Integer mVersion, String scriptFileUploadPath) {
        ScriptCheckDTO dto = new ScriptCheckDTO();
        if (scriptCheck == null || !scriptCheck || ScriptMVersionEnum.isM_1(mVersion)) {
            return dto;
        }
        if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_SCRIPT_CHECK)) {
            return dto;
        }

        ScriptCheckAndUpdateReq scriptCheckAndUpdateReq = new ScriptCheckAndUpdateReq();
        List<String> requestUrls = new ArrayList<>();
        scriptCheckAndUpdateReq.setRequest(requestUrls);
        scriptCheckAndUpdateReq.setUploadPath(scriptFileUploadPath);
        scriptCheckAndUpdateReq.setAbsolutePath(true);
        scriptCheckAndUpdateReq.setUpdate(true);
        if (mVersion != null) {scriptCheckAndUpdateReq.setVersion(mVersion);}
        List<BusinessLinkResult> businessActivityList = getBusinessActivity(refType, refValue);
        if (CollectionUtils.isEmpty(businessActivityList)) {
            dto.setErrmsg("?????????????????????????????????");
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
            log.error("cloud??????????????????????????????{}", scriptFileUploadPath);
            dto.setErrmsg("cloud??????????????????????????????" + scriptFileUploadPath);
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
                //return tBusinessLinkManageTableMapper.selectBusinessesLinkByIdList(businessActivityIds);
                return businessLinkManageDAO.getListByIds(businessActivityIds);
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Long updateScriptManage(ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest) {
        // ????????????
        this.checkUpdateScriptManageParam(scriptManageDeployUpdateRequest);
        // ??????????????????
        scriptManageDeployUpdateRequest.setName(scriptManageDeployUpdateRequest.getName().trim());

        // ????????????
        List<FileManageUpdateRequest> scriptFile =
            scriptManageDeployUpdateRequest.getFileManageUpdateRequests().stream()
                .filter(o -> o.getIsDeleted() == 0 && FileTypeEnum.SCRIPT.getCode().equals(o.getFileType()))
                .collect(Collectors.toList());
        // ??????
        ScriptManageExceptionUtil.isUpdateValidError(CollectionUtils.isEmpty(scriptFile)
            || scriptFile.size() != 1, "?????????????????????!");

        //??????????????????????????????????????????????????????????????????,???????????????????????????????????????????????? eg: ??????-> ??????->??????->??????
        if (StringUtil.isNotBlank(scriptFile.get(0).getScriptContent())) {
            List<FileManageUpdateRequest> originRequests =
                    scriptManageDeployUpdateRequest.getFileManageUpdateRequests();
            if (originRequests.size() > 1) {
                List<FileManageUpdateRequest> oldList =
                        scriptManageDeployUpdateRequest.getFileManageUpdateRequests().stream()
                                .filter(o -> o.getIsDeleted() == 1 && FileTypeEnum.SCRIPT.getCode().equals(o.getFileType()))
                                .collect(Collectors.toList());
                if (oldList.size() != 0) {
                    //?????????????????????????????????
                    if (Objects.isNull(scriptFile.get(0).getId())) {
                        scriptFile.get(0).setId(oldList.get(0).getId());
                    }
                }
            }
        }

        // ???????????????????????????
        this.uploadUpdateScriptFile(scriptFile);

        // ??????
        ScriptManageExceptionUtil.isUpdateValidError(CollectionUtils.isEmpty(scriptFile) || scriptFile.size() != 1,
            "?????????????????????!");

        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH);
        // ???????????? url
        String scriptFileUrl;
        if (scriptFile.get(0).getId() == null) {
            scriptFileUrl = tmpFilePath + "/" + scriptFile.get(0).getUploadId() + "/" + scriptFile.get(0).getFileName();
        } else {
            List<FileManageResult> fileManageResults = fileManageDAO.selectFileManageByIds(
                Collections.singletonList(scriptFile.get(0).getId()));
            if (CollectionUtils.isEmpty(fileManageResults)) {
                log.error("??????????????????????????????fileId:{}", scriptFile.get(0).getId());
                throw ScriptManageExceptionUtil.getUpdateValidError("?????????????????????!");
            }
            scriptFileUrl = fileManageResults.get(0).getUploadPath();
        }

        // cloud ??????, ??????, ????????????
        ScriptCheckDTO scriptCheckDTO = this.checkAndUpdateScript(scriptManageDeployUpdateRequest.getRefType(),
            scriptManageDeployUpdateRequest.getRefValue(), scriptManageDeployUpdateRequest.getMVersion(), scriptFileUrl);

        // ??????????????????
        if (scriptCheckDTO != null && !StringUtil.isBlank(scriptCheckDTO.getErrmsg())) {
            throw ScriptManageExceptionUtil.getUpdateValidError(scriptCheckDTO.getErrmsg());
        }

        if (scriptCheckDTO != null && scriptCheckDTO.getIsHttp() != null && !scriptCheckDTO.getIsHttp()) {
            ScriptManageExceptionUtil.isCreateValidError(
                CollectionUtils.isEmpty(scriptManageDeployUpdateRequest.getPluginList()),
                "????????????http????????????????????????????????????!");
        }

        // ????????????, ????????????????????????
        ScriptManageDeployResult scriptManageDeployResult = this.updateScriptAndCreateScriptDeployAndGet(
            scriptManageDeployUpdateRequest);
        String targetScriptPath = this.getTargetScriptPath(scriptManageDeployUpdateRequest, scriptManageDeployResult);

        // ??????, ????????????, ??????, ??????????????????
        List<FileManageUpdateRequest> addFileManageUpdateRequests = scriptManageDeployUpdateRequest
            .getFileManageUpdateRequests().stream()
            .filter(o -> o.getIsDeleted() == 0).collect(Collectors.toList());

        // ????????????????????????
        Integer ifCoverBigFile = scriptManageDeployUpdateRequest.getIfCoverBigFile();
        if (ifCoverBigFile != null && ifCoverBigFile.equals(1)) {
            addFileManageUpdateRequests = addFileManageUpdateRequests.stream().filter(
                t -> t.getIsBigFile() == null || !t.getIsBigFile().equals(1)).collect(Collectors.toList());
        }

        // ??????????????????????????????
        List<FileManageCreateParam> fileManageCreateParams = getFileManageCreateParamsByUpdateReq(
            addFileManageUpdateRequests, targetScriptPath);

        // ??????????????????, ????????????ids
        List<Long> fileIds = fileManageDAO.createFileManageList(fileManageCreateParams);

        // ?????? cloud ???, ?????????id
        // ????????????id
        Long oldScriptDeployId = scriptManageDeployUpdateRequest.getId();
        Long newScriptDeployId = scriptManageDeployResult.getId();
        // ??????: ???????????????, ??????, ??????????????????????????????
        this.updateCloudFileByScriptId(oldScriptDeployId, newScriptDeployId, scriptManageDeployResult.getType(),
            fileManageCreateParams);

        // ??????ids???????????????id?????????
        scriptFileRefDAO.createScriptFileRefs(fileIds, newScriptDeployId);

        return newScriptDeployId;
    }

    /**
     * ?????? scriptId ?????????????????????????????????????????????
     *
     * @param oldScriptDeployId ??????????????????id
     * @param newScriptDeployId ??????????????????id
     * @param scriptType        ????????????
     * @param files             ????????????
     */
    private void updateCloudFileByScriptId(Long oldScriptDeployId, Long newScriptDeployId,
        Integer scriptType, List<FileManageCreateParam> files) {
        if (oldScriptDeployId == null || CollectionUtils.isEmpty(files)) {
            return;
        }

        // ????????????
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
                //uploadFileDTO.setIsOrderSplit(fileExtendVO.getIsOrderSplit());
            }
            uploadFileDTO.setUploadedData(0L);
            uploadFileDTO.setUploadTime(DateUtil.format(file.getUploadTime(), AppConstants.DATE_FORMAT_STRING));
            return uploadFileDTO;
        }).collect(Collectors.toList());
        request.setUploadFiles(uploadFiles);

        log.info("????????????????????????????????????????????????");
        // cloud ??????
        ResponseResult<Object> response = sceneManageApi.updateSceneFileByScriptId(request);
        if (!response.getSuccess()) {
            log.error("???????????? --> ????????? cloud ??????, ????????????????????????, ????????????: {}", JSONUtil.toJsonStr(response));
            throw ScriptManageExceptionUtil.getUpdateValidError(
                String.format("????????? cloud ??????????????????, ????????????: %s", response.getError().getSolution()));
        }
    }

    /**
     * ????????????????????????
     *
     * @param scriptManageDeployUpdateRequest ????????????????????????
     * @param scriptManageDeployResult        ????????????
     * @return ??????????????????
     */
    private String getTargetScriptPath(ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest,
        ScriptManageDeployResult scriptManageDeployResult) {
        List<String> sourcePaths = new ArrayList<>();
        // ?????????????????? ?????? + ??????id + ??????
        String targetScriptPath = this.getTargetScriptPath(scriptManageDeployResult);
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH);
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
                //??????????????????????????????
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

        //???????????? ??????????????????????????? ?????????????????????????????????????????????????????????
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
     * ????????????, ????????????????????????, ????????????????????????
     *
     * @param scriptManageDeployUpdateRequest ??????, ??????, ????????????
     * @return ????????????????????????
     */
    private ScriptManageDeployResult updateScriptAndCreateScriptDeployAndGet(
        ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest) {
        // ????????????id
        Long scriptDeployId = scriptManageDeployUpdateRequest.getId();
        // ??????????????????id??????????????????
        ScriptManageDeployResult oldScriptManageDeployResult = scriptManageDAO.selectScriptManageDeployById(
            scriptDeployId);
        ScriptManageExceptionUtil.isUpdateValidError(oldScriptManageDeployResult == null, "??????????????????????????????!");

        // ??????id, ??????????????????id
        Long scriptId = oldScriptManageDeployResult.getScriptId();
        // ????????????
        ScriptManageResult scriptManageResult = scriptManageDAO.selectScriptManageById(scriptId);
        ScriptManageExceptionUtil.isUpdateValidError(scriptManageResult == null, "?????????????????????????????????????????????!");

        // ????????????????????????
        ScriptManageDeployCreateParam scriptManageDeployCreateParam = new ScriptManageDeployCreateParam();
        BeanUtils.copyProperties(scriptManageDeployUpdateRequest, scriptManageDeployCreateParam);
        scriptManageDeployCreateParam.setScriptId(scriptId);
        scriptManageDeployCreateParam.setStatus(ScriptManageDeployStatusEnum.NEW.getCode());

        // ?????? + 1
        // ??????: ??????????????????, ????????????????????????
        int scriptNewVersion = scriptManageResult.getScriptVersion() + 1;
        scriptManageDeployCreateParam.setScriptVersion(scriptNewVersion);

        //?????????????????????????????????????????????????????????
        scriptManageDAO.updateScriptVersion(scriptId, scriptNewVersion);

        // ????????????????????????
        Map<String, Object> features = Maps.newHashMap();
        features.put(FeaturesConstants.PLUGIN_CONFIG, scriptManageDeployUpdateRequest.getPluginList());
        scriptManageDeployCreateParam.setFeature(JSON.toJSONString(features));
        return scriptManageDAO.createScriptManageDeploy(scriptManageDeployCreateParam);
    }

    @Override
    public List<ScriptManageSceneManageResponse> getAllScenes(String businessFlowName) {
        List<ScriptManageSceneManageResponse> scriptManageSceneManageResponses = new ArrayList<>();
        try {
            List<BusinessFlowIdAndNameDto> businessFlowIdAndNameDtoList = linkManageService.businessFlowIdFuzzSearch(
                businessFlowName);
            if (CollectionUtils.isNotEmpty(businessFlowIdAndNameDtoList)) {
                scriptManageSceneManageResponses = businessFlowIdAndNameDtoList.stream().map(businessFlowIdAndNameDto -> {
                    ScriptManageSceneManageResponse scriptManageSceneManageResponse
                        = new ScriptManageSceneManageResponse();
                    scriptManageSceneManageResponse.setId(businessFlowIdAndNameDto.getId());
                    scriptManageSceneManageResponse.setSceneName(businessFlowIdAndNameDto.getBusinessFlowName());
                    return scriptManageSceneManageResponse;

                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("????????????????????????");
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
                    scriptManageDeploy.getName() + " ??????" + scriptManageDeploy.getScriptVersion());
                scriptManageDeployActivityResponses.add(scriptManageDeployActivityResponse);
            });
            return scriptManageDeployActivityResponses;
        }
        return null;
    }

    @Override
    public List<ScriptManageActivityResponse> listAllActivities(String activityName) {
        // ???????????????id?????????
        // ???????????????????????????????????????, ?????????????????????????????????????????????
        List<BusinessActiveIdAndNameDto> businessActiveIdAndNameList = linkManageService.businessActiveNameFuzzSearch(
            activityName);
        if (CollectionUtils.isEmpty(businessActiveIdAndNameList)) {
            return Collections.emptyList();
        }

        // ??????
        List<ScriptManageActivityResponse> scriptManageActivityResponses = businessActiveIdAndNameList.stream()
            .map(businessActiveIdAndNameDto -> {
                ScriptManageActivityResponse scriptManageActivityResponse = new ScriptManageActivityResponse();
                scriptManageActivityResponse.setId(businessActiveIdAndNameDto.getId());
                scriptManageActivityResponse.setBusinessActiveName(businessActiveIdAndNameDto.getBusinessActiveName());
                return scriptManageActivityResponse;
            }).collect(Collectors.toList());

        // ????????????????????????id
        List<String> activityIds = scriptManageActivityResponses.stream()
            .map(ScriptManageActivityResponse::getId)
            .collect(Collectors.toList());
        if (activityIds.isEmpty()) {
            return scriptManageActivityResponses;
        }

        // ?????????????????????, ??????, ??? ???????????????
        List<Integer> scriptManageDeployStatusList = Arrays.asList(ScriptManageDeployStatusEnum.NEW.getCode(),
            ScriptManageDeployStatusEnum.PASS.getCode());

        // ????????????????????????????????????????????????
        List<ScriptManageDeployResult> scriptManageDeployResults = scriptManageDAO.selectByRefIdsAndType(
            activityIds, ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE, scriptManageDeployStatusList);
        if (CollectionUtils.isEmpty(scriptManageDeployResults)) {
            return scriptManageActivityResponses;
        }

        // ????????????, ??????????????????id, ??????
        Map<String, List<ScriptManageDeployResult>> activityScriptMap = scriptManageDeployResults.stream()
            .filter(scriptManageDeploy -> StringUtils.isNotBlank(scriptManageDeploy.getRefValue()))
            .collect(Collectors.groupingBy(ScriptManageDeployResult::getRefValue));
        // ??????????????????????????????
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
        String fileDir = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_USER_DATA_DIR);
        String[] cmdArray = {"curl", "-o", fileDir + "/" + fileName, "--create-dirs", "-OL", url};
        LinuxHelper.execCurl(cmdArray);
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
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_ROLLBACK_VALID_ERROR, "???????????????????????????????????????");
        }
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployId(
            scriptManageDeployResult.getId());
        if (CollectionUtils.isEmpty(scriptFileRefResults)) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_ROLLBACK_VALID_ERROR, "????????????????????????????????????");
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
            throw new IllegalArgumentException("takinScriptId ?????????????????????");
        }
        String originalName = partRequest.getOriginalName();
        if (StrUtil.isBlank(originalName)) {
            throw new IllegalArgumentException("originalName ?????????????????????");
        }
        // ????????????id??????
        List<ScriptManageDeployResult> scriptManageDeployList = scriptManageDAO.selectScriptManageDeployByScriptId(
            takinScriptId);
        if (CollectionUtils.isEmpty(scriptManageDeployList) || Objects.isNull(scriptManageDeployList.get(0).getId())) {
            throw new IllegalArgumentException("??????takin-web???????????????????????????");
        }
        // ???????????????id
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
            // ??????????????????????????????
            fileManageResults.forEach(result -> {
                if (result.getFileName().equals(originalName)) {
                    manageResult.set(result);
                }
            });
        }

        //???????????????????????????????????????
        if (manageResult.get() != null) {
            //????????????????????????
            List<Long> ids = Collections.singletonList(manageResult.get().getId());
            fileManageDAO.deleteByIds(ids);
            //????????????????????????
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
        // ??????????????????id????????????????????????????????????id
        ScriptManageDeployResult scriptDeploy = scriptManageDeployDAO.getById(scriptDeployId);
        if (scriptDeploy == null) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "???????????????????????????!");
        }

        String refType = scriptDeploy.getRefType();
        // ????????????????????????
        // 1 1??????, ????????????????????????, 2??????, ???web???scene???, ??????
        if (ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE.equals(refType)) {
            return sceneLinkRelateDAO.listBusinessLinkIdsByBusinessFlowIds(
                Arrays.asList(Long.valueOf(scriptDeploy.getRefValue())));
        }

        return Collections.singletonList(Long.valueOf(scriptDeploy.getRefValue()));
    }

    @Override
    public String getZipFileNameByScriptDeployId(Long scriptDeployId) {
        // ????????????????????????
        ScriptDeployDetailResult scriptDeploy = scriptManageDAO.getScriptDeployByDeployId(scriptDeployId);
        Assert.notNull(scriptDeploy, "??????????????????");

        // ?????????????????????
        List<String> filePathList = scriptManageDAO.listFilePathByScriptDeployId(scriptDeployId);
        Assert.notEmpty(filePathList, "????????????????????????");
        String aFile = filePathList.get(0);
        File file = new File(aFile);
        Assert.isTrue(file.exists(), "????????????????????????");
        File parentFile = file.getParentFile();

        // ????????????????????????
        String absoluteZipName = String.format("%s%s%s.%s", parentFile.getParent(),
            File.separator, scriptDeploy.getName(), ProbeConstants.FILE_TYPE_ZIP);
        ZipUtil.zip(parentFile.getAbsolutePath(), absoluteZipName);
        return absoluteZipName;
    }

    @Override
    public List<FileManageEntity> getAllFile() {
        return fileManageDAO.getAllFile();
    }

    private List<FileManageResult> addScriptFile(WebPartRequest partRequest, Long takinScriptId) {
        //??????????????????????????????
        // ??????????????????, ????????????ids
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
        // ??????ids???????????????id?????????
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
        //        //??????????????????????????????
        //        fileExtend.put("dataCount", null);
        //???????????????
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
        //????????????????????????
        List<BusinessLinkResult> businessActivityList = getBusinessActivity(refType, refValue);
        if (CollectionUtils.isEmpty(businessActivityList)) {
            log.error("??????????????????????????????:[refType:{},refValue:{}]", refType, refValue);
            return nameResponseList;
        }
        //????????????????????????????????????
        List<String> typeList;
        BusinessLinkResult linkResult = businessActivityList.get(0);
        Integer linkResultType = linkResult.getType();
        if (ScriptManageConstant.BUSINESS_ACTIVITY_REF_TYPE.equals(refType) && linkResultType != null && linkResultType
            .equals(BusinessTypeEnum.VIRTUAL_BUSINESS.getType())) {
            typeList = Lists.newArrayList(linkResult.getServerMiddlewareType());
            if (CollectionUtils.isEmpty(typeList)) {
                log.error("?????????????????????????????????????????????:[businessActivityList:{}]", JSON.toJSONString(businessActivityList));
                return nameResponseList;
            }
        } else {
            //????????????????????????id
            List<Long> businessLinkIdList = businessActivityList.stream().map(BusinessLinkResult::getLinkId).collect(
                Collectors.toList());
            List<BusinessLinkResult> businessLinkResultList = businessLinkManageDAO.getListByIds(businessLinkIdList);
            List<Long> techLinkIdList = businessLinkResultList.stream()
                .map(BusinessLinkResult::getRelatedTechLink)
                .map(Long::parseLong)
                .collect(Collectors.toList());
            //????????????????????????????????????
            LinkManageQueryParam queryParam = new LinkManageQueryParam();
            queryParam.setLinkIdList(techLinkIdList);
            List<LinkManageResult> linkManageResultList = linkManageDAO.selectList(queryParam);
            if (CollectionUtils.isEmpty(linkManageResultList)) {
                log.error("????????????????????????:[techLinkIdList:{}]", JSON.toJSONString(techLinkIdList));
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
                log.error("????????????????????????:[techLinkIdList:{}]", JSON.toJSONString(techLinkIdList));
                return nameResponseList;
            }
        }
        EnginePluginFetchWrapperReq fetchWrapperReq = new EnginePluginFetchWrapperReq();
        fetchWrapperReq.setPluginTypes(typeList);
        ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> responseResult
            = sceneManageApi.listEnginePlugins(fetchWrapperReq);
        Map<String, List<EnginePluginSimpleInfoResp>> dataMap = responseResult.getData();
        if (Objects.isNull(responseResult) || dataMap.isEmpty()) {
            log.error("????????????????????????:[typeList:{}]", JSON.toJSONString(typeList));
            return nameResponseList;
        }
        nameResponseList = typeList.stream().map(type -> {
            SupportJmeterPluginNameResponse nameResponse = new SupportJmeterPluginNameResponse();
            nameResponse.setType(type);
            type = type.toLowerCase();
            if (dataMap.containsKey(type)) {
                List<EnginePluginSimpleInfoResp> pluginSimpleInfoRespList = dataMap.get(type);
                if (CollectionUtils.isNotEmpty(pluginSimpleInfoRespList)) {
                    List<SinglePluginRenderResponse> singlePluginRenderResponseList;
                    singlePluginRenderResponseList = pluginSimpleInfoRespList.stream().map(enginePluginSimpleInfoResp -> {
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
        //????????????-??????-????????????  ?????????????????????singlePluginRenderResponseList?????? ??????????????????????????????
        nameResponseList = nameResponseList.stream().filter(
            t -> CollectionUtil.isNotEmpty(t.getSinglePluginRenderResponseList())).collect(Collectors.toList());
        return nameResponseList;
    }

    /**
     * ???????????????jmeter??????????????????
     *
     * @return jmeter??????????????????
     */
    @Override
    public List<SupportJmeterPluginNameResponse> getAllJmeterPluginNameList() {
        final List<SupportJmeterPluginNameResponse> nameResponseList = Lists.newArrayList();
        EnginePluginFetchWrapperReq fetchWrapperReq = new EnginePluginFetchWrapperReq();
        fetchWrapperReq.setPluginTypes(new ArrayList<>(0));
        ResponseResult<Map<String, List<EnginePluginSimpleInfoResp>>> responseResult
            = sceneManageApi.listEnginePlugins(fetchWrapperReq);
        if (responseResult.getSuccess()) {
            responseResult.getData().forEach((t, v) -> {
                nameResponseList.add(new SupportJmeterPluginNameResponse() {{
                    setType(t);
                    setSinglePluginRenderResponseList(v.stream().map(c -> new SinglePluginRenderResponse() {{
                        setLabel(c.getPluginName());
                        setValue(c.getPluginId());
                    }}).collect(Collectors.toList()));
                }});
            });
        }
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
            String uploadPath = targetScriptPath + fileManageUpdateRequest.getFileName();
            uploadPath = PathFormatForTest.format(uploadPath);
            fileManageCreateParam.setUploadPath(uploadPath);
            fileManageCreateParam.setUploadTime(fileManageUpdateRequest.getUploadTime());
            String targetP = fileManageCreateParam.getUploadPath().replaceAll("[/]", "");
            String targetPMd5 = MD5Utils.getInstance().getMD5(targetP);
            Object bodyMd5 = redisTemplate.opsForValue().get(FileManageSignCache.CACHE_NAME+targetPMd5);
            //??????md5
            fileManageCreateParam.setMd5(bodyMd5!=null?bodyMd5.toString():"");
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
                    //???????????????????????????????????????????????????
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
        //??????ids
        List<Long> userIds = scriptManageDeployResults.getList().stream()
            .map(ScriptManageDeployResult::getUserId).filter(Objects::nonNull)
            .collect(Collectors.toList());
        //????????????Map key:userId  value:user??????
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        List<Long> allowUpdateUserIdList = WebPluginUtils.getUpdateAllowUserIdList();
        List<Long> allowDeleteUserIdList = WebPluginUtils.getDeleteAllowUserIdList();
        List<Long> allowDownloadUserIdList = WebPluginUtils.getDownloadAllowUserIdList();
        List<ScriptManageDeployResponse> scriptManageDeployResponses = scriptManageDeployResults.getList().stream()
            .map(scriptManageDeployResult -> {
                ScriptManageDeployResponse scriptManageDeployResponse = new ScriptManageDeployResponse();
                BeanUtils.copyProperties(scriptManageDeployResult, scriptManageDeployResponse);
                //?????????id
                scriptManageDeployResponse.setUserId(scriptManageDeployResult.getUserId());  //???????????????
                String userName = Optional.ofNullable(userMap.get(scriptManageDeployResult.getUserId()))
                    .map(UserExt::getName).orElse("");
                scriptManageDeployResponse.setUserName(userName);

                WebPluginUtils.fillQueryResponse(scriptManageDeployResponse);

                //m1????????????????????????????????????????????????????????????
                if (ScriptMVersionEnum.isM_1(scriptManageDeployResult.getMVersion())) {
                    scriptManageDeployResponse.setCanEdit(false);
                    scriptManageDeployResponse.setCanRemove(false);
                }
                return scriptManageDeployResponse;
            }).collect(Collectors.toList());

        setFileList(scriptManageDeployResponses);
        setTagList(scriptManageDeployResponses);
        setRefName(scriptManageDeployResponses, scriptManageDeployResults);

        // canDebug ??????
        this.setupCanDebug(scriptManageDeployResponses);
        return PagingList.of(scriptManageDeployResponses, scriptManageDeployResults.getTotal());
    }

    /**
     * ???????????????????????? canDebug ??????
     *
     * @param scriptDeployList ??????????????????
     */
    private void setupCanDebug(List<ScriptManageDeployResponse> scriptDeployList) {
        // ??????????????????ids
        if (CollectionUtils.isEmpty(scriptDeployList)) {
            return;
        }

        List<Long> scriptDeployIds = scriptDeployList.stream()
            .map(ScriptManageDeployResponse::getId).collect(Collectors.toList());
        if (scriptDeployIds.isEmpty()) {
            return;
        }

        // ?????????????????????????????????????????????
        List<ScriptDeployFinishDebugVO> scriptDeployFinishDebugResultList =
            scriptDebugDAO.listScriptDeployFinishDebugResult(scriptDeployIds);
        if (scriptDeployFinishDebugResultList.isEmpty()) {
            return;
        }

        Map<Long, ScriptDeployFinishDebugVO> scriptDeployIdAboutResult = scriptDeployFinishDebugResultList.stream()
            .collect(Collectors.toMap(ScriptDeployFinishDebugVO::getScriptDeployId, Function.identity()));
        // ??????????????????
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
            // TODO: ????????????,??????????????????.
            //ErrorInfo error = sceneManageList.getError();
            //if (error != null) {
            //    errorMessage = error.getMsg();
            //}

            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR,
                "???????????????????????????" + errorMessage);
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
                            // added by ?????? ???????????????????????????
                            if (sceneManageListResp.getSceneName().contains("SCENE_MANAGER_TRY_RUN") ||
                                sceneManageListResp.getSceneName().contains("SCENE_MANAGER_FLOW_DEBUG")) {
                                debuggerStr.append(sceneManageListResp.getId()).append(",");
                            } else {
                                sb.append(sceneManageListResp.getSceneName()).append("???");
                            }
                        }
                    }
                }
            }
        }
        if (!StringUtil.isBlank(sb.toString())) {
            sb.deleteCharAt(sb.lastIndexOf("???"));
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR,
                "????????????????????????????????????????????????????????????:" + sb);
        }
        // added by ?????? ????????????????????????cloud????????????
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
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "???????????????????????????????????????10");
        }
        List<String> collect = scriptTagCreateRefRequest.getTagNames().stream().filter(o -> o.length() > 10).collect(
            Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "??????????????????????????????10");
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
                        //?????????????????????
                        tagManageParam.setTagStatus(0);
                        //?????????????????????
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

        // ??????????????????
        this.setFileList(result);

        // ??????????????????
        result.setPluginConfigDetailResponseList(
            ScriptManageUtil.listPluginConfigs(scriptManageDeployResult.getFeature()));
        return result;
    }

    /**
     * ????????????????????????
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
            //?????????????????????
            if (ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE.equals(entry.getKey())) {
                List<Long> businessFlowIds = entry.getValue().stream().map(
                    scriptManageDeployResult -> Long.parseLong(scriptManageDeployResult.getRefValue())).collect(
                    Collectors.toList());
                List<Scene> scenes = tSceneMapper.selectBusinessFlowNameByIds(businessFlowIds);
                if (CollectionUtils.isNotEmpty(scenes)) {
                    businessFlowMap = scenes.stream().collect(Collectors.toMap(Scene::getId, a -> a, (k1, k2) -> k1));
                }
            }
            //?????????????????????
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
            log.info("????????????????????????id");
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
     * ??????????????????????????????????????????
     */
    private void setRefName(ScriptManageDeployDetailResponse result,
        ScriptManageDeployResult scriptManageDeployResult) {
        if (StringUtils.isEmpty(scriptManageDeployResult.getRefValue())) {
            return;
        }
        //?????????????????????
        if (ScriptManageConstant.BUSINESS_PROCESS_REF_TYPE.equals(scriptManageDeployResult.getRefType())) {
            long businessId = Long.parseLong(scriptManageDeployResult.getRefValue());
            List<Scene> scenes = tSceneMapper.selectBusinessFlowNameByIds(Collections.singletonList(businessId));
            if (CollectionUtils.isNotEmpty(scenes)) {
                result.setRefName(scenes.get(0).getSceneName());
            }
        }
        //?????????????????????
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
     * ????????????????????????
     */
    private void checkCreateScriptManageParam(ScriptManageDeployCreateRequest scriptManageDeployCreateRequest) {
        if (scriptManageDeployCreateRequest == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "???????????????");
        }
        if (StringUtil.isBlank(scriptManageDeployCreateRequest.getName())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "?????????????????????");
        }
        if (StringUtil.isBlank(scriptManageDeployCreateRequest.getRefType())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "???????????????????????????");
        }
        if (StringUtil.isBlank(scriptManageDeployCreateRequest.getRefValue())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "????????????????????????");
        }
        if (scriptManageDeployCreateRequest.getType() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "?????????????????????");
        }
        if (CollectionUtils.isEmpty(scriptManageDeployCreateRequest.getFileManageCreateRequests())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "?????????????????????");
        }
        if (CollectionUtils.isNotEmpty(scriptManageDeployCreateRequest.getFileManageCreateRequests())) {
            List<String> uploadFileNames = scriptManageDeployCreateRequest.getFileManageCreateRequests().stream()
                .filter(o -> o.getIsDeleted() != 1)
                .map(FileManageCreateRequest::getFileName)
                .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(uploadFileNames)) {
                Set<String> singleUploadFileNames = new HashSet<>(uploadFileNames);
                ScriptManageExceptionUtil.isUpdateValidError(singleUploadFileNames.size() != uploadFileNames.size(), "????????????????????????!");
            }
        }
        if (CollectionUtils.isNotEmpty(scriptManageDeployCreateRequest.getAttachmentManageCreateRequests())) {
            List<String> uploadAttachments = scriptManageDeployCreateRequest.getAttachmentManageCreateRequests().stream()
                .filter(o -> o.getIsDeleted() != 1)
                .map(FileManageCreateRequest::getFileName)
                .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(uploadAttachments)) {
                Set<String> singleUploadFileNames = new HashSet<>(uploadAttachments);
                ScriptManageExceptionUtil.isUpdateValidError(singleUploadFileNames.size() != uploadAttachments.size(), "???????????????????????????!");
            }
        }
        boolean existJmx = false;
        for (FileManageCreateRequest fileManageCreateRequest : scriptManageDeployCreateRequest
            .getFileManageCreateRequests()) {
            if (StringUtil.isBlank(fileManageCreateRequest.getFileName())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "???????????????????????????????????????");
            }
            if (fileManageCreateRequest.getFileName().length() > 64) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "??????????????????????????????????????????64???");
            }
            if (fileManageCreateRequest.getFileType() == null) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR,
                    "??????????????????????????????????????????fileName=" + fileManageCreateRequest.getFileName());
            }
            if (fileManageCreateRequest.getFileName().contains(" ")) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "????????????????????????");
            }
            if (fileManageCreateRequest.getFileType() == 0 && fileManageCreateRequest.getIsDeleted() == 0) {
                existJmx = true;
            }
            fileManageCreateRequest.setFileName(FileUtil.replaceFileName(fileManageCreateRequest.getFileName()));
        }
        if (!existJmx) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "???????????????????????????????????????");
        }
    }

    /**
     * ??????????????????
     *
     * @param scriptManageDeployUpdateRequest ????????????
     */
    private void checkUpdateScriptManageParam(ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest) {
        ScriptManageExceptionUtil.isUpdateValidError(scriptManageDeployUpdateRequest == null, "????????????!");
        ScriptManageExceptionUtil.isUpdateValidError(scriptManageDeployUpdateRequest.getId() == null, "??????id??????!");
        ScriptManageExceptionUtil.isUpdateValidError(StringUtil.isBlank(scriptManageDeployUpdateRequest.getName()),
            "??????????????????!");
        ScriptManageExceptionUtil.isUpdateValidError(StringUtil.isBlank(scriptManageDeployUpdateRequest.getRefType()),
            "????????????????????????!");
        ScriptManageExceptionUtil.isUpdateValidError(StringUtil.isBlank(scriptManageDeployUpdateRequest.getRefValue()),
            "?????????????????????!");
        ScriptManageExceptionUtil.isUpdateValidError(scriptManageDeployUpdateRequest.getType() == null, "??????????????????!");

        ScriptManageExceptionUtil.isUpdateValidError(
            CollectionUtils.isEmpty(scriptManageDeployUpdateRequest.getFileManageUpdateRequests()), "??????????????????!");

        if (CollectionUtils.isNotEmpty(scriptManageDeployUpdateRequest.getFileManageUpdateRequests())) {
            List<String> uploadFileNames = scriptManageDeployUpdateRequest.getFileManageUpdateRequests().stream()
                .filter(o -> o.getIsDeleted() != 1)
                .map(FileManageUpdateRequest::getFileName)
                .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(uploadFileNames)) {
                Set<String> singleUploadFileNames = new HashSet<>(uploadFileNames);
                ScriptManageExceptionUtil.isUpdateValidError(singleUploadFileNames.size() != uploadFileNames.size(), "????????????????????????!");
            }
        }
        if (CollectionUtils.isNotEmpty(scriptManageDeployUpdateRequest.getAttachmentManageUpdateRequests())) {
            List<String> uploadAttachments = scriptManageDeployUpdateRequest.getAttachmentManageUpdateRequests().stream()
                .filter(o -> o.getIsDeleted() != 1)
                .map(FileManageUpdateRequest::getFileName)
                .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(uploadAttachments)) {
                Set<String> singleUploadFileNames = new HashSet<>(uploadAttachments);
                ScriptManageExceptionUtil.isUpdateValidError(singleUploadFileNames.size() != uploadAttachments.size(), "???????????????????????????!");
            }
        }

        boolean existJmx = false;
        for (FileManageUpdateRequest fileManageUpdateRequest : scriptManageDeployUpdateRequest
            .getFileManageUpdateRequests()) {

            ScriptManageExceptionUtil.isUpdateValidError(StringUtil.isBlank(fileManageUpdateRequest.getFileName()),
                "????????????????????????????????????!");
            ScriptManageExceptionUtil.isUpdateValidError(fileManageUpdateRequest.getFileName().length() > 64,
                "??????????????????????????????????????????64!");
            ScriptManageExceptionUtil.isUpdateValidError(fileManageUpdateRequest.getFileName().contains(" "),
                "?????????????????????!");
            ScriptManageExceptionUtil.isUpdateValidError(fileManageUpdateRequest.getFileType() == null,
                "??????????????????????????????????????????fileName=" + fileManageUpdateRequest.getFileName());

            if (fileManageUpdateRequest.getFileType() == 0 && fileManageUpdateRequest.getIsDeleted() == 0) {
                existJmx = true;
            }
            fileManageUpdateRequest.setFileName(FileUtil.replaceFileName(fileManageUpdateRequest.getFileName()));
        }

        ScriptManageExceptionUtil.isUpdateValidError(!existJmx, "????????????????????????????????????!");
    }

    /**
     * ?????????????????? ?????? + ??????id + ??????
     *
     * @param scriptManageDeployResult ????????????
     * @return ????????????
     */
    private String getTargetScriptPath(ScriptManageDeployResult scriptManageDeployResult) {
        return String.format("%s/%s/%s/",
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_SCRIPT_PATH),
            scriptManageDeployResult.getScriptId(), scriptManageDeployResult.getScriptVersion());
    }

}
