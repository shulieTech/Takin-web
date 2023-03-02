package io.shulie.takin.cloud.biz.service.script.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.script.ScriptFileApi;
import io.shulie.takin.adapter.api.model.request.pressure.ScriptAnnounceRequest;
import io.shulie.takin.adapter.api.model.request.pressure.ScriptAnnounceRequest.FileItem;
import io.shulie.takin.adapter.api.model.request.script.ScriptVerifyRequest;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.service.script.ScriptAnalyzeService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.script.util.SaxUtil;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityExt;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityExt.FileVerifyItem;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityRespExt;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScriptAnalyzeServiceImpl implements ScriptAnalyzeService {

    @Resource
    private ScriptFileApi scriptFileApi;

    @Resource
    private AppConfig appConfig;

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public ScriptVerityRespExt verityScript(ScriptVerityExt scriptVerityExt) {
        ScriptVerityRespExt scriptVerityRespExt = new ScriptVerityRespExt();
        List<String> errorMsgList = new ArrayList<>();
        scriptVerityRespExt.setErrorMsg(errorMsgList);

        if (CollectionUtils.isEmpty(scriptVerityExt.getRequest())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_VERITY_ERROR, "脚本校验业务活动不能为空");
        }
        // 增加新版压测校验
        if (scriptVerityExt.isUseNewVerify()) {
            try {
                ScriptAnnounceRequest request = toAnnounceRequest(scriptVerityExt);
                scriptFileApi.fileAnnounce(request);
                cacheInfo(scriptVerityExt, request);
            } catch (Exception e) {
                log.error("文件下载异常", e);
                errorMsgList.add(e.getMessage());
            }
        }
        {return scriptVerityRespExt;}
    }

    @Override
    public void updateScriptContent(String uploadPath) {
        SaxUtil.updateJmx(uploadPath);
    }

    @Override
    public ScriptParseExt parseScriptFile(String uploadPath) {
        return SaxUtil.parseJmx(uploadPath);
    }

    @Override
    public List<ScriptNode> buildNodeTree(String scriptFile) {
        return JmxUtil.buildNodeTree(scriptFile);
    }

    private ScriptAnnounceRequest toAnnounceRequest(ScriptVerityExt scriptVerityExt) {
        ScriptAnnounceRequest request = new ScriptAnnounceRequest();
        request.setCallbackUrl(appConfig.getCallbackUrl());
        request.setWatchmanIdList(scriptVerityExt.getWatchmanIdList()); // 压力机集群
        convertAndAdd(scriptVerityExt.getScriptPaths(), request);
        convertAndAdd(scriptVerityExt.getCsvPaths(), request);
        convertAndAdd(scriptVerityExt.getAttachments(), request);
        convertAndAdd(scriptVerityExt.getPluginPaths(), request, true);
        return request;
    }

    private void convertAndAdd(List<FileVerifyItem> scriptPaths, ScriptAnnounceRequest request) {
        convertAndAdd(scriptPaths, request, false);
    }

    private void convertAndAdd(List<FileVerifyItem> scriptPaths, ScriptAnnounceRequest request, boolean filterInner) {
        if (!CollectionUtils.isEmpty(scriptPaths)) {
            List<FileItem> fileList = request.getFileList();
            scriptPaths.stream()
                .filter(item -> !filterInner || !item.isInner())
                .map(item -> {
                    Map<String, String> param = new HashMap<>(4);
                    param.put("bigFile", String.valueOf(item.isBigFile()));
                    param.put("filePath", item.getFullPath());
                    return FileItem.builder()
                        .path(item.getPath()).sign(item.getMd5())
                        .downloadUrl(appConfig.getEngineFileDownloadUrl(param))
                        .build();
                }).forEach(fileList::add);
        }
    }

    private ScriptVerifyRequest toVerifyRequest(ScriptVerityExt scriptVerityExt, ScriptAnnounceRequest announceRequest) {
        List<String> watchmanIdList = scriptVerityExt.getWatchmanIdList();
        ScriptVerifyRequest request = new ScriptVerifyRequest();
        request.setAttach(announceRequest.getAttach());
        request.setCallbackUrl(appConfig.getCallbackUrl());
        request.setWatchmanId(watchmanIdList.get(0));
        request.setWatchmanIdList(watchmanIdList); // 压力机集群
        request.setScriptPath(scriptVerityExt.getScriptPaths().get(0).getPath());
        request.setDataFilePath(mergePaths(scriptVerityExt.getCsvPaths()));
        request.setAttachmentsPath(mergePaths(scriptVerityExt.getAttachments()));
        request.setPluginPath(mergePaths(scriptVerityExt.getPluginPaths()));
        return request;
    }

    private List<String> mergePaths(List<FileVerifyItem> verifyItems) {
        return verifyItems.stream().map(FileVerifyItem::getPath).collect(Collectors.toList());
    }

    private void cacheInfo(ScriptVerityExt scriptVerityExt, ScriptAnnounceRequest request) {
        String attach = request.getAttach();
        PressureStartCache.setFileAttachId(attach);
        // 缓存校验请求
        ScriptVerifyRequest verifyRequest = toVerifyRequest(scriptVerityExt, request);
        String scriptVerifyKey = PressureStartCache.getScriptVerifyKey(attach);
        redisClientUtil.set(scriptVerifyKey, verifyRequest, TimeUnit.DAYS.toSeconds(1L));

        // 缓存使用文件，回调使用
        String downloadFilesKey = PressureStartCache.getScriptDownloadFilesKey(attach);
        redisClientUtil.addSetValue(downloadFilesKey, allFiles(scriptVerityExt).toArray(new String[0]));
        redisClientUtil.expire(downloadFilesKey, 1, TimeUnit.DAYS);

        // 文件相关信息缓存
        String pressureFileKey = PressureStartCache.getPressureFileKey(attach);
        redisClientUtil.hmset(pressureFileKey, PressureStartCache.FILE_DOWNLOAD_STATUS, CheckStatus.PENDING.ordinal());
        redisClientUtil.expire(pressureFileKey, 1, TimeUnit.DAYS);

        // 第一次发压时，存在文件下发路径与启动路径不一致的情况，特殊处理
        if (!scriptVerityExt.isFromScene()) {
            String mappingKey = PressureStartCache.getScriptMappingKey(attach);
            Map<String, Object> fileMapping = new HashMap<>(8);
            fileMapping.put(String.valueOf(FileTypeEnum.SCRIPT.getCode()), Collections.singletonList(verifyRequest.getScriptPath()));
            fileMapping.put(String.valueOf(FileTypeEnum.DATA.getCode()), verifyRequest.getDataFilePath());
            fileMapping.put(String.valueOf(FileTypeEnum.ATTACHMENT.getCode()), verifyRequest.getAttachmentsPath());
            redisClientUtil.hmset(mappingKey, fileMapping);
            redisClientUtil.expire(mappingKey, 2, TimeUnit.MINUTES);
        }
    }

    // 操作的文件集合
    private Set<String> allFiles(ScriptVerityExt verityExt) {
        return Stream.of(verityExt.getScriptPaths(), verityExt.getCsvPaths(),
                verityExt.getAttachments(), verityExt.getPluginPaths()).filter(CollectionUtils::isNotEmpty)
            .flatMap(Collection::stream).filter(item -> !item.isInner())
            .map(FileVerifyItem::getPath).collect(Collectors.toSet());
    }
}
