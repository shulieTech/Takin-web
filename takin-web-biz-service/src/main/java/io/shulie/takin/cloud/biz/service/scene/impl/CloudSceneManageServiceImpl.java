package io.shulie.takin.cloud.biz.service.scene.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.cloud.entity.dao.report.TReportMapper;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneBusinessActivityRefMapper;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneManageMapper;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneScriptRefMapper;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneSlaRefMapper;
import com.pamirs.takin.cloud.entity.domain.entity.report.Report;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneBusinessActivityRef;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneManage;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneRef;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneSlaRef;
import com.pamirs.takin.cloud.entity.domain.vo.scenemanage.SceneManageStartRecordVO;
import io.shulie.takin.adapter.api.model.common.RuleBean;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.shulie.takin.adapter.api.model.common.UploadFileDTO;
import io.shulie.takin.adapter.api.model.request.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.cloud.biz.cloudserver.SceneManageDTOConvert;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneBusinessActivityRefInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageQueryInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneScriptRefInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.biz.notify.StartFailEventSource;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneScriptRefOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneSlaRefOutput;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.service.script.ScriptAnalyzeService;
import io.shulie.takin.cloud.biz.utils.FileTypeBusinessUtil;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryBean;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.enums.PressureModeEnum;
import io.shulie.takin.cloud.common.enums.TimeUnitEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageErrorEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.common.utils.LinuxUtil;
import io.shulie.takin.cloud.common.utils.UrlUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneManageCreateOrUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.api.AssetExtApi;
import io.shulie.takin.cloud.ext.content.asset.AssetBillExt;
import io.shulie.takin.cloud.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.response.Response;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityExt;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityRespExt;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.utils.PathFormatForTest;
import io.shulie.takin.utils.file.FileManagerHelper;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.string.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author qianshui
 * @date 2020/4/17 下午3:32
 */
@Service
@Slf4j
public class CloudSceneManageServiceImpl extends AbstractIndicators implements CloudSceneManageService {
    @Resource
    private ReportDao reportDao;
    @Resource
    private TReportMapper tReportMapper;
    @Resource
    private PluginManager pluginManager;
    @Resource
    private CloudReportService cloudReportService;
    @Resource
    private SceneManageDAO sceneManageDAO;
    @Resource
    private ScriptAnalyzeService scriptAnalyzeService;
    @Resource
    private TSceneManageMapper tSceneManageMapper;
    @Resource
    private TSceneSlaRefMapper tSceneSlaRefMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private TSceneScriptRefMapper tSceneScriptRefMapper;
    @Resource
    private TSceneBusinessActivityRefMapper tSceneBusinessActivityRefMapper;
    @Resource
    private EventCenterTemplate eventCenterTemplate;
    @Resource
    private RedisClientUtil redisClientUtil;

    @Value("${script.temp.path}")
    private String scriptTempPath;
    @Value("${script.path}")
    private String scriptPath;
    @Value("${script.pre.match:true}")
    private boolean scriptPreMatch;

    public static final String SCENE_MANAGE = "sceneManage";
    public static final String SCENE_BUSINESS_ACTIVITY = "sceneBusinessActivity";
    public static final String SCENE_SCRIPT = "sceneScript";
    public static final String SCENE_SLA = "sceneSla";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSceneManage(SceneManageWrapperInput wrapperRequest) {
        /*
         * 0、校验业务活动和脚本文件是否匹配
         *    1、是否有脚本脚本文件
         *    2、Jmeter脚本文件是否唯一
         *    3、业务活动必须存在于脚本文件
         * 1、保存基本信息+施压配置
         * 2、保存业务活动
         * 3、保存脚本
         * 4、保存SLA
         */
        boolean isScriptManage = false;
        if (wrapperRequest.getFeatures() != null) {
            JSONObject json = JSON.parseObject(wrapperRequest.getFeatures());
            isScriptManage = json.get(SceneManageConstant.FEATURES_SCRIPT_ID) != null;
        }
        Map<String, Object> maps = buildSceneManageRef(wrapperRequest);
        List<SceneBusinessActivityRef> businessActivityRefs = (List<SceneBusinessActivityRef>)maps.get(
            SCENE_BUSINESS_ACTIVITY);
        List<SceneScriptRef> sceneScriptRefList = (List<SceneScriptRef>)maps.get(SCENE_SCRIPT);
        List<SceneSlaRef> sceneSlaRefs = (List<SceneSlaRef>)maps.get(SCENE_SLA);
        if (isScriptManage) {
            matchScriptBusinessActivity(businessActivityRefs, sceneScriptRefList);
        }
        Long sceneId = saveToDatabase((SceneManageCreateOrUpdateParam)maps.get(SCENE_MANAGE),
            businessActivityRefs, sceneScriptRefList, sceneSlaRefs, isScriptManage);

        //使用了脚本，需要转移文件
        if (isScriptManage) {
            List<SceneScriptRefInput> uploadFileList = wrapperRequest.getUploadFile();
            if (CollectionUtils.isNotEmpty(uploadFileList)) {
                //数据文件、脚本文件
                List<SceneScriptRefInput> normalFileList
                    = uploadFileList.stream().filter(sceneScriptRefInput -> sceneScriptRefInput.getFileType().equals(0)
                    || sceneScriptRefInput.getFileType().equals(1)).collect(
                    Collectors.toList());
                String destPath = getDestPath(sceneId);
                for (SceneScriptRefInput file : normalFileList) {
                    String sourcePath = file.getUploadPath();
                    copyFile(sourcePath, destPath);
                }
                //附件
                List<SceneScriptRefInput> attachmentFileList
                    = uploadFileList.stream().filter(sceneScriptRefInput -> sceneScriptRefInput.getFileType().equals(2))
                    .collect(
                        Collectors.toList());
                if (CollectionUtils.isNotEmpty(attachmentFileList)) {
                    String attachmentPath = destPath + SceneManageConstant.FILE_SPLIT + "attachments";
                    for (SceneScriptRefInput file : attachmentFileList) {
                        String sourcePath = file.getUploadPath();
                        copyFile(sourcePath, attachmentPath);
                    }
                }
            }
        }

        return sceneId;
    }

    @Override
    public String getDestPath(Long sceneId) {
        return scriptPath + SceneManageConstant.FILE_SPLIT + sceneId + SceneManageConstant.FILE_SPLIT;
    }

    private void delDirFile(String dest) {
        log.info("开始删除路径[{}].", dest);
        File file = new File(dest);
        boolean deleteResult;
        // 是个文件
        if (!file.isDirectory()) {
            deleteResult = file.delete();
            log.info("[{}]删除文件结果:{}.", file.getAbsolutePath(), deleteResult);
        }
        // 是个文件夹
        else if (file.isDirectory()) {
            String[] fileList = file.list();
            if (fileList != null) {
                for (String s : fileList) {
                    File deleteFile = new File(dest + "/" + s);
                    if (!deleteFile.isDirectory()) {
                        deleteResult = deleteFile.delete();
                        log.info("[{}]删除文件夹下文件结果:{}.", deleteFile.getAbsolutePath(), deleteResult);
                    } else if (deleteFile.isDirectory()) {
                        // 递归删除
                        delDirFile(dest + "/" + s);
                    }
                }
            }
            deleteResult = file.delete();
            log.info("[{}]删除文件夹结果:{}.", file.getAbsolutePath(), deleteResult);
        }
    }

    private void copyFile(String source, String dest) {
        if (StringUtils.isBlank(dest) || StringUtils.isBlank(source)) {
            return;
        }
        source = PathFormatForTest.format(source);
        dest = PathFormatForTest.format(dest);
        File file = new File(dest.substring(0, dest.lastIndexOf("/")));
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            log.debug("io.shulie.takin.cloud.biz.service.scene.impl.SceneManageServiceImpl#copyFile:mkdirs:{}", mkdirs);
        }
        String source1 = source;
        String dest1 = dest;
        new Thread(() -> {
            try {
                FileManagerHelper.copyFiles(Collections.singletonList(source1), dest1);
            } catch (Exception e) {
                log.error("异常代码【{}】,异常内容：压测场景处理文件异常 --> 文件复制失败: {}",
                    TakinCloudExceptionEnum.SCENE_MANAGE_FILE_COPY_ERROR, e);
            }
        }).start();
    }

    /**
     * 前置匹配脚本和业务活动
     */
    private void matchScriptBusinessActivity(List<SceneBusinessActivityRef> businessActivityList,
        List<SceneScriptRef> scriptList) {
        if (scriptPreMatch && CollectionUtils.isNotEmpty(businessActivityList) && CollectionUtils.isNotEmpty(
            scriptList)) {
            List<SceneScriptRef> sceneScriptRefList = scriptList.stream().filter(
                sceneScriptRef -> sceneScriptRef.getFileType() == 0).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(sceneScriptRefList)) {
                return;
            }
            SceneScriptRef sceneScriptRef = sceneScriptRefList.get(0);
            ScriptParseExt scriptParseExt = scriptAnalyzeService.parseScriptFile(sceneScriptRef.getUploadPath());
            if (scriptParseExt == null || CollectionUtils.isEmpty(scriptParseExt.getRequestUrl())
                || CollectionUtils.isEmpty(businessActivityList)) {
                return;
            }
            businessActivityList.forEach(businessActivity -> scriptParseExt.getRequestUrl().forEach(scriptUrlExt -> {
                if (UrlUtil.checkEqual(businessActivity.getBindRef(), scriptUrlExt.getPath())) {
                    businessActivity.setBindRef(scriptUrlExt.getName());
                }
            }));
        }
    }

    private Long saveToDatabase(SceneManageCreateOrUpdateParam createParam,
        List<SceneBusinessActivityRef> businessActivityList,
        List<SceneScriptRef> scriptList, List<SceneSlaRef> slaList, boolean isScriptManage) {
        //负责人默认创建人
        Long sceneId = sceneManageDAO.insert(createParam);
        if (sceneId == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_ADD_ERROR, "create scene error");
        }
        fillSceneId(businessActivityList, sceneId);
        fillSceneId(scriptList, sceneId);
        fillSceneId(slaList, sceneId);
        if (!isScriptManage) {
            moveTempFile(scriptList, sceneId);
        }

        if (CollectionUtils.isNotEmpty(businessActivityList)) {
            tSceneBusinessActivityRefMapper.batchInsert(businessActivityList);
        }
        if (CollectionUtils.isNotEmpty(scriptList)) {
            if (isScriptManage) {
                scriptList.forEach(sceneScriptRef -> {
                    String uploadPath = sceneScriptRef.getUploadPath();
                    uploadPath = sceneId + "/" + uploadPath.substring(uploadPath.lastIndexOf("/") + 1);
                    sceneScriptRef.setUploadPath(uploadPath);
                });
            } else {
                scriptList = scriptList.stream().filter(data -> data.getUploadId() != null).collect(
                    Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(scriptList)) {
                tSceneScriptRefMapper.batchInsert(scriptList);
            }
        }
        if (CollectionUtils.isNotEmpty(slaList)) {
            tSceneSlaRefMapper.batchInsert(slaList);
        }
        return sceneId;
    }

    @Override
    public PageInfo<SceneManageListOutput> queryPageList(SceneManageQueryInput queryVO) {

        Page<SceneManageListOutput> page = PageHelper.startPage(queryVO.getPageNumber(), queryVO.getPageSize());
        SceneManageQueryBean sceneManageQueryBean = BeanUtil.copyProperties(queryVO, SceneManageQueryBean.class);
        //默认查询普通类型场景，场景类型目前不透出去
        if (sceneManageQueryBean.getType() == null) {
            sceneManageQueryBean.setType(0);
        }
        List<SceneManageEntity> queryList = sceneManageDAO.getPageList(sceneManageQueryBean);
        if (CollectionUtils.isEmpty(queryList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<SceneManageListOutput> resultList = queryList.stream().map(t -> new SceneManageListOutput() {{
            setStatus(t.getStatus());
            setFeatures(t.getFeatures());
            setId(t.getId());
            setLastPtTime(DateUtil.formatDateTime(t.getLastPtTime()));
            setSceneName(t.getSceneName());
            setEstimateFlow(null);
            setHasReport(false);
            setThreadNum(null);
            setType(t.getType());
            setEnvCode(t.getEnvCode());
            setTenantId(t.getTenantId());
            setUserId(t.getUserId());
            setUserName(null);
            setScriptAnalysisResult(t.getScriptAnalysisResult());
        }}).collect(Collectors.toList());
        Map<Long, Integer> threadNum = new HashMap<>(1);
        for (SceneManageEntity sceneManage : queryList) {
            if (sceneManage.getPtConfig() == null) {
                continue;
            }
            JSONObject object = JSON.parseObject(sceneManage.getPtConfig());
            Integer maxThreadNumber = 0;
            // 新版本
            if (StrUtil.isNotBlank(sceneManage.getScriptAnalysisResult())) {
                PtConfigExt ptConfigExt = JSON.parseObject(sceneManage.getPtConfig(), PtConfigExt.class);
                for (Entry<String, ThreadGroupConfigExt> entry : ptConfigExt.getThreadGroupConfigMap().entrySet()) {
                    ThreadGroupConfigExt v = entry.getValue();
                    if (v.getThreadNum() != null) {
                        maxThreadNumber += v.getThreadNum();
                    }
                }
            }
            // 旧版本
            if (object.containsKey("threadNum")) {
                maxThreadNumber = object.getIntValue("threadNum");
            }
            threadNum.put(sceneManage.getId(), maxThreadNumber);
        }

        List<Long> sceneIds = tReportMapper.listReportSceneIds(
                resultList.stream().map(SceneManageListOutput::getId).collect(Collectors.toList()))
            .stream().map(Report::getSceneId).distinct().collect(Collectors.toList());

        resultList.forEach(t -> {
            t.setThreadNum(threadNum.get(t.getId()));
            t.setHasReport(sceneIds.contains(t.getId()));
            t.setStatus(SceneManageStatusEnum.getAdaptStatus(t.getStatus()));
        });

        PageInfo<SceneManageListOutput> pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    private Map<String, Object> buildSceneManageRef(SceneManageWrapperInput wrapperRequest) {
        List<SceneScriptRef> scriptList = buildScriptRef(wrapperRequest.getUploadFile(),
            wrapperRequest.getScriptType());
        List<SceneBusinessActivityRef> businessActivityList = buildSceneBusinessActivityRef(
            wrapperRequest.getBusinessActivityConfig());
        SceneManageCreateOrUpdateParam createParam = buildSceneManage(wrapperRequest);
        List<SceneSlaRef> slaList = Lists.newArrayList();
        slaList.addAll(buildSceneSlaRef(wrapperRequest.getStopCondition(), SceneManageConstant.EVENT_DESTORY));
        slaList.addAll(buildSceneSlaRef(wrapperRequest.getWarningCondition(), SceneManageConstant.EVENT_WARN));
        Map<String, Object> maps = Maps.newHashMap();
        maps.put(SCENE_MANAGE, createParam);
        maps.put(SCENE_BUSINESS_ACTIVITY, businessActivityList);
        maps.put(SCENE_SCRIPT, scriptList);
        maps.put(SCENE_SLA, slaList);
        return maps;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSceneManage(SceneManageWrapperInput wrapperRequest) {
        SceneManageWrapperOutput oldScene = getSceneManage(wrapperRequest.getId(), null);
        boolean isScriptManage = false;
        if (wrapperRequest.getFeatures() != null) {
            JSONObject json = JSON.parseObject(wrapperRequest.getFeatures());
            isScriptManage = json.get(SceneManageConstant.FEATURES_SCRIPT_ID) != null;
        }
        if (oldScene == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_VERIFY_ERROR,
                "压测场景记录不存在，id=" + wrapperRequest.getId());
        }
        boolean fileNeedChange = false;
        if (isScriptManage) {
            JSONObject json = JSON.parseObject(oldScene.getFeatures());
            JSONObject newFeatures = JSON.parseObject(wrapperRequest.getFeatures());
            if (newFeatures != null && json != null && json.get(SceneManageConstant.FEATURES_SCRIPT_ID) != null
                && newFeatures.get(SceneManageConstant.FEATURES_SCRIPT_ID) != null
                && json.getLongValue(SceneManageConstant.FEATURES_SCRIPT_ID) != newFeatures.getLongValue(
                SceneManageConstant.FEATURES_SCRIPT_ID)) {
                fileNeedChange = true;
            }
        }

        Map<String, Object> maps = buildSceneManageRef(wrapperRequest);
        SceneManageCreateOrUpdateParam manageCreateOrUpdateParam = (SceneManageCreateOrUpdateParam)maps.get(
            SCENE_MANAGE);
        List<SceneBusinessActivityRef> businessActivityRefs = (List<SceneBusinessActivityRef>)maps.get(
            SCENE_BUSINESS_ACTIVITY);
        List<SceneScriptRef> sceneScriptRefList = (List<SceneScriptRef>)maps.get(SCENE_SCRIPT);
        List<SceneSlaRef> sceneSlaRefs = (List<SceneSlaRef>)maps.get(SCENE_SLA);
        if (isScriptManage) {
            matchScriptBusinessActivity(businessActivityRefs, sceneScriptRefList);
        }
        updateToDatabase(manageCreateOrUpdateParam, businessActivityRefs, sceneScriptRefList, sceneSlaRefs,
            isScriptManage);
        //删除脚本文件、从新从文件库重新copy
        Long sceneId = wrapperRequest.getId();
        if (isScriptManage && fileNeedChange && StringUtils.isNotBlank(scriptPath) && sceneId != null) {
            this.operateFileOnSystem(wrapperRequest.getUploadFile(), sceneId);
        }
    }

    private void updateToDatabase(SceneManageCreateOrUpdateParam updateParam,
        List<SceneBusinessActivityRef> businessActivityList,
        List<SceneScriptRef> scriptList, List<SceneSlaRef> slaList, boolean isScriptManage) {
        sceneManageDAO.update(updateParam);
        Long sceneId = updateParam.getId();
        fillSceneId(businessActivityList, sceneId);
        fillSceneId(scriptList, sceneId);
        fillSceneId(slaList, sceneId);
        tSceneBusinessActivityRefMapper.deleteBySceneId(sceneId);
        if (CollectionUtils.isNotEmpty(businessActivityList)) {
            tSceneBusinessActivityRefMapper.batchInsert(businessActivityList);
        }
        tSceneSlaRefMapper.deleteBySceneId(sceneId);
        if (CollectionUtils.isNotEmpty(slaList)) {
            tSceneSlaRefMapper.batchInsert(slaList);
        }
        if (isScriptManage) {
            for (SceneScriptRef ref : scriptList) {
                ref.setUploadPath(sceneId + "/" + ref.getFileName());
            }
        }
        if (isScriptManage) {
            dealScriptRefFileByScriptManage(scriptList, sceneId);
        } else {
            dealScriptRefFile(scriptList, sceneId);
        }

    }

    /**
     * 4.5.0之前，未使用脚本管理处理方式
     *
     * @param scriptList -
     * @param sceneId    -
     */
    private void dealScriptRefFile(List<SceneScriptRef> scriptList, Long sceneId) {
        if (CollectionUtils.isNotEmpty(scriptList)) {
            List<Long> ids = scriptList.stream().filter(data -> data.getId() != null && 1 == data.getIsDeleted()).map(
                SceneScriptRef::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                tSceneScriptRefMapper.deleteByIds(ids);
                //删除大文件对应的缓存
                delCache(ids);
                //删除脚本/数据文件
                List<String> files = scriptList.stream().filter(
                        data -> data.getId() != null && 1 == data.getIsDeleted()).map(SceneScriptRef::getUploadPath)
                    .collect(
                        Collectors.toList());
                deleteUploadFile(files);
            }

            scriptList = scriptList.stream().filter(data -> data.getUploadId() != null).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(scriptList)) {
                moveTempFile(scriptList, sceneId);
                tSceneScriptRefMapper.batchInsert(scriptList);
            }
        }
    }

    /**
     * 使用脚本管理处理脚本文件
     *
     * @param scriptList 脚本列表
     * @param sceneId    场景id
     */
    private void dealScriptRefFileByScriptManage(List<SceneScriptRef> scriptList, Long sceneId) {
        if (CollectionUtils.isEmpty(scriptList)) {
            return;
        }

        //删除场景脚本关联关系，重新关联
        tSceneScriptRefMapper.deleteBySceneId(sceneId);
        List<Long> ids = scriptList.stream().filter(data -> data.getId() != null && 1 == data.getIsDeleted())
            .map(SceneScriptRef::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            tSceneScriptRefMapper.deleteByIds(ids);
            //删除大文件对应的缓存
            delCache(ids);
        }
        if (CollectionUtils.isNotEmpty(scriptList)) {
            tSceneScriptRefMapper.batchInsert(scriptList);
        }
    }

    /**
     * 大文件删除对应的缓存
     */
    public void delCache(List<Long> fileIds) {
        if (fileIds != null && fileIds.size() > 0) {
            for (Long fileId : fileIds) {
                SceneScriptRef sceneScriptRef = tSceneScriptRefMapper.selectByPrimaryKey(fileId);
                try {
                    if (sceneScriptRef != null) {
                        //删除记录的总位置
                        StringBuilder bigFileStartPos = new StringBuilder();
                        bigFileStartPos.append(sceneScriptRef.getSceneId());
                        bigFileStartPos.append("-");
                        bigFileStartPos.append(sceneScriptRef.getFileName());
                        stringRedisTemplate.delete(bigFileStartPos.toString());

                        //删除总行数
                        bigFileStartPos.append("-NUM");
                        stringRedisTemplate.delete(bigFileStartPos.toString());
                    }
                } catch (Exception e) {
                    log.error("异常代码【{}】,异常内容：大文件上传异常 --> 大文件删除对应的缓存异常: {}",
                        TakinCloudExceptionEnum.BIGFILE_UPLOAD_ERROR, e);
                }
            }
        }
    }

    @Override
    public void updateSceneManageStatus(UpdateStatusBean statusVO) {
        sceneManageDAO.updateStatus(statusVO.getSceneId(), statusVO.getAfterStatus(), statusVO.getPreStatus());
    }

    @Override
    public Boolean updateSceneLifeCycle(UpdateStatusBean statusVO) {
        String checkStatus = Arrays.stream(statusVO.getCheckEnum()).map(SceneManageStatusEnum::getDesc).collect(
            Collectors.joining(","));
        String updateStatus = statusVO.getUpdateEnum().getDesc();

        SceneManageEntity sceneManageResult = sceneManageDAO.getSceneById(statusVO.getSceneId());
        if (sceneManageResult == null) {
            log.error("异常代码【{}】,异常内容：更新生命周期失败 --> 找不到对应的场景: {}",
                TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_LIFE_CYCLE_ERROR, statusVO.getSceneId());
            toFailureState(statusVO.getSceneId(), statusVO.getResultId(),
                SceneManageErrorEnum.SCENEMANAGE_UPDATE_LIFECYCLE_NOT_FIND_SCENE.getErrorMessage());
            return false;
        }

        SceneManageStatusEnum statusEnum = SceneManageStatusEnum.getSceneManageStatusEnum(
            sceneManageResult.getStatus());
        if (statusEnum == null) {
            log.error("异常代码【{}】,异常内容：更新生命周期失败 --> 未知状态: 场景id{}，状态{}",
                TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_LIFE_CYCLE_ERROR, statusVO.getSceneId(),
                sceneManageResult.getStatus());
            toFailureState(statusVO.getSceneId(), statusVO.getResultId(),
                SceneManageErrorEnum.SCENEMANAGE_UPDATE_LIFECYCLE_UNKNOWN_STATE.getErrorMessage());
            return false;
        }
        String statusMsg = statusEnum.getDesc();

        if (!Arrays.asList(statusVO.getCheckEnum()).contains(statusEnum)) {
            log.error("异常代码【{}】,异常内容：更新生命周期失败 --> check状态错误,本次压测 {}-{}-{} 状态更新失败，更新生命周期：{} -> {},check:{}",
                TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_LIFE_CYCLE_ERROR, statusVO.getSceneId(),
                statusVO.getResultId(),
                statusVO.getTenantId(), statusMsg, updateStatus, checkStatus);
            toFailureState(statusVO.getSceneId(), statusVO.getResultId(),
                SceneManageErrorEnum.SCENEMANAGE_UPDATE_LIFECYCLE_CHECK_FAILED.getErrorMessage());
            return false;
        }

        try {
            SceneManageCreateOrUpdateParam updateParam = new SceneManageCreateOrUpdateParam();
            updateParam.setLastPtTime(new Date());
            updateParam.setId(statusVO.getSceneId());
            updateParam.setUpdateTime(new Date());
            // --->update
            updateParam.setStatus(statusVO.getUpdateEnum().getValue());

            //   这个报告startTime 压测正式真实开始时间，所以后续要被更新
            //        report.setStartTime(new Date());
            sceneManageDAO.update(updateParam);
            log.info("本次压测{}-{}-{} 状态更新成功，更新生命周期：{} -> {},check:{}", statusVO.getSceneId(), statusVO.getResultId(),
                statusVO.getTenantId(), statusMsg, updateStatus, checkStatus);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：更新生命周期失败 --> 本次压测{}-{}-{} 状态更新失败，更新生命周期：{} -> {},check:{},异常信息:{}",
                TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_LIFE_CYCLE_ERROR, statusVO.getSceneId(),
                statusVO.getResultId(),
                statusVO.getTenantId(), statusMsg, updateStatus, checkStatus, e);
            toFailureState(statusVO.getSceneId(), statusVO.getResultId(), "状态更新失败" + e.getLocalizedMessage());
            return false;

        }
        return true;
    }

    /**
     * 至失败状态
     */
    private void toFailureState(Long sceneId, Long reportId, String errorMsg) {
        // 记录失败原因，成功则不记录报告中 报告直接完成
        cloudReportService.updateReportFeatures(reportId, ReportConstants.FINISH_STATUS, ReportConstants.PRESSURE_MSG,
            errorMsg);
        ReportResult recentlyReport = reportDao.getRecentlyReport(sceneId);
        if (!reportId.equals(recentlyReport.getId())) {
            log.error("更新压测生命周期，所更新的报告不是压测场景的最新报告,场景id:{},更新的报告id:{},当前最新的报告id:{}",
                sceneId, reportId, recentlyReport.getId());
            return;
        }
        // 状态 更新 失败状态
        SceneManageEntity sceneManage = new SceneManageEntity() {{
            setId(sceneId);
            setLastPtTime(new Date());
            setUpdateTime(new Date());
            setStatus(SceneManageStatusEnum.FAILED.getValue());
        }};
        // --->update 失败状态
        sceneManageDAO.getBaseMapper().updateById(sceneManage);

        String resourceId = recentlyReport.getResourceId();
        String startKey = PressureStartCache.getStartFlag(resourceId);
        if (!redisClientUtil.hasKey(startKey)) {
            // 触发启动失败事件
            Event event = new Event();
            event.setEventName(PressureStartCache.START_FAILED);
            StartFailEventSource source = new StartFailEventSource();
            source.setContext(getResourceContext(resourceId));
            source.setMessage(errorMsg);
            event.setExt(source);
            eventCenterTemplate.doEvents(event);
        }
    }

    @Override
    public void reportRecord(SceneManageStartRecordVO recordVO) {
        if (!existSceneManage(recordVO.getSceneId())) {
            log.error("异常代码【{}】,异常内容：获取场景信息失败 --> 找不到对应的场景: {}",
                TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, recordVO.getSceneId());
            toFailureState(recordVO.getSceneId(), recordVO.getResultId(),
                String.format("场景%s不存在", recordVO.getSceneId()));
        }
        if (!recordVO.getSuccess()) {
            // 目前只记录失败
            toFailureState(recordVO.getSceneId(), recordVO.getResultId(), recordVO.getErrorMsg());
        }
    }

    @Override
    public List<SceneManageListOutput> querySceneManageList() {
        List<SceneManage> sceneManages = tSceneManageMapper.selectAllSceneManageList();
        if (CollectionUtils.isNotEmpty(sceneManages)) {
            return SceneManageDTOConvert.INSTANCE.ofs(sceneManages);
        }

        return Lists.newArrayList();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateFileByScriptId(CloudUpdateSceneFileRequest request) {
        // 填充租户信息
        CloudPluginUtils.fillUserData(request);
        // 查出所有请求租户的所有场景
        log.info("更新脚本对应的文件 --> 求出租户下的所有场景");
        List<SceneManageEntity> sceneManageList = sceneManageDAO.listFromUpdateScript(request);
        if (sceneManageList.isEmpty()) {
            return;
        }

        // 根据 oldScriptId 过滤, 并收集新的 scene, 用来更新
        log.info("更新脚本对应的文件 --> 根据老脚本发布id获得对应的所有场景");
        List<SceneManageEntity> newSceneManageEntities = sceneManageList.stream().map(sceneManage -> {
            // 扩展字段
            String features = sceneManage.getFeatures();
            if (StrUtil.isBlank(features)) {
                return null;
            }

            // 根据 oldScriptId 过滤
            //  张天赐 - 兼容新老版本
            //SceneMangeFeaturesVO featuresVO = JSONUtil.toBean(features, SceneMangeFeaturesVO.class);
            Map<String, Object> feature = JSONObject.parseObject(features, new TypeReference<Map<String, Object>>() {});
            if (feature == null
                || !feature.containsKey("scriptId") || feature.get("scriptId") == null
                || !Objects.equals(Long.parseLong(feature.get("scriptId").toString()), request.getOldScriptId())) {
                return null;
            }
            // 赋值新的 scriptId
            feature.put("scriptId", request.getNewScriptId());

            SceneManageEntity newSceneManageEntity = new SceneManageEntity();
            newSceneManageEntity.setId(sceneManage.getId());
            newSceneManageEntity.setFeatures(JSONUtil.toJsonStr(feature));
            return newSceneManageEntity;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (newSceneManageEntities.isEmpty()) {
            return;
        }

        // 更新文件
        this.doUpdateFileByScriptId(request, newSceneManageEntities);
        log.info("更新脚本对应的文件 --> 更新结束");
    }

    @Override
    public List<SceneManageWrapperOutput> getByIds(List<Long> sceneIds) {
        if (CollectionUtils.isEmpty(sceneIds)) {
            return Lists.newArrayList();

        }
        List<SceneManage> byIds = tSceneManageMapper.getByIds(sceneIds);
        List<SceneManageWrapperOutput> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(byIds)) {
            byIds.forEach(sceneManage -> {
                SceneManageWrapperOutput output = new SceneManageWrapperOutput();
                output.setPressureTestSceneName(sceneManage.getSceneName());
                output.setId(sceneManage.getId());
                result.add(output);
            });
        }
        return result;
    }

    /**
     * 更新文件
     *
     * @param request                请求参数
     * @param newSceneManageEntities 要更新的场景列表
     */
    private void doUpdateFileByScriptId(CloudUpdateSceneFileRequest request,
        List<SceneManageEntity> newSceneManageEntities) {
        // 转成需要入参
        List<UploadFileDTO> uploadFiles = request.getUploadFiles();
        List<SceneScriptRefInput> inputList = this.uploadFiles2InputList(uploadFiles);

        log.info("更新脚本对应的文件 --> 脚本更新开始");
        // 转换关联类
        List<SceneScriptRef> sceneScriptList = this.buildScriptRef(inputList, request.getScriptType());
        for (SceneManageEntity scene : newSceneManageEntities) {
            Long sceneId = scene.getId();

            // 更改脚本上传路径
            for (SceneScriptRef sceneScript : sceneScriptList) {
                sceneScript.setUploadPath(sceneId + "/" + sceneScript.getFileName());
                sceneScript.setSceneId(sceneId);
            }

            // 更新
            this.dealScriptRefFileByScriptManage(sceneScriptList, sceneId);
            if (StringUtils.isBlank(scriptPath)) {
                continue;
            }

            //如果覆盖大文件 就直接️删整个场景目录 否则只保留大文件,其他文件删除;如果没有值，默认覆盖
            if (request.getIfCoverBigFile() == null || request.getIfCoverBigFile().equals(1)) {
                this.operateFileOnSystem(inputList, sceneId);
            } else {
                this.updateFilesExceptBigFile(inputList, sceneId);
            }
        }

        // 更新 scene
        log.info("更新脚本对应的文件 --> 场景更新, 更新对应的脚本发布id");
        if (!sceneManageDAO.updateBatchById(newSceneManageEntities)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_ERROR, "场景数据更新失败!");
        }

    }

    private void updateFilesExceptBigFile(List<SceneScriptRefInput> inputList, Long sceneId) {
        String destPath = scriptPath + SceneManageConstant.FILE_SPLIT + sceneId + SceneManageConstant.FILE_SPLIT;
        try {
            this.delFilesByDirExceptBigFile(inputList, destPath);
            this.transferTo(inputList, destPath);
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_ERROR,
                "updateFilesExceptBigFile 更新文件失败：" + e.getMessage());
        }
    }

    /**
     * 操作系统的文件
     *
     * @param inputList 文件入参
     * @param sceneId   场景id
     */
    private void operateFileOnSystem(List<SceneScriptRefInput> inputList, Long sceneId) {
        String destPath = scriptPath + SceneManageConstant.FILE_SPLIT + sceneId + SceneManageConstant.FILE_SPLIT;
        try {
            this.delDirFile(scriptPath + SceneManageConstant.FILE_SPLIT + sceneId);
            this.transferTo(inputList, destPath);
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_FILE_COPY_ERROR,
                "operateFileOnSystem 更新文件失败,原因：", e);
        }
    }

    /**
     * 文件迁移
     *
     * @param inputList 文件列表
     * @param destPath  目标路径
     */
    private void transferTo(List<SceneScriptRefInput> inputList, String destPath) {
        // 数据文件、脚本文件
        List<SceneScriptRefInput> normalFileList = inputList.stream()
            .filter(input -> FileTypeBusinessUtil.isScriptOrData(input.getFileType()))
            .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(normalFileList)) {
            for (SceneScriptRefInput file : normalFileList) {
                this.copyFile(file.getUploadPath(), destPath);
            }
        }

        // 附件
        List<SceneScriptRefInput> attachmentFileList = inputList.stream()
            .filter(input -> FileTypeBusinessUtil.isAttachment(input.getFileType()))
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(attachmentFileList)) {
            String attachmentPath = destPath + SceneManageConstant.FILE_SPLIT + "attachments";
            for (SceneScriptRefInput file : attachmentFileList) {
                this.copyFile(file.getUploadPath(), attachmentPath);
            }
        }
    }

    private void delFilesByDirExceptBigFile(List<SceneScriptRefInput> inputList, String destPath) {
        //找到大文件
        List<String> bigFileNames = inputList.stream()
            .filter(t -> StringUtil.isNotBlank(t.getFileExtend()))
            .filter(file -> {
                JSONObject json2Bean = JsonHelper.json2Bean(file.getFileExtend(), JSONObject.class);
                if (null != json2Bean) {
                    Integer isBigFile = json2Bean.getInteger("isBigFile");
                    return isBigFile != null && isBigFile.equals(1);
                }
                return false;
            }).map(SceneScriptRefInput::getFileName).collect(Collectors.toList());
        String bigFileName = CollectionUtils.isEmpty(bigFileNames) ? null : bigFileNames.get(0);
        //删除除了大文件之外的文件
        File destDir = new File(destPath);
        if (destDir.exists()) {
            File[] listFiles = destDir.listFiles();
            if (null != listFiles) {
                for (File file : listFiles) {
                    if (!file.getName().equals(bigFileName)) {
                        boolean delete = file.delete();
                        log.debug(
                            "io.shulie.takin.cloud.biz.service.scene.impl"
                                + ".SceneManageServiceImpl#delFilesByDirExceptBigFile:delete:{}.",
                            delete);
                    }
                }
            }
        }
    }

    /**
     * 入参脚本文件转成入参类
     *
     * @param uploadFiles 入参文件
     * @return 入参类列表
     */
    private List<SceneScriptRefInput> uploadFiles2InputList(List<UploadFileDTO> uploadFiles) {
        return uploadFiles.stream()
            .map(t -> BeanUtil.copyProperties(t, SceneScriptRefInput.class))
            .collect(Collectors.toList());
    }

    private Boolean existSceneManage(Long sceneId) {
        return sceneManageDAO.getSceneById(sceneId) != null;
    }

    @Override
    public void delete(Long id) {
        SceneManageEntity entity = new SceneManageEntity();
        entity.setIsDeleted(1);
        entity.setStatus(-1);
        entity.setId(id);
        sceneManageDAO.updateById(entity);
    }

    @Override
    public SceneManageWrapperOutput getSceneManage(Long id, SceneManageQueryOptions options) {
        SceneManageEntity sceneManageResult = getSceneManage(id);
        if (options == null) {
            options = new SceneManageQueryOptions();
        }
        SceneManageWrapperOutput wrapperDTO = new SceneManageWrapperOutput();
        fillBase(wrapperDTO, sceneManageResult);
        if (Boolean.TRUE.equals(options.getIncludeBusinessActivity())) {
            List<SceneBusinessActivityRef> businessActivityList = tSceneBusinessActivityRefMapper.selectBySceneId(id);
            List<SceneBusinessActivityRefOutput> dtoList = SceneManageDTOConvert.INSTANCE.ofBusinessActivityList(
                businessActivityList);
            wrapperDTO.setBusinessActivityConfig(dtoList);
        }

        if (Boolean.TRUE.equals(options.getIncludeScript())) {
            List<SceneScriptRef> scriptList = tSceneScriptRefMapper.selectBySceneIdAndScriptType(id,
                wrapperDTO.getScriptType());
            List<SceneScriptRefOutput> dtoList = SceneManageDTOConvert.INSTANCE.ofScriptList(scriptList);
            wrapperDTO.setUploadFile(dtoList);
        }

        if (Boolean.TRUE.equals(options.getIncludeSLA())) {
            List<SceneSlaRef> slaList = tSceneSlaRefMapper.selectBySceneId(id);
            List<SceneSlaRefOutput> dtoList = SceneManageDTOConvert.INSTANCE.ofSlaList(slaList);
            wrapperDTO.setStopCondition(
                dtoList.stream().filter(data -> SceneManageConstant.EVENT_DESTORY.equals(data.getEvent()))
                    .collect(Collectors.toList()));
            wrapperDTO.setWarningCondition(
                dtoList.stream().filter(data -> SceneManageConstant.EVENT_WARN.equals(data.getEvent()))
                    .collect(Collectors.toList()));
        }

        wrapperDTO.setTotalTestTime(wrapperDTO.getPressureTestSecond());
        wrapperDTO.setUserId(sceneManageResult.getUserId());
        return wrapperDTO;
    }

    @Override
    public void saveUnUploadLogInfo() {

    }

    @Override
    public ScriptVerityRespExt checkAndUpdate(List<String> request, String uploadPath, boolean isAbsolutePath,
        boolean update, Integer version) {
        String path;
        if (!isAbsolutePath) {
            path = scriptPath + SceneManageConstant.FILE_SPLIT + uploadPath;
            //兼容性处理
            File file = new File(path);
            if (!file.exists() && uploadPath.startsWith(scriptPath + SceneManageConstant.FILE_SPLIT)) {
                path = uploadPath;
            }
        } else {
            path = uploadPath;
        }
        ScriptVerityExt scriptVerityExt = new ScriptVerityExt();
        scriptVerityExt.setRequest(request);
        scriptVerityExt.setVersion(version);
        scriptVerityExt.setScriptPath(path);
        ScriptVerityRespExt scriptVerityRespExt = scriptAnalyzeService.verityScript(scriptVerityExt);
        if (scriptVerityRespExt != null && CollectionUtils.isNotEmpty(scriptVerityRespExt.getErrorMsg())) {
            return scriptVerityRespExt;
        }
        if (update) {
            scriptAnalyzeService.updateScriptContent(uploadPath);
        }
        return null;

    }

    private SceneManageEntity getSceneManage(Long id) {
        if (id == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "ID不能为空");
        }
        SceneManageEntity result = sceneManageDAO.getSceneById(id);
        if (result == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "场景记录不存在" + id);
        }
        return result;
    }

    @Override
    public List<SceneBusinessActivityRefOutput> getBusinessActivityBySceneId(Long sceneId) {
        List<SceneBusinessActivityRef> businessActivityList = tSceneBusinessActivityRefMapper.selectBySceneId(sceneId);
        return SceneManageDTOConvert.INSTANCE.ofBusinessActivityList(businessActivityList);
    }

    @Override
    public BigDecimal calcEstimateFlow(List<AssetBillExt> bills) {
        // 尝试调用插件
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            Response<BigDecimal> res = assetExtApi.calcEstimateAmount(bills);
            if (null != res && res.isSuccess()) {
                return res.getData();
            }
            return BigDecimal.ZERO;
        }
        // 返回0
        else {
            return BigDecimal.ZERO;
        }

    }

    private void fillBase(SceneManageWrapperOutput wrapperOutput, SceneManageEntity sceneManageResult) {
        wrapperOutput.setId(sceneManageResult.getId());
        wrapperOutput.setScriptType(sceneManageResult.getScriptType());
        wrapperOutput.setPressureTestSceneName(sceneManageResult.getSceneName());
        wrapperOutput.setType(sceneManageResult.getType());

        // 状态适配
        wrapperOutput.setStatus(SceneManageStatusEnum.getAdaptStatus(sceneManageResult.getStatus()));
        wrapperOutput.setUserId(sceneManageResult.getUserId());
        wrapperOutput.setEnvCode(sceneManageResult.getEnvCode());
        wrapperOutput.setTenantId(sceneManageResult.getTenantId());
        wrapperOutput.setUpdateTime(DateUtil.formatDateTime(sceneManageResult.getUpdateTime()));
        wrapperOutput.setLastPtTime(DateUtil.formatDateTime(sceneManageResult.getLastPtTime()));
        wrapperOutput.setLastPtDateTime(sceneManageResult.getLastPtTime());
        if (StringUtils.isBlank(sceneManageResult.getScriptAnalysisResult())) {
            fillPtConfigOld(wrapperOutput, sceneManageResult.getPtConfig());
        } else {
            wrapperOutput.setPressureType(sceneManageResult.getType());
            fillPtConfig(wrapperOutput, sceneManageResult.getPtConfig());
        }
        wrapperOutput.setFeatures(sceneManageResult.getFeatures());
        wrapperOutput.setScriptAnalysisResult(sceneManageResult.getScriptAnalysisResult());
    }

    /**
     * 老版本ptConfig数据，转成新版本数据
     */
    private void fillPtConfigOld(SceneManageWrapperOutput wrapperOutput, String ptConfig) {
        try {
            JSONObject json = JsonUtil.parse(ptConfig);
            if (null == json) {
                return;
            }

            Integer ptType = json.getInteger(SceneManageConstant.PT_TYPE);
            ThreadGroupConfigExt tgConfig = new ThreadGroupConfigExt();
            tgConfig.setType(ptType);
            tgConfig.setThreadNum(json.getInteger(SceneManageConstant.THREAD_NUM));
            PressureModeEnum mode = PressureModeEnum.value(json.getInteger(SceneManageConstant.PT_MODE));
            if (null != mode) {
                tgConfig.setMode(mode.getCode());
            }
            tgConfig.setRampUp(json.getInteger(SceneManageConstant.STEP_DURATION));
            tgConfig.setRampUpUnit(json.getString(SceneManageConstant.STEP_DURATION_UNIT));
            tgConfig.setSteps(json.getInteger(SceneManageConstant.STEP));
            tgConfig.setEstimateFlow(json.getDouble(SceneManageConstant.ESTIMATE_FLOW));
            // 递增时长
            if (tgConfig.getRampUp() != null) {
                String rampUpUnit = tgConfig.getRampUpUnit() == null ? "m" : tgConfig.getRampUpUnit();
                tgConfig.setRampUp(convertTime(Long.valueOf(tgConfig.getRampUp()), rampUpUnit).intValue());
                tgConfig.setRampUpUnit(TimeUnitEnum.SECOND.getValue());
            }

            Map<String, ThreadGroupConfigExt> map = new HashMap<>(1);
            map.put("all", tgConfig);

            wrapperOutput.setConcurrenceNum(tgConfig.getThreadNum());
            wrapperOutput.setIpNum(json.getInteger(SceneManageConstant.HOST_NUM));
            wrapperOutput.setPressureType(ptType);
            //压测时长
            TimeBean duration = new TimeBean(json.getLong(SceneManageConstant.PT_DURATION),
                json.getString(SceneManageConstant.PT_DURATION_UNIT));
            wrapperOutput.setPressureTestTime(duration);
            wrapperOutput.setPressureTestSecond(duration.getSecondTime());
            wrapperOutput.setThreadGroupConfigMap(map);
            if (null != tgConfig.getEstimateFlow()) {
                wrapperOutput.setEstimateFlow(
                    BigDecimal.valueOf(tgConfig.getEstimateFlow()).setScale(2, RoundingMode.HALF_UP));
            }
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "施压配置json解析错误", e);
        }
    }

    /**
     * 新版本ptConfig数据
     */
    private void fillPtConfig(SceneManageWrapperOutput wrapperOutput, String config) {
        try {
            PtConfigExt ptConfig = JSON.parseObject(config, PtConfigExt.class);
            if (null == ptConfig) {
                return;
            }
            wrapperOutput.setIpNum(ptConfig.getPodNum());
            //压测时长
            wrapperOutput.setPressureTestTime(new TimeBean(ptConfig.getDuration(), ptConfig.getUnit()));
            wrapperOutput.setPressureTestSecond(convertTime(ptConfig.getDuration(), ptConfig.getUnit()));
            wrapperOutput.setThreadGroupConfigMap(ptConfig.getThreadGroupConfigMap());
            // 递增时长
            wrapperOutput.getThreadGroupConfigMap().forEach((k, v) -> {
                if (v.getRampUp() != null) {
                    String rampUpUnit = v.getRampUpUnit() == null ? "m" : v.getRampUpUnit();
                    v.setRampUp(convertTime(Long.valueOf(v.getRampUp()), rampUpUnit).intValue());
                    v.setRampUpUnit(TimeUnitEnum.SECOND.getValue());
                }
            });
            //预计消耗流量
            if (null != ptConfig.getEstimateFlow()) {
                BigDecimal flow = BigDecimal.valueOf(ptConfig.getEstimateFlow());
                wrapperOutput.setEstimateFlow(flow.setScale(2, RoundingMode.HALF_UP));
            } else if (MapUtils.isNotEmpty(ptConfig.getThreadGroupConfigMap())) {
                double flow = ptConfig.getThreadGroupConfigMap().values().stream().filter(Objects::nonNull)
                    .map(ThreadGroupConfigExt::getEstimateFlow)
                    .mapToDouble(d -> null == d ? 0d : d)
                    .sum();
                wrapperOutput.setEstimateFlow(new BigDecimal(flow).setScale(2, RoundingMode.HALF_UP));
            }
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "施压配置json解析错误", e);
        }
    }

    /**
     * 秒转化为时、分、秒
     *
     * @return -
     */
    private Long convertTime(Long time, String unit) {
        if (time == null) {
            return 0L;
        }
        TimeUnitEnum tue = TimeUnitEnum.value(unit);
        if (null == tue) {
            return time;
        }
        return TimeUnit.SECONDS.convert(time, tue.getUnit());
    }

    private SceneManageCreateOrUpdateParam buildSceneManage(SceneManageWrapperInput wrapperVO) {
        SceneManageCreateOrUpdateParam param = new SceneManageCreateOrUpdateParam();
        param.setId(wrapperVO.getId());
        param.setSceneName(wrapperVO.getPressureTestSceneName());
        param.setScriptType(wrapperVO.getScriptType());
        param.setPtConfig(buildPtConfig(wrapperVO));
        param.setStatus(SceneManageStatusEnum.WAIT.getValue());
        param.setType(wrapperVO.getType() == null ? 0 : wrapperVO.getType());
        param.setFeatures(wrapperVO.getFeatures());
        param.setUserId(wrapperVO.getUserId());
        param.setTenantId(wrapperVO.getTenantId());
        param.setEnvCode(wrapperVO.getEnvCode());
        return param;
    }

    private String buildPtConfig(SceneManageWrapperInput wrapperVO) {
        Map<String, Object> map = Maps.newHashMap();
        //默认为并发模式，兼容之前的版本
        if (wrapperVO.getPressureType() == null) {
            wrapperVO.setPressureType(0);
        }
        map.put(SceneManageConstant.PT_TYPE, wrapperVO.getPressureType());
        //        if (PressureSceneEnum.DEFAULT.equals(wrapperVO.getPressureType()) && wrapperVO.getConcurrenceNum()
        //        == null) {
        //            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_BUILD_PARAM_ERROR,
        //            "并发模式中，并发数不能为空");
        //        }
        map.put(SceneManageConstant.THREAD_NUM, wrapperVO.getConcurrenceNum());
        map.put(SceneManageConstant.HOST_NUM, wrapperVO.getIpNum());
        map.put(SceneManageConstant.PT_DURATION, wrapperVO.getPressureTestTime().getTime());
        map.put(SceneManageConstant.PT_DURATION_UNIT, wrapperVO.getPressureTestTime().getUnit());
        map.put(SceneManageConstant.PT_MODE, wrapperVO.getPressureMode());
        map.put(SceneManageConstant.STEP_DURATION,
            wrapperVO.getIncreasingTime() != null ? wrapperVO.getIncreasingTime().getTime() : null);
        map.put(SceneManageConstant.STEP_DURATION_UNIT,
            wrapperVO.getIncreasingTime() != null ? wrapperVO.getIncreasingTime().getUnit() : null);
        map.put(SceneManageConstant.STEP, wrapperVO.getStep());
        BigDecimal value = new BigDecimal(0);
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            Response<BigDecimal> res = assetExtApi.calcEstimateAmount(
                BeanUtil.copyProperties(wrapperVO, AssetBillExt.class, ""));
            if (null != res && res.isSuccess() && null != res.getData()) {
                value = res.getData();
            }
        }
        map.put(SceneManageConstant.ESTIMATE_FLOW, value);
        return JSON.toJSONString(map);
    }

    private List<SceneBusinessActivityRef> buildSceneBusinessActivityRef(List<SceneBusinessActivityRefInput> voList) {
        List<SceneBusinessActivityRef> businessActivityList = Lists.newArrayList();
        for (SceneBusinessActivityRefInput data : voList) {
            SceneBusinessActivityRef ref = new SceneBusinessActivityRef();
            ref.setBusinessActivityId(data.getBusinessActivityId());
            ref.setBusinessActivityName(data.getBusinessActivityName());
            ref.setBindRef(data.getBindRef());
            ref.setApplicationIds(data.getApplicationIds());
            ref.setGoalValue(buildGoalValue(data));
            businessActivityList.add(ref);
        }
        return businessActivityList;
    }

    private String buildGoalValue(SceneBusinessActivityRefInput vo) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(SceneManageConstant.TPS, vo.getTargetTPS());
        map.put(SceneManageConstant.RT, vo.getTargetRT());
        map.put(SceneManageConstant.SUCCESS_RATE, vo.getTargetSuccessRate());
        map.put(SceneManageConstant.SA, vo.getTargetSA());
        return JSON.toJSONString(map);
    }

    private List<SceneScriptRef> buildScriptRef(List<SceneScriptRefInput> voList, Integer scriptType) {
        List<SceneScriptRef> scriptList = Lists.newArrayList();
        voList.forEach(data -> {
            SceneScriptRef ref = new SceneScriptRef();
            ref.setId(data.getId());
            ref.setScriptType(scriptType);
            ref.setUploadId(data.getUploadId());
            ref.setFileName(data.getFileName());
            ref.setFileType(data.getFileType());
            ref.setFileSize(data.getFileSize());
            if (data.getUploadId() != null) {
                ref.setUploadPath(data.getUploadId() + SceneManageConstant.FILE_SPLIT + data.getFileName());
            }
            if (data.getId() != null) {
                ref.setUploadPath(data.getUploadPath());
            }

            Map<String, Object> extend = Maps.newHashMap();
            extend.put(SceneManageConstant.DATA_COUNT, data.getUploadedData());
            extend.put(SceneManageConstant.IS_SPLIT, data.getIsSplit());
            extend.put(SceneManageConstant.TOPIC, data.getTopic());
            extend.put(SceneManageConstant.IS_ORDERED_SPLIT, data.getIsOrderSplit());
            ref.setFileExtend(JSON.toJSONString(extend));

            ref.setIsDeleted(data.getIsDeleted());
            ref.setUploadTime(DateUtil.parseDateTime(data.getUploadTime()));
            ref.setFileMd5(data.getFileMd5());
            scriptList.add(ref);
        });
        return scriptList;
    }

    private List<SceneSlaRef> buildSceneSlaRef(List<SceneSlaRefInput> voList, String event) {
        List<SceneSlaRef> slaList = Lists.newArrayList();
        voList = voList.stream().filter(
            data -> data.getBusinessActivity() != null && data.getBusinessActivity().length > 0).collect(
            Collectors.toList());
        if (CollectionUtils.isEmpty(voList)) {
            return slaList;
        }
        voList.forEach(data -> {
            SceneSlaRef ref = new SceneSlaRef();
            ref.setSlaName(data.getRuleName());
            if (data.getBusinessActivity() != null && data.getBusinessActivity().length > 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : data.getBusinessActivity()) {
                    sb.append(s);
                    sb.append(",");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                ref.setBusinessActivityIds(sb.toString());
            }
            ref.setTargetType(data.getRule().getIndexInfo());
            ref.setCondition(buildSlaCondition(data.getRule(), event));
            ref.setStatus(data.getStatus());
            slaList.add(ref);
        });
        return slaList;
    }

    private String buildSlaCondition(RuleBean vo, String event) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(SceneManageConstant.COMPARE_TYPE, vo.getCondition());
        map.put(SceneManageConstant.COMPARE_VALUE, vo.getDuring());
        map.put(SceneManageConstant.ACHIEVE_TIMES, vo.getTimes());
        map.put(SceneManageConstant.EVENT, event);
        return JSON.toJSONString(map);
    }

    private void fillSceneId(List<? extends SceneRef> refList, Long sceneId) {
        if (CollectionUtils.isEmpty(refList)) {
            return;
        }
        refList.forEach(data -> data.setSceneId(sceneId));
    }

    private void moveTempFile(List<SceneScriptRef> scriptList, Long sceneId) {
        String dirPath = scriptPath + SceneManageConstant.FILE_SPLIT + sceneId;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            log.debug("io.shulie.takin.cloud.biz.service.scene.impl.SceneManageServiceImpl#moveTempFile:mkdir:{}.",
                mkdir);
        }
        scriptList.stream().filter(data -> data.getUploadId() != null).forEach(data -> {
            String tempPath = scriptTempPath
                + SceneManageConstant.FILE_SPLIT
                + data.getUploadId()
                + SceneManageConstant.FILE_SPLIT
                + data.getFileName();
            File file = new File(tempPath);
            data.setFileSize(LinuxUtil.getPrintSize(file.length()));
            data.setUploadPath(sceneId + SceneManageConstant.FILE_SPLIT + data.getFileName());
            LinuxUtil.executeLinuxCmd("mv " + tempPath + " " + dirPath);
            LinuxUtil.executeLinuxCmd("rm -rf " + scriptTempPath
                + SceneManageConstant.FILE_SPLIT
                + data.getUploadId());
        });
    }

    private void deleteUploadFile(List<String> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        files.forEach(file ->
            LinuxUtil.executeLinuxCmd("rm -rf " + scriptPath
                + SceneManageConstant.FILE_SPLIT
                + file)
        );
    }

    @Override
    public void recovery(Long id) {
        SceneManageEntity entity = new SceneManageEntity();
        entity.setId(id);
        entity.setStatus(0);
        entity.setIsArchive(0);
        sceneManageDAO.updateById(entity);
    }

    @Override
    public void archive(Long id) {
        SceneManageEntity entity = new SceneManageEntity();
        entity.setId(id);
        entity.setStatus(-1);
        entity.setIsArchive(1);
        sceneManageDAO.updateById(entity);
    }
}
