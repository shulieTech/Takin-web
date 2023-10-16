package io.shulie.takin.web.biz.service.datamanage.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.shulie.takin.adapter.api.entrypoint.file.CloudFileApi;
import io.shulie.takin.adapter.api.model.common.UploadFileDTO;
import io.shulie.takin.adapter.api.model.request.file.UploadRequest;
import io.shulie.takin.adapter.api.model.request.filemanager.FileDeleteParamReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.common.utils.Md5Util;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.constant.ScriptCsvCreateTaskState;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileV2Request;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.*;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.*;
import io.shulie.takin.web.biz.service.datamanage.CsvManageService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.ApiMatcher;
import io.shulie.takin.web.biz.utils.CsvTemplateUtils;
import io.shulie.takin.web.biz.utils.exception.ScriptManageExceptionUtil;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.data.dao.linkmanage.SceneDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptFileRefDAO;
import io.shulie.takin.web.data.mapper.mysql.FileManageMapper;
import io.shulie.takin.web.data.mapper.mysql.SceneMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptCsvCreateTaskMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptCsvDataSetMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.model.mysql.ScriptCsvCreateTaskEntity;
import io.shulie.takin.web.data.model.mysql.ScriptCsvDataSetEntity;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptFileRefResult;
import io.shulie.takin.web.diff.api.DiffFileApi;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.ext.api.traffic.TrafficRecorderExtApi;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.traffic.TrafficRecorderExtResponse;
import io.shulie.takin.web.ext.entity.traffic.TrafficRecorderQueryExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CsvManageServiceImpl implements CsvManageService {

    @Value("${script.temp.path}")
    private String tempPath;

    @Autowired
    private ScriptManageService scriptManageService;

    @Autowired
    private ScriptCsvDataSetMapper scriptCsvDataSetMapper;

    @Autowired
    private FileManageMapper fileManageMapper;

    @Autowired
    private ScriptFileRefDAO scriptFileRefDAO;

    @Resource
    private SceneDAO sceneDao;

    @Autowired
    private ScriptCsvCreateTaskMapper scriptCsvCreateTaskMapper;
    @Resource
    private PluginManager pluginManager;

    @Resource
    private SceneManageApi sceneManageApi;

    @Resource
    private CloudFileApi cloudFileApi;

    @Autowired
    private SceneDAO sceneDAO;

    @Resource
    private SceneMapper sceneMapper;

    @Resource
    private DiffFileApi fileApi;

    private static String CSV_TASK_REDIS_KEY = "script:csv:task";
    private static String CSV_TASK_RUN_REDIS_KEY = "script:csv:task:run";


    @Override
    public List<ScriptCsvDataSetEntity> transformFromJmeter(ScriptAnalyzeRequest request) {
        if (StringUtils.isBlank(request.getScriptFile())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_ANALYZE_PARAMS_ERROR, "请提供脚本文件完整的路径和名称");
        }
        File file = new File(request.getScriptFile());
        if (!file.exists() || !file.isFile()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_FILE_NOT_EXISTS, "请检测脚本文件是否存在");
        }
        List<ScriptNode> scriptNodes = JmxUtil.buildCsvNode(request.getScriptFile());
        if (CollectionUtils.isEmpty(scriptNodes)) {
            return Lists.newArrayList();
        }
        List<ScriptCsvDataSetEntity> tempCsvDataSets = scriptNodes.stream().map(t -> {
            ScriptCsvDataSetEntity setEntity = new ScriptCsvDataSetEntity();
            setEntity.setScriptCsvDataSetName(t.getTestName());
            Map<String, String> props = t.getProps();
            // 文件名
            Path path = Paths.get(props.getOrDefault("filename", ""));
            setEntity.setScriptCsvFileName(path.getFileName().toString());
            setEntity.setScriptCsvVariableName(props.getOrDefault("variableNames", ""));
            setEntity.setIgnoreFirstLine("true".equals(props.getOrDefault("ignoreFirstLine", "false")));
            setEntity.setCreateTime(LocalDateTime.now());
            setEntity.setUpdateTime(LocalDateTime.now());
            return setEntity;
        }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(tempCsvDataSets)) {
            // 2.2 先校验 csv的文件名不重复
            List<ScriptCsvDataSetEntity> distinct = this.distinct(tempCsvDataSets);
            if (tempCsvDataSets.size() != distinct.size()) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "jmeter脚本存在相同文件名的csv组件，请修改csv文件名！");
            }
            // 3.3. 如果存在变量相同的也丢弃
            for (ScriptCsvDataSetEntity scriptCsvDataSetEntity : distinct) {
                String scriptCsvVariableName = scriptCsvDataSetEntity.getScriptCsvVariableName();
                String[] variableNames = scriptCsvVariableName.split(",");
                if (Sets.newHashSet(variableNames).size() != variableNames.length) {
                    throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "同一个组件内不允许存在相同变量名的变量配置");
                }
            }
            return distinct;
        }
        return Lists.newArrayList();
    }


    @Override
    public void save(List<ScriptCsvDataSetEntity> setEntityList, BusinessFlowParseRequest businessFlowParseRequest) {
        // 1. 获取原有的csv
        List<ScriptCsvDataSetEntity> oldCsv = scriptCsvDataSetMapper.listByBusinessFlowId(businessFlowParseRequest.getId());
        Map<String, ScriptCsvDataSetEntity> oldDataMap = oldCsv.stream().collect(Collectors.toMap(this::getIndex, t -> t));
        Set<String> indexKeys = oldDataMap.keySet();
        // 2. 对比现有的，用文件名来对比，分成3种情况
        List<ScriptCsvDataSetEntity> addList = Lists.newArrayList();
        List<ScriptCsvDataSetEntity> unChangeList = Lists.newArrayList();
        for (ScriptCsvDataSetEntity setEntity : setEntityList) {
            if (indexKeys.contains(this.getIndex(setEntity))) {
                unChangeList.add(oldDataMap.get(this.getIndex(setEntity)));
                continue;
            }
            // 2.1 新增csv的，则直接添加进去
            setEntity.setBusinessFlowId(businessFlowParseRequest.getId());
            setEntity.setScriptDeployId(businessFlowParseRequest.getScriptDeployId());
            setEntity.setIsSplit(false);
            setEntity.setIsOrderSplit(false);
            addList.add(setEntity);
        }

        if (!CollectionUtils.isEmpty(addList)) {
            scriptCsvDataSetMapper.batchInsert(addList);
        }
        // 2.3 未做改变的，需要更换脚本id
        if (!CollectionUtils.isEmpty(unChangeList)) {
            unChangeList.forEach(t -> {
                t.setScriptDeployId(businessFlowParseRequest.getScriptDeployId());
            });
            scriptCsvDataSetMapper.updateBatch(unChangeList);
        }


        //得出需要删除
        // 2.2 原有的csv，但组件名修改或者变量名称修改了  将该文件历史全部删除，如果有任务，则自动取消任务
        List<String> unchangeList = unChangeList.stream().map(this::getIndex).collect(Collectors.toList());
        List<ScriptCsvDataSetEntity> deleteList = oldCsv.stream().filter(t -> !unchangeList.contains(this.getIndex(t))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(deleteList)) {
            // 1. 取消任务
            List<Long> csvDataSetIds = deleteList.stream().map(ScriptCsvDataSetEntity::getId).collect(Collectors.toList());
            LambdaQueryWrapper<ScriptCsvCreateTaskEntity> taskWrapper = new LambdaQueryWrapper<>();
            taskWrapper.in(ScriptCsvCreateTaskEntity::getScriptCsvDataSetId, csvDataSetIds);
            taskWrapper.in(ScriptCsvCreateTaskEntity::getCreateStatus, Arrays.asList(ScriptCsvCreateTaskState.IN_FORMATION, ScriptCsvCreateTaskState.BE_QUEUING));
            List<ScriptCsvCreateTaskEntity> updateTaskList = scriptCsvCreateTaskMapper.selectList(taskWrapper);
            updateTaskList.forEach(t -> {
                t.setCreateStatus(ScriptCsvCreateTaskState.CANCELLED);
                t.setRemark("文件重新上传，css组件变更");
                t.setUpdateTime(LocalDateTime.now());
            });
            scriptCsvCreateTaskMapper.updateBatch(updateTaskList);
            // 2. 文件删除
            LambdaQueryWrapper<FileManageEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(FileManageEntity::getScriptCsvDataSetId, csvDataSetIds);
            List<FileManageEntity> fileManageEntityList = fileManageMapper.selectList(wrapper);

            FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
            fileDeleteParamReq.setPaths(fileManageEntityList.stream().map(FileManageEntity::getUploadPath).collect(Collectors.toList()));
            fileApi.deleteFile(fileDeleteParamReq);

            // 3. 文件关联关系 删除
            List<Long> fileIds = fileManageEntityList.stream().map(FileManageEntity::getId).collect(Collectors.toList());
            List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectByFileManageIds(fileIds);
            scriptFileRefDAO.deleteByIds(scriptFileRefResults.stream().map(ScriptFileRefResult::getId).collect(Collectors.toList()));

        }

        // 更新场景 其实有点重复更新了，现在这样吧 在jmx脚本更新的时候也更新过了
        this.updateSceneManage(businessFlowParseRequest.getScriptDeployId());
    }


    @Override
    public List<ScriptCsvDataSetResponse> listCsvByBusinessFlowId(Long businessFlowId) {
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = scriptCsvDataSetMapper.listByBusinessFlowId(businessFlowId);
        // 获取文件表数据
        List<Long> fileIds = csvDataSetEntityList.stream().map(ScriptCsvDataSetEntity::getFileManageId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fileIds)) {
            return DataTransformUtil.list2list(csvDataSetEntityList, ScriptCsvDataSetResponse.class);
        }
        LambdaQueryWrapper<FileManageEntity> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.in(FileManageEntity::getId, fileIds);
        fileWrapper.eq(FileManageEntity::getIsDeleted, 0);
        List<FileManageEntity> fileManageEntityList = fileManageMapper.selectList(fileWrapper);
        Map<Long, FileManageEntity> fileIdMap = fileManageEntityList.stream().collect(Collectors.toMap(FileManageEntity::getId, t -> t));
        return csvDataSetEntityList.stream().map(t -> {
            ScriptCsvDataSetResponse response = new ScriptCsvDataSetResponse();
            BeanUtils.copyProperties(t, response);
            response.setIsSplit(t.getIsSplit() ? 1 : 0);
            response.setIsOrderSplit(t.getIsOrderSplit() ? 1 : 0);

            FileManageEntity fileManageEntity = fileIdMap.get(response.getFileManageId());
            if (fileManageEntity != null) {
                response.setAliasName(fileManageEntity.getAliasName());
                response.setCreateTime(fileManageEntity.getGmtCreate());
                response.setUploadPath(fileManageEntity.getUploadPath());
                response.setCreateType(fileManageEntity.getCreateType());
            }
            return response;
        }).collect(Collectors.toList());
    }


    @Override
    public List<FileManageResponse> listAnnexByBusinessFlowId(Long businessFlowId) {
        SceneResult sceneDetail = sceneDao.getSceneDetail(businessFlowId);
        if (sceneDetail == null) {
            throw new RuntimeException("未找到该业务流程");
        }
        Long scriptDeployId = sceneDetail.getScriptDeployId();

        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployId(scriptDeployId);
        if (CollectionUtils.isEmpty(scriptFileRefResults)) {
            log.error("不存在关联的文件id");
            return Lists.newArrayList();
        }
        List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(Collectors.toList());
        LambdaQueryWrapper<FileManageEntity> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.in(FileManageEntity::getId, fileIds);
        fileWrapper.eq(FileManageEntity::getIsDeleted, 0);
        List<FileManageEntity> fileManageEntityList = fileManageMapper.selectList(fileWrapper);
        return fileManageEntityList.stream().filter(f -> f.getFileType().equals(FileTypeEnum.ATTACHMENT.getCode())).map(t -> {
            FileManageResponse fileManageResponse = new FileManageResponse();
            BeanUtils.copyProperties(t, fileManageResponse);
            return fileManageResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ScriptCsvCreateDetailResponse> createDetail(Long businessFlowId, Long scriptCsvDataSetId) {
        // 1. 获取组件列表
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = Lists.newArrayList();
        if (scriptCsvDataSetId != null) {
            ScriptCsvDataSetEntity scriptCsvDataSetEntity = scriptCsvDataSetMapper.selectById(scriptCsvDataSetId);
            if (scriptCsvDataSetEntity == null) {
                return Lists.newArrayList();
            }
            csvDataSetEntityList.add(scriptCsvDataSetEntity);
        } else {
            csvDataSetEntityList.addAll(scriptCsvDataSetMapper.listByBusinessFlowId(businessFlowId));
        }

        if (CollectionUtils.isEmpty(csvDataSetEntityList)) {
            return Lists.newArrayList();
        }
        // 2. 获取任务列表
        List<Long> ids = csvDataSetEntityList.stream().map(ScriptCsvDataSetEntity::getId).collect(Collectors.toList());
        LambdaQueryWrapper<ScriptCsvCreateTaskEntity> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.in(ScriptCsvCreateTaskEntity::getScriptCsvDataSetId, ids);
        taskWrapper.orderByDesc(ScriptCsvCreateTaskEntity::getId);
        List<ScriptCsvCreateTaskEntity> tasks = scriptCsvCreateTaskMapper.selectList(taskWrapper);
        Map<Long, List<ScriptCsvCreateTaskEntity>> taskMap = tasks.stream().collect(Collectors.groupingBy(ScriptCsvCreateTaskEntity::getScriptCsvDataSetId));
        // 4. 获取模板列表
        return csvDataSetEntityList.stream().map(t -> {
            ScriptCsvCreateDetailResponse detailResponse = new ScriptCsvCreateDetailResponse();
            // 组装组件数据
            BeanUtils.copyProperties(t, detailResponse);
            detailResponse.setScriptCsvDataSetId(t.getId());
            // 转一下 map
            Map<String, String> scriptCsvVariableJsonPath = Maps.newHashMap();
            String scriptCsvVariableName = t.getScriptCsvVariableName();
            String[] variableNames = scriptCsvVariableName.split(",");
            for (String variableName : variableNames) {
                scriptCsvVariableJsonPath.put(variableName, "");
            }
            detailResponse.setScriptCsvVariableJsonPath(scriptCsvVariableJsonPath);

            List<ScriptCsvCreateTaskEntity> taskList = taskMap.get(t.getId());
            if (!CollectionUtils.isEmpty(taskList)) {
                // 组装 最近一次任务的
                ScriptCsvCreateTaskEntity scriptCsvCreateTaskEntity = taskList.get(0);
                BeanUtils.copyProperties(scriptCsvCreateTaskEntity, detailResponse);

                Map<String, String> taskVariableJsonPathMap = JSON.parseObject(scriptCsvCreateTaskEntity.getScriptCsvVariableJsonPath(), Map.class);
                scriptCsvVariableJsonPath.putAll(taskVariableJsonPathMap);
                detailResponse.setScriptCsvVariableJsonPath(scriptCsvVariableJsonPath);
                //组装 模板列表
                String templateContent = scriptCsvCreateTaskEntity.getTemplateContent();
                ScriptCsvDataTemplateResponse templateResponse = JSON.parseObject(templateContent, ScriptCsvDataTemplateResponse.class);
                detailResponse.setTemplate(templateResponse);
            }
            // 重组
            return detailResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public ScriptCsvDataTemplateResponse getCsvTemplate(ScriptCsvDataTemplateRequest request) {

        // 1. 获取流量
        List<TrafficRecorderExtResponse> trafficRecorderExtResponses = this.getTemplateData(request);
        // 2.获取流量数量
        Long count = this.getTemplateDataCount(request);

        // 3. 转化模板数据
        ScriptCsvDataTemplateResponse response = new ScriptCsvDataTemplateResponse();
        ScriptCsvDataTemplateResponse.TemplateDTO restfulUrl = CsvTemplateUtils.parseRestfulUrl(request);
        ScriptCsvDataTemplateResponse.TemplateDTO requestBody = CsvTemplateUtils.parseRequestBody(trafficRecorderExtResponses);
        ScriptCsvDataTemplateResponse.TemplateDTO header = CsvTemplateUtils.parseHeader(trafficRecorderExtResponses);
        // 4. 组装数接口
        List<ScriptCsvDataTemplateResponse.TemplateDTO> templateDTO = Lists.newArrayList();
        templateDTO.add(header);
        templateDTO.add(requestBody);
        templateDTO.add(restfulUrl);
        response.setTemplateDTO(templateDTO);

        response.setTotal(count);
        response.setAppName(request.getAppName());
        response.setServiceName(request.getServiceName());
        response.setMethodName(request.getMethodName());
        response.setStartTime(request.getStartTime());
        response.setEndTime(request.getEndTime());
        response.setCount(request.getCount());
        return response;
    }


    @Override
    public void createTask(List<ScriptCsvCreateTaskRequest> requests) {
        List<ScriptCsvCreateTaskEntity> taskEntityList = Lists.newArrayList();
        for (ScriptCsvCreateTaskRequest request : requests) {
            // 1. 创建任务
            ScriptCsvCreateTaskEntity scriptCsvCreateTask = new ScriptCsvCreateTaskEntity();
            // 校验 是否所以映射关系是否都配置了
            Map<String, String> scriptCsvVariableJsonPath = request.getScriptCsvVariableJsonPath();
            if (scriptCsvVariableJsonPath == null) {
                throw new RuntimeException("映射关系未空，请配置~");
            }
            ScriptCsvDataSetEntity scriptCsvDataSetEntity = scriptCsvDataSetMapper.selectById(request.getScriptCsvDataSetId());
            if (scriptCsvDataSetEntity == null) {
                throw new RuntimeException("组件已修改，请重新进入生成页面~");
            }
            String scriptCsvVariableName = scriptCsvDataSetEntity.getScriptCsvVariableName();
            String[] variableNames = scriptCsvVariableName.split(",");

            for (String variableName : variableNames) {
                if (request.getScriptCsvVariableJsonPath().get(variableName) == null || StringUtils.isBlank(request.getScriptCsvVariableJsonPath().get(variableName))) {
                    throw new RuntimeException("变量" + variableName + "未配置模板字段~");
                }
            }


            scriptCsvCreateTask.setTemplateContent(JSON.toJSONString(request.getTemplate()));
            scriptCsvCreateTask.setScriptCsvVariableJsonPath(JSON.toJSONString(request.getScriptCsvVariableJsonPath()));

            ScriptCsvDataTemplateResponse template = request.getTemplate();

            CurrentCreateScheduleDTO currentCreateScheduleDTO = new CurrentCreateScheduleDTO();
            currentCreateScheduleDTO.setCount(template.getCount() != null ? template.getCount() : template.getTotal());
            currentCreateScheduleDTO.setTotal(template.getTotal());
            scriptCsvCreateTask.setCurrentCreateSchedule(JSON.toJSONString(currentCreateScheduleDTO));

            scriptCsvCreateTask.setCreateStatus(ScriptCsvCreateTaskState.BE_QUEUING);
            scriptCsvCreateTask.setRemark("");
            scriptCsvCreateTask.setAliasName(request.getAliasName());
            scriptCsvCreateTask.setDeptId(WebPluginUtils.traceDeptId());
            scriptCsvCreateTask.setBusinessFlowId(request.getBusinessFlowId());
            scriptCsvCreateTask.setLinkId(request.getLinkId());
            scriptCsvCreateTask.setScriptCsvDataSetId(request.getScriptCsvDataSetId());
            scriptCsvCreateTask.setCreateTime(LocalDateTime.now());
            scriptCsvCreateTask.setUpdateTime(LocalDateTime.now());
            scriptCsvCreateTask.setTenantId(WebPluginUtils.traceTenantId());
            scriptCsvCreateTask.setEnvCode(WebPluginUtils.traceEnvCode());
            taskEntityList.add(scriptCsvCreateTask);
        }
        int insert = scriptCsvCreateTaskMapper.batchInsert(taskEntityList);
        if (insert <= 0) {
            throw new RuntimeException("csv任务创建失败");
        }

    }


    @Override
    public void cancelTask(ScriptCsvCreateTaskRequest request) {
        ScriptCsvCreateTaskEntity entity = new ScriptCsvCreateTaskEntity();
        entity.setId(request.getTaskId());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setCreateStatus(3);
        entity.setRemark("页面取消");
        int insert = scriptCsvCreateTaskMapper.updateById(entity);
        if (insert <= 0) {
            throw new RuntimeException("csv任务取消失败");
        }
    }

    @Override
    public ScriptCsvDataSetResponse listFileCsvById(Long businessFlowId, Long scriptCsvDataSetId) {
        ScriptCsvDataSetResponse scriptCsvDataSetResponse = new ScriptCsvDataSetResponse();
        // 1. 获取组件数据
        ScriptCsvDataSetEntity setEntity = scriptCsvDataSetMapper.selectById(scriptCsvDataSetId);
        if (setEntity == null) {
            throw new RuntimeException("未找到该组件");
        }
        BeanUtils.copyProperties(setEntity, scriptCsvDataSetResponse);
        List<FileManageResponse> responseList = Lists.newArrayList();
        // 1. 获取 任务列表
        LambdaQueryWrapper<ScriptCsvCreateTaskEntity> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(ScriptCsvCreateTaskEntity::getBusinessFlowId, businessFlowId);
        taskWrapper.eq(ScriptCsvCreateTaskEntity::getScriptCsvDataSetId, scriptCsvDataSetId);
        taskWrapper.in(ScriptCsvCreateTaskEntity::getCreateStatus, Arrays.asList(ScriptCsvCreateTaskState.IN_FORMATION, ScriptCsvCreateTaskState.BE_QUEUING));
        List<ScriptCsvCreateTaskEntity> taskEntityList = scriptCsvCreateTaskMapper.selectList(taskWrapper);
        responseList.addAll(taskEntityList.stream().map(t -> {
            FileManageResponse response = new FileManageResponse();
            response.setCreateType(1);
            response.setTaskId(t.getId());
            response.setAliasName(t.getAliasName());
            response.setTaskStatus(t.getCreateStatus());
            response.setFileName(setEntity.getScriptCsvFileName());
            CurrentCreateScheduleDTO currentCreateScheduleDTO = JSON.parseObject(t.getCurrentCreateSchedule(), CurrentCreateScheduleDTO.class);
            response.setCurrentCreateSchedule(currentCreateScheduleDTO.parseCurrentCreateSchedule(t.getCreateStatus()));
            //response.setFileSize(currentCreateScheduleMap.get("count") != null ?currentCreateScheduleMap.get("count").toString(): "0");
            response.setFileType(1);
            response.setDataCount(currentCreateScheduleDTO.getCount());
            return response;
        }).collect(Collectors.toList()));

        LambdaQueryWrapper<FileManageEntity> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(FileManageEntity::getScriptCsvDataSetId, scriptCsvDataSetId);
        fileWrapper.eq(FileManageEntity::getIsDeleted, 0);
        fileWrapper.orderByDesc(FileManageEntity::getUploadTime);
        List<FileManageEntity> fileManageEntityList = fileManageMapper.selectList(fileWrapper);
        responseList.addAll(fileManageEntityList.stream().map(t -> {
            FileManageResponse response = new FileManageResponse();
            BeanUtils.copyProperties(t, response);
            response.setFileManageId(t.getId());
            // 生成类型 自动生成 100%
            if (response.getCreateType().equals(1)) {
                response.setTaskStatus(2);
                response.setCurrentCreateSchedule("100%");
            }
            if (StringUtils.isNotEmpty(t.getFileExtend())) {
                Map<String, Object> stringObjectMap = JsonHelper.json2Map(t.getFileExtend(), String.class, Object.class);
                if (stringObjectMap != null && stringObjectMap.get("dataCount") != null && !StringUtil.isBlank(stringObjectMap.get("dataCount").toString())) {
                    response.setDataCount(Long.valueOf(stringObjectMap.get("dataCount").toString()));
                }
                if (stringObjectMap != null && stringObjectMap.get("isSplit") != null && !StringUtil.isBlank(stringObjectMap.get("isSplit").toString())) {
                    response.setIsSplit(Integer.valueOf(stringObjectMap.get("isSplit").toString()));
                }
                if (stringObjectMap != null && stringObjectMap.get("isOrderSplit") != null && !StringUtil.isBlank(
                        stringObjectMap.get("isOrderSplit").toString())) {
                    response.setIsOrderSplit(Integer.valueOf(stringObjectMap.get("isOrderSplit").toString()));
                }
                response.setIsBigFile(0);
                if (stringObjectMap != null && stringObjectMap.get("isBigFile") != null && !StringUtil.isBlank(stringObjectMap.get("isBigFile").toString())) {
                    response.setIsBigFile(Integer.valueOf(stringObjectMap.get("isBigFile").toString()));
                }
            }
            return response;
        }).collect(Collectors.toList()));
        scriptCsvDataSetResponse.setFiles(responseList);

        return scriptCsvDataSetResponse;
    }


    @Override
    public void spiltOrIsOrderSplit(BusinessFlowDataFileV2Request request) {
        // 组件id
        ScriptCsvDataSetEntity scriptCsvDataSetEntity = scriptCsvDataSetMapper.selectById(request.getScriptCsvDataSetId());
        if (scriptCsvDataSetEntity == null) {
            throw new RuntimeException("未找到该组件");
        }
        // 更新组件的拆分状态
        if (request.getIsSplit() != null) {
            scriptCsvDataSetEntity.setIsSplit(request.getIsSplit() == 1);
        }
        if (request.getIsOrderSplit() != null) {
            scriptCsvDataSetEntity.setIsOrderSplit(request.getIsOrderSplit() == 1);
        }
        scriptCsvDataSetEntity.setUpdateTime(LocalDateTime.now());
        scriptCsvDataSetMapper.updateById(scriptCsvDataSetEntity);

        // 更新css组件所有文件的拆分状态
        LambdaQueryWrapper<FileManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileManageEntity::getScriptCsvDataSetId, request.getScriptCsvDataSetId());
        List<FileManageEntity> fileManageEntityList = fileManageMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(fileManageEntityList)) {
            log.info("本次更新，无文件需要更新");
            return;
        }
        // 找出当前绑定的场景
        FileManageEntity currentFileManage = null;
        for (FileManageEntity fileManageEntity : fileManageEntityList) {
            // 更新文件
            Map<String, Object> fileExtend = Maps.newHashMap();
            if (StringUtils.isNotBlank(fileManageEntity.getFileExtend())) {
                fileExtend.putAll(JSON.parseObject(fileManageEntity.getFileExtend(), Map.class));
            }
            if (request.getIsSplit() != null) {
                fileExtend.put("isSplit", request.getIsSplit());
            }

            if (request.getIsOrderSplit() != null) {
                fileExtend.put("isOrderSplit", request.getIsOrderSplit());
            }
            fileManageEntity.setFileExtend(JSON.toJSONString(fileExtend));
            fileManageEntity.setGmtUpdate(LocalDateTime.now());
            if (scriptCsvDataSetEntity.getFileManageId() != null && fileManageEntity.getId().equals(scriptCsvDataSetEntity.getFileManageId())) {
                currentFileManage = fileManageEntity;
            }
        }

        int i = fileManageMapper.updateBatch(fileManageEntityList);
        if (i <= 0) {
            throw new RuntimeException("文件更新失败");
        }
        if (currentFileManage == null) {
            return;
        }
        // 更新场景
        // 获取所有脚本，用于更新场景的对象
        this.updateSceneManage(scriptCsvDataSetEntity.getScriptDeployId());
    }


    @Override
    public void deleteFile(Long fileManageId) {
        FileManageEntity fileManageEntity = fileManageMapper.selectById(fileManageId);
        if (fileManageEntity == null) {
            throw new RuntimeException("未找到该文件");
        }
        // 1. 是否跟组件关联
        if (fileManageEntity.getScriptCsvDataSetId() != null) {
            ScriptCsvDataSetEntity setEntity = scriptCsvDataSetMapper.selectById(fileManageEntity.getScriptCsvDataSetId());
            if (fileManageEntity.getId().equals(setEntity.getFileManageId())) {
                throw new RuntimeException("该文件与css组件已绑定，无法删除！");
            }
        }
        // 2. 删除文件 ---文件表  t_file_manage
        int i = fileManageMapper.deleteById(fileManageEntity.getId());
        if (i <= 0) {
            throw new RuntimeException("文件删除失败");
        }
        // 3. 找到文件关联关系 --- t_script_file_ref
        ScriptFileRefResult scriptFileRefResult = scriptFileRefDAO.selectByFileManageId(fileManageEntity.getId());
        if (scriptFileRefResult != null) {
            // 4. 删除文件关联关系
            scriptFileRefDAO.deleteByIds(Collections.singletonList(scriptFileRefResult.getId()));
            log.info("删除文件，调用同步压测场景接口。");
            // 5. 删除场景的关联
            this.updateSceneManage(scriptFileRefResult.getScriptDeployId());
        }

        // 6.删除文件
        if (StringUtils.isNotBlank(fileManageEntity.getUploadPath())) {
            FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
            fileDeleteParamReq.setPaths(Collections.singletonList(fileManageEntity.getUploadPath()));
            fileApi.deleteFile(fileDeleteParamReq);
            //LinuxUtil.executeLinuxCmd("rm -rf " + fileManageEntity.getUploadPath());
        }

    }

    @Override
    public void selectCsv(Long scriptCsvDataSetId, Long fileManageId) {
        // 1. 选择
        ScriptCsvDataSetEntity scriptCsvDataSetEntity = scriptCsvDataSetMapper.selectById(scriptCsvDataSetId);
        if (scriptCsvDataSetEntity == null) {
            throw new RuntimeException("未找到该组件");
        }
        // 检查下该文件是不是属于该组件的
        FileManageEntity fileManageEntity = fileManageMapper.selectById(fileManageId);
        if (fileManageEntity == null) {
            throw new RuntimeException("未找到该文件");
        }
        if (fileManageEntity.getScriptCsvDataSetId() != null && !fileManageEntity.getScriptCsvDataSetId().equals(scriptCsvDataSetId)) {
            throw new RuntimeException("该文件与组件不匹配");
        }
        Long oldFileManageId = scriptCsvDataSetEntity.getFileManageId();
        Long scriptDeployId = scriptCsvDataSetEntity.getScriptDeployId();
        scriptCsvDataSetEntity.setFileManageId(fileManageId);
        int i = scriptCsvDataSetMapper.updateById(scriptCsvDataSetEntity);
        if (i <= 0) {
            throw new RuntimeException("组件文件关联更新失败");
        }
        // 需要更新场景 存在未绑定的
        if (oldFileManageId != null) {
            ScriptFileRefResult scriptFileRefResult = scriptFileRefDAO.selectByFileManageId(oldFileManageId);
            // 删除之前的关联关系
            scriptFileRefDAO.deleteByIds(Collections.singletonList(scriptFileRefResult.getId()));
        }
        // 新增新的关系
        scriptFileRefDAO.createScriptFileRefs(Collections.singletonList(fileManageId), scriptDeployId);
        // 更新场景
        this.updateSceneManage(scriptDeployId);
    }


    @Override
    public PagingList<ScriptCsvManageResponse> csvManage(PageScriptCssManageQueryRequest request) {

        List<SceneResult> sceneResults = this.parseScene(request.getBusinessFlowName());
        if (sceneResults != null && sceneResults.isEmpty()) {
            return PagingList.empty();
        }
        List<Long> scriptCsvDataSetIds = this.parseScriptCsvDataSetId(request.getScriptCsvDataSetName());
        if (scriptCsvDataSetIds != null && scriptCsvDataSetIds.isEmpty()) {
            return PagingList.empty();
        }

        if (request.getType() == 1) {
            return this.taskManage(request, sceneResults, scriptCsvDataSetIds);
        }
        // 0:查csv列表
        // 查关联的脚步文件id
        List<Long> fileIds = this.parseFileId(sceneResults);
        LambdaQueryWrapper<FileManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(!CollectionUtils.isEmpty(fileIds), FileManageEntity::getId, fileIds);
        wrapper.eq(FileManageEntity::getFileType, FileTypeEnum.DATA.getCode());
        wrapper.like(StringUtils.isNotBlank(request.getScriptCsvFileName()), FileManageEntity::getFileName, request.getScriptCsvFileName());
        wrapper.in(!CollectionUtils.isEmpty(scriptCsvDataSetIds), FileManageEntity::getScriptCsvDataSetId, scriptCsvDataSetIds);
        wrapper.orderByDesc(FileManageEntity::getUploadTime);

        Page<FileManageEntity> entityPage = fileManageMapper.selectPage(new Page<>(request.getCurrent() + 1, request.getPageSize()), wrapper);

        List<FileManageEntity> records = entityPage.getRecords();
        if (entityPage.getTotal() == 0) {
            return PagingList.empty();
        }

        // 需要查询 关联业务流程 ---> 通过文件id 找到关联的脚本，通过脚本找到场景
        List<Long> finalFileIds = records.stream().map(FileManageEntity::getId).distinct().collect(Collectors.toList());
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectByFileManageIds(finalFileIds);
        List<Long> scriptDeployIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getScriptDeployId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<SceneEntity> sceneWrapper = new LambdaQueryWrapper();
        sceneWrapper.select(SceneEntity::getId, SceneEntity::getScriptDeployId, SceneEntity::getSceneName);
        sceneWrapper.in(SceneEntity::getScriptDeployId, scriptDeployIds);
        List<SceneEntity> sceneEntities = sceneMapper.selectList(sceneWrapper);

        // 脚本id对应的业务活动名称是什么 老数据兼容
        Map<Long, SceneEntity> idSceneNameMap = sceneEntities.stream().collect(Collectors.toMap(SceneEntity::getId, t -> t, (t1, t2) -> t2));
        Map<Long, Long> fileIdScriptIdMap = scriptFileRefResults.stream().collect(Collectors.toMap(ScriptFileRefResult::getFileId, ScriptFileRefResult::getScriptDeployId));

        // 新版本 从组件里获取
        Map<Long, SceneEntity> scriptDeployIdSceneNameMap = sceneEntities.stream().collect(Collectors.toMap(SceneEntity::getScriptDeployId, t -> t, (t1, t2) -> t2));


        // 根据文件Id 查询文件组件信息
        List<Long> finalScriptCsvDataSetId = records.stream().map(FileManageEntity::getScriptCsvDataSetId).distinct().collect(Collectors.toList());
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = scriptCsvDataSetMapper.selectBatchIds(finalScriptCsvDataSetId);
        Map<Long, ScriptCsvDataSetEntity> scriptCsvDataSetIdEntityMap = csvDataSetEntityList.stream().collect(Collectors.toMap(ScriptCsvDataSetEntity::getId, t -> t));

        return PagingList.of(records.stream().map(t -> {
            ScriptCsvManageResponse response = new ScriptCsvManageResponse();
            BeanUtils.copyProperties(t, response);
            response.setFileManageId(t.getId());

            response.setFileCreateTime(t.getGmtCreate());

            Long scriptDeployId = fileIdScriptIdMap.get(t.getId());
            if (scriptDeployId != null) {
                SceneEntity sceneEntity = scriptDeployIdSceneNameMap.get(scriptDeployId);
                response.setBusinessFlowId(sceneEntity != null ? sceneEntity.getId() : null);
                response.setBusinessFlowName(sceneEntity != null ? sceneEntity.getSceneName() : null);
            }
            response.setIsSelect(Boolean.FALSE);
            response.setScriptCsvFileName(t.getFileName());
            if (t.getScriptCsvDataSetId() != null) {
                ScriptCsvDataSetEntity setEntity = scriptCsvDataSetIdEntityMap.get(t.getScriptCsvDataSetId());
                if (setEntity != null) {
                    SceneEntity sceneEntity = idSceneNameMap.get(setEntity.getBusinessFlowId());
                    response.setBusinessFlowId(setEntity.getBusinessFlowId());
                    response.setBusinessFlowName(sceneEntity != null ? sceneEntity.getSceneName() : "");
                    response.setScriptCsvDataSetName(setEntity.getScriptCsvDataSetName());
                    response.setScriptCsvVariableName(setEntity.getScriptCsvVariableName());
                    // 是否选择判断
                    response.setIsSelect(t.getId().equals(setEntity.getFileManageId()));
                }
            } else {
                // 没有组件id 默认就是选择的 也就是老版本
                response.setIsSelect(true);
            }
            response.setDeptId(t.getDeptId());
            WebPluginUtils.fillQueryResponse(response);
            return response;
        }).collect(Collectors.toList()), entityPage.getTotal());


    }

    private List<Long> parseFileId(List<SceneResult> sceneResults) {
        if (CollectionUtils.isEmpty(sceneResults)) {
            return Lists.newArrayList();
        }
        List<Long> scriptDeployIds = sceneResults.stream().map(SceneResult::getScriptDeployId).collect(Collectors.toList());
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployIds(scriptDeployIds);
        return scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(Collectors.toList());
    }

    private PagingList<ScriptCsvManageResponse> taskManage(PageScriptCssManageQueryRequest request, List<SceneResult> sceneResults, List<Long> scriptCsvDataSetIds) {
        // 1:查任务列表
        // 1. 根据 businessFlowName
        List<Long> scriptCsvDataSetIdsByFile = this.parseScriptCsvFileName(request.getScriptCsvFileName());
        if (scriptCsvDataSetIdsByFile != null && scriptCsvDataSetIdsByFile.isEmpty()) {
            return PagingList.empty();
        }
        // 2. 两个 scriptCsvDataSetIds  scriptCsvDataSetIdsByFile
        List<Long> finalScriptCsvDataSetId = Lists.newArrayList();
        if (scriptCsvDataSetIds != null && scriptCsvDataSetIdsByFile != null) {
            finalScriptCsvDataSetId.addAll(scriptCsvDataSetIds.stream().filter(scriptCsvDataSetIdsByFile::contains).collect(Collectors.toList()));
        }
        // 3. 场景id
        LambdaQueryWrapper<ScriptCsvCreateTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(!CollectionUtils.isEmpty(finalScriptCsvDataSetId), ScriptCsvCreateTaskEntity::getScriptCsvDataSetId, finalScriptCsvDataSetId);
        if (sceneResults != null) {
            wrapper.in(ScriptCsvCreateTaskEntity::getBusinessFlowId, sceneResults.stream().map(SceneResult::getId).collect(Collectors.toList()));
        }
        wrapper.eq(request.getTaskState() != null, ScriptCsvCreateTaskEntity::getCreateStatus, request.getTaskState());
        wrapper.orderByDesc(ScriptCsvCreateTaskEntity::getUpdateTime);

        Page<ScriptCsvCreateTaskEntity> entityPage = scriptCsvCreateTaskMapper.selectPage(new Page<>(request.getCurrent() + 1, request.getPageSize()), wrapper);
        List<ScriptCsvCreateTaskEntity> records = entityPage.getRecords();
        if (entityPage.getTotal() == 0) {
            return PagingList.empty();
        }
        // 获取场景信息
        List<Long> sceneIds = records.stream().map(ScriptCsvCreateTaskEntity::getBusinessFlowId).distinct().collect(Collectors.toList());
        List<SceneEntity> sceneEntities = sceneMapper.selectBatchIds(sceneIds);
        Map<Long, SceneEntity> sceneIdEntityMap = sceneEntities.stream().collect(Collectors.toMap(SceneEntity::getId, t -> t));

        // 获取组件信息
        List<Long> scriptCsvDataSetId = records.stream().map(ScriptCsvCreateTaskEntity::getScriptCsvDataSetId).distinct().collect(Collectors.toList());
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = scriptCsvDataSetMapper.selectBatchIds(scriptCsvDataSetId);
        Map<Long, ScriptCsvDataSetEntity> scriptCsvDataSetIdEntityMap = csvDataSetEntityList.stream().collect(Collectors.toMap(ScriptCsvDataSetEntity::getId, t -> t));

        return PagingList.of(records.stream().map(t -> {
            ScriptCsvManageResponse response = new ScriptCsvManageResponse();
            ScriptCsvDataSetEntity scriptCsvDataSetEntity = scriptCsvDataSetIdEntityMap.get(t.getScriptCsvDataSetId());
            if (scriptCsvDataSetEntity != null) {
                BeanUtils.copyProperties(scriptCsvDataSetEntity, response);
            }
            SceneEntity sceneEntity = sceneIdEntityMap.get(t.getBusinessFlowId());
            response.setBusinessFlowId(sceneEntity != null ? sceneEntity.getId() : null);
            response.setBusinessFlowName(sceneEntity != null ? sceneEntity.getSceneName() : null);
            // 任务相关
            response.setAliasName(t.getAliasName());
            response.setId(t.getId());
            response.setTaskId(t.getId());
            response.setCreateType(1);
            response.setCreateStatus(t.getCreateStatus());
            CurrentCreateScheduleDTO currentCreateScheduleDTO = JSON.parseObject(t.getCurrentCreateSchedule(), CurrentCreateScheduleDTO.class);
            response.setCurrentCreateSchedule(currentCreateScheduleDTO.parseCurrentCreateSchedule(t.getCreateStatus()));
            response.setIsSelect(Boolean.FALSE);
            response.setCurrentCreateScheduleString(t.getCurrentCreateSchedule());
            response.setCreateTime(t.getCreateTime());
            response.setDeptId(t.getDeptId());
            WebPluginUtils.fillQueryResponse(response);

            return response;
        }).collect(Collectors.toList()), entityPage.getTotal());
    }

    private List<Long> parseScriptCsvDataSetId(String scriptCsvDataSetName) {
        if (StringUtils.isEmpty(scriptCsvDataSetName)) {
            return null;
        }
        LambdaQueryWrapper<ScriptCsvDataSetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ScriptCsvDataSetEntity::getId);
        wrapper.like(ScriptCsvDataSetEntity::getScriptCsvDataSetName, scriptCsvDataSetName);
        return scriptCsvDataSetMapper.selectList(wrapper).stream().map(ScriptCsvDataSetEntity::getId).collect(Collectors.toList());
    }

    private List<Long> parseScriptCsvFileName(String scriptCsvFileName) {
        if (StringUtils.isEmpty(scriptCsvFileName)) {
            return null;
        }
        LambdaQueryWrapper<ScriptCsvDataSetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ScriptCsvDataSetEntity::getId);
        wrapper.like(ScriptCsvDataSetEntity::getScriptCsvFileName, scriptCsvFileName);
        return scriptCsvDataSetMapper.selectList(wrapper).stream().map(ScriptCsvDataSetEntity::getId).collect(Collectors.toList());
    }

    private List<SceneResult> parseScene(String businessFlowName) {
        if (StringUtils.isEmpty(businessFlowName)) {
            return null;
        }
        SceneQueryParam queryParam = new SceneQueryParam();
        WebPluginUtils.fillQueryParam(queryParam);
        queryParam.setSceneName(businessFlowName);
        return sceneDAO.selectList(queryParam);
    }


    @Override
    public List<UploadResponse> upload(Long sceneId, Long scriptCsvDataSetId, List<MultipartFile> file) {
        if (CollectionUtil.isEmpty(file)) {
            throw new RuntimeException("上传文件不能为空");
        }
        List<ScriptCsvDataSetEntity> setEntity = Lists.newArrayList();

        if (scriptCsvDataSetId != null) {
            setEntity.add(scriptCsvDataSetMapper.selectById(scriptCsvDataSetId));
        } else {
            setEntity.addAll(scriptCsvDataSetMapper.listByBusinessFlowId(sceneId));
        }

        Map<String, Long> fileNameIdMap = setEntity.stream().collect(Collectors.toMap(ScriptCsvDataSetEntity::getScriptCsvFileName, ScriptCsvDataSetEntity::getId));
        Map<Long, String> idFileNameMap = setEntity.stream().collect(Collectors.toMap(ScriptCsvDataSetEntity::getId, ScriptCsvDataSetEntity::getScriptCsvFileName));

        for (MultipartFile multipartFile : file) {
            if (null == multipartFile || multipartFile.isEmpty()) {
                throw new RuntimeException("上传文件不能为空");
            }
            String name = multipartFile.getOriginalFilename();
            if (scriptCsvDataSetId != null) {
                // 只能上传csv
                String fileName = idFileNameMap.get(scriptCsvDataSetId);
                if (!name.contains(".csv") || !name.equals(fileName)) {
                    throw new RuntimeException("组件中上传文件仅允许.csv格式且文件名为" + fileName + "的数据");
                }
            } else {
                if (name.endsWith(".csv") && !fileNameIdMap.containsKey(name)) {
                    throw new RuntimeException("组件中不允许上传组件外的数据文件");
                }
            }
        }

        List<UploadResponse> upload = cloudFileApi.upload(new UploadRequest() {{
            setFileList(file);
        }});


        for (UploadResponse uploadResponse : upload) {
            if (FileTypeEnum.ATTACHMENT.getCode().equals(uploadResponse.getFileType())) {
                continue;
            }
            if (scriptCsvDataSetId != null) {
                uploadResponse.setScriptCsvDataSetId(scriptCsvDataSetId);
            } else {
                // 进行适配，如果有组件
                uploadResponse.setScriptCsvDataSetId(fileNameIdMap.get(uploadResponse.getFileName()));
            }

        }
        return upload;
    }


    @Override
    public BusinessFlowDetailResponse uploadDataFile(BusinessFlowDataFileRequest request) {
        BusinessFlowDetailResponse result = new BusinessFlowDetailResponse();
        result.setId(request.getId());
        if (CollectionUtils.isEmpty(request.getFileManageUpdateRequests())) {
            return result;
        }
        // 检验下
        for (FileManageUpdateRequest updateRequest : request.getFileManageUpdateRequests()) {
            if (FileTypeEnum.DATA.getCode().equals(updateRequest.getFileType()) && updateRequest.getScriptCsvDataSetId() == null) {
                throw new RuntimeException("数据文件，必须要有对应的组件id");
            }
        }
        // 1. 判断业务流程是否存在
        SceneResult sceneResult = sceneDao.getSceneDetail(request.getId());
        if (sceneResult == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, "没有找到对应的业务流程！");
        }
        Long scriptDeployId = sceneResult.getScriptDeployId();
        // 2.保存文件 到 t_file_manage
        ScriptManageDeployUpdateRequest updateRequest = new ScriptManageDeployUpdateRequest();
        updateRequest.setId(scriptDeployId);


        // 更新拆分状态
        List<Long> scriptCsvDataSetIds = request.getFileManageUpdateRequests().stream().map(FileManageUpdateRequest::getScriptCsvDataSetId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(scriptCsvDataSetIds)) {
            csvDataSetEntityList.addAll(scriptCsvDataSetMapper.selectBatchIds(scriptCsvDataSetIds));
        }
        Map<Long, ScriptCsvDataSetEntity> scriptCsvDataSetIdMap = csvDataSetEntityList.stream().collect(Collectors.toMap(ScriptCsvDataSetEntity::getId, t -> t));

        request.getFileManageUpdateRequests().forEach(t -> {
            // 提前定义好UUID 用于落盘
            t.setUuId(UUID.randomUUID().toString());
            if (FileTypeEnum.DATA.getCode().equals(t.getFileType())) {
                ScriptCsvDataSetEntity scriptCsvDataSetEntity = scriptCsvDataSetIdMap.get(t.getScriptCsvDataSetId());
                if (scriptCsvDataSetEntity != null) {
                    t.setIsSplit(Boolean.TRUE.equals(scriptCsvDataSetEntity.getIsSplit()) ? 1 : 0);
                    t.setIsOrderSplit(Boolean.TRUE.equals(scriptCsvDataSetEntity.getIsOrderSplit()) ? 1 : 0);
                }
                if (t.getDeptId() == null) {
                    t.setDeptId(WebPluginUtils.traceDeptId());
                }
            }
        });

        updateRequest.setFileManageUpdateRequests(request.getFileManageUpdateRequests());
        List<FileManageEntity> fileManageEntityList = scriptManageService.updateScriptCssManage(updateRequest);
        Map<Long, List<FileManageEntity>> scriptCsvDataSetIdFileMap = fileManageEntityList.stream().collect(Collectors.groupingBy(FileManageEntity::getScriptCsvDataSetId));


        // 3. 默认选择上传的文件
        if (!CollectionUtils.isEmpty(fileManageEntityList)) {
            // 旧的文件id
            List<Long> oleFileIds = csvDataSetEntityList.stream().map(ScriptCsvDataSetEntity::getFileManageId).filter(Objects::nonNull)
                    .distinct().collect(Collectors.toList());
            // 新增新的文件Id
            csvDataSetEntityList.forEach(t -> {
                List<FileManageEntity> files = scriptCsvDataSetIdFileMap.get(t.getId());
                if (!CollectionUtils.isEmpty(files)) {
                    t.setFileManageId(files.get(0).getId());
                }
            });

            int i = scriptCsvDataSetMapper.updateBatchSelective(csvDataSetEntityList);
            if (i <= 0) {
                throw new RuntimeException("组件文件关联更新失败");
            }
            // 删除之前的关联关系
            if (!CollectionUtils.isEmpty(oleFileIds)) {
                List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectByFileManageIds(oleFileIds);
                scriptFileRefDAO.deleteByIds(scriptFileRefResults.stream().map(ScriptFileRefResult::getId).collect(Collectors.toList()));
            }
            // 新增新的关系
            scriptFileRefDAO.createScriptFileRefs(fileManageEntityList.stream().map(FileManageEntity::getId).collect(Collectors.toList()), scriptDeployId);
        }

        // 更新场景
        this.updateSceneManage(scriptDeployId);

        return result;
    }

    @Override
    public void updateAliasName(ScriptCsvAliasNameUpdateRequest request) {
        // 1. 判断文件是否存在
        FileManageEntity fileManageEntity = fileManageMapper.selectById(request.getFileManageId());
        if (fileManageEntity == null) {
            throw new RuntimeException("文件不存在");
        }
        fileManageEntity.setAliasName(request.getAliasName());
        fileManageEntity.setGmtUpdate(LocalDateTime.now());
        fileManageMapper.updateById(fileManageEntity);
    }

    @Override
    public void deleteCsvAll(Long sceneId) {
        // 1.获取组件
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = scriptCsvDataSetMapper.listByBusinessFlowId(sceneId);
        if (CollectionUtils.isEmpty(csvDataSetEntityList)) {
            return;
        }

        // 2.获取任务
        List<Long> scriptCsvDataSetIds = csvDataSetEntityList.stream().map(ScriptCsvDataSetEntity::getId).collect(Collectors.toList());
        LambdaQueryWrapper<ScriptCsvCreateTaskEntity> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.in(ScriptCsvCreateTaskEntity::getScriptCsvDataSetId, scriptCsvDataSetIds);
        List<ScriptCsvCreateTaskEntity> scriptCsvCreateTaskEntities = scriptCsvCreateTaskMapper.selectList(taskWrapper);
        if (!CollectionUtils.isEmpty(scriptCsvCreateTaskEntities)) {
            scriptCsvCreateTaskMapper.deleteBatchIds(scriptCsvCreateTaskEntities.stream().map(ScriptCsvCreateTaskEntity::getId).collect(Collectors.toList()));
        }
        // 3. 删除组件
        scriptCsvDataSetMapper.deleteBatchIds(scriptCsvDataSetIds);
        // 4. 删除组件对应的文件
        LambdaQueryWrapper<FileManageEntity> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.in(FileManageEntity::getScriptCsvDataSetId, scriptCsvDataSetIds);
        List<FileManageEntity> fileManageEntityList = fileManageMapper.selectList(fileWrapper);
        if (CollectionUtils.isEmpty(fileManageEntityList)) {
            return;
        }
        List<Long> fileIds = fileManageEntityList.stream().map(FileManageEntity::getId).collect(Collectors.toList());
        fileManageMapper.deleteBatchIds(fileIds);

        // 删除文件位置
        List<String> filePaths = fileManageEntityList.stream().map(FileManageEntity::getUploadPath).collect(Collectors.toList());
        FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
        fileDeleteParamReq.setPaths(filePaths);
        fileApi.deleteFile(fileDeleteParamReq);

        // 删除关联关系
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectByFileManageIds(fileIds);
        if (!CollectionUtils.isEmpty(scriptFileRefResults)) {
            scriptFileRefDAO.deleteByIds(scriptFileRefResults.stream().map(ScriptFileRefResult::getId).collect(Collectors.toList()));
        }
    }

    @Override
    public void runJob() {
        // 1. 每次只执行一次 上锁
        Boolean running = RedisHelper.setIfAbsent(CSV_TASK_RUN_REDIS_KEY, 1, 1L, TimeUnit.MINUTES);
        if (running == null || Boolean.FALSE.equals(running)) {
            return;
        }
        try {
            // 任务上锁
            Object lock = RedisHelper.stringGet(CSV_TASK_REDIS_KEY);
            if (lock != null && StringUtils.isNotBlank(lock.toString())) {
                // 2. 判断是否任务状态是否取消
                ScriptCsvCreateTaskEntity scriptCsvCreateTaskEntity = scriptCsvCreateTaskMapper.selectById(Long.valueOf(lock.toString()));
                if (scriptCsvCreateTaskEntity == null) {
                    this.completeTask(null, null);
                    return;
                }
                // 租户补充
                TenantCommonExt commonExt = new TenantCommonExt();
                commonExt.setTenantId(scriptCsvCreateTaskEntity.getTenantId());
                commonExt.setEnvCode(scriptCsvCreateTaskEntity.getEnvCode());
                commonExt.setSource(ContextSourceEnum.JOB.getCode());
                WebPluginUtils.setTraceTenantContext(commonExt);
                CurrentCreateScheduleDTO currentCreateScheduleDTO = JSON.parseObject(scriptCsvCreateTaskEntity.getCurrentCreateSchedule(), CurrentCreateScheduleDTO.class);
                if (scriptCsvCreateTaskEntity.getCreateStatus().equals(ScriptCsvCreateTaskState.GENERATED)) {
                    RedisHelper.delete(CSV_TASK_REDIS_KEY);
                    return;
                }
                if (scriptCsvCreateTaskEntity.getCreateStatus().equals(ScriptCsvCreateTaskState.CANCELLED)) {
                    this.completeTask(scriptCsvCreateTaskEntity, null);
                    return;
                }
                // 3. 页码总数
                int totalPage = currentCreateScheduleDTO.getTotalPage();
                int page = currentCreateScheduleDTO.getPage();
                if (page > totalPage) {
                    this.completeTask(scriptCsvCreateTaskEntity, currentCreateScheduleDTO);
                    return;
                }
                // 4. 开始收集数据
                this.collectData(scriptCsvCreateTaskEntity);
                return;
            }
            // 5. 查找任务
            LambdaQueryWrapper<ScriptCsvCreateTaskEntity> taskWrapper = new LambdaQueryWrapper<>();
            taskWrapper.eq(ScriptCsvCreateTaskEntity::getCreateStatus, ScriptCsvCreateTaskState.BE_QUEUING);
            taskWrapper.orderByAsc(ScriptCsvCreateTaskEntity::getCreateTime);
            List<ScriptCsvCreateTaskEntity> scriptCsvCreateTaskEntities = scriptCsvCreateTaskMapper.selectList(taskWrapper);
            if (CollectionUtils.isEmpty(scriptCsvCreateTaskEntities)) {
                return;
            }
            // 6.开始任务，并锁定任务
            ScriptCsvCreateTaskEntity scriptCsvCreateTaskEntity = scriptCsvCreateTaskEntities.get(0);
            // 锁定
            Boolean ifAbsent = RedisHelper.setIfAbsent(CSV_TASK_REDIS_KEY, scriptCsvCreateTaskEntity.getId());
            if (Boolean.FALSE.equals(ifAbsent)) {
                // 锁定失败
                return;
            }

            scriptCsvCreateTaskEntity.setCreateStatus(ScriptCsvCreateTaskState.IN_FORMATION);
            // 填入临时目录路径
            ScriptCsvDataSetEntity scriptCsvDataSetEntity = scriptCsvDataSetMapper.selectById(scriptCsvCreateTaskEntity.getScriptCsvDataSetId());
            if (scriptCsvDataSetEntity == null) {
                return;
            }
            CurrentCreateScheduleDTO currentCreateScheduleDTO = JSON.parseObject(scriptCsvCreateTaskEntity.getCurrentCreateSchedule(), CurrentCreateScheduleDTO.class);
            currentCreateScheduleDTO.setUploadId(UUID.randomUUID().toString());
            currentCreateScheduleDTO.setTempPath(tempPath + SceneManageConstant.FILE_SPLIT +
                    currentCreateScheduleDTO.getUploadId() + SceneManageConstant.FILE_SPLIT + scriptCsvDataSetEntity.getScriptCsvFileName());
            currentCreateScheduleDTO.setCurrent(0L);
            currentCreateScheduleDTO.setPage(0);
            currentCreateScheduleDTO.setTotalPage((int) (currentCreateScheduleDTO.getTotal() / 10000 + 1));
            currentCreateScheduleDTO.setIgnoreFirstLine(scriptCsvDataSetEntity.getIgnoreFirstLine());
            currentCreateScheduleDTO.setScriptCsvVariableName(scriptCsvDataSetEntity.getScriptCsvVariableName());

            scriptCsvCreateTaskEntity.setCurrentCreateSchedule(JSON.toJSONString(currentCreateScheduleDTO));
            scriptCsvCreateTaskEntity.setUpdateTime(LocalDateTime.now());
            scriptCsvCreateTaskEntity.setRemark("正在进行中的任务");
            scriptCsvCreateTaskEntity.setTaskStartTime(LocalDateTime.now());

            int i = scriptCsvCreateTaskMapper.updateById(scriptCsvCreateTaskEntity);
            if (i <= 0) {
                log.error("任务状态更新失败");
                return;
            }

            // 异步收集
            // 3. 开始收集数据
            this.collectData(scriptCsvCreateTaskEntity);
        } catch (Exception e) {
            log.error("执行失败：", e);
        } finally {
            RedisHelper.delete(CSV_TASK_RUN_REDIS_KEY);
        }

    }

    private void collectData(ScriptCsvCreateTaskEntity scriptCsvCreateTaskEntity) {
        try {
            scriptCsvCreateTaskEntity.setUpdateTime(LocalDateTime.now());
            // 1.获取当前进度，并更新下一集的进度
            CurrentCreateScheduleDTO currentCreateScheduleDTO = JSON.parseObject(scriptCsvCreateTaskEntity.getCurrentCreateSchedule(), CurrentCreateScheduleDTO.class);
            // 判断进度是否完成

            long current = currentCreateScheduleDTO.getCurrent();
            long count = currentCreateScheduleDTO.getCount();

            if (count <= current) {
                // 进度完成
                this.completeTask(scriptCsvCreateTaskEntity, currentCreateScheduleDTO);
                return;
            }

            // 2.当当天进度数据获取完
            ScriptCsvDataTemplateResponse templateResponse = JSON.parseObject(scriptCsvCreateTaskEntity.getTemplateContent(), ScriptCsvDataTemplateResponse.class);
            ScriptCsvDataTemplateRequest request = new ScriptCsvDataTemplateRequest();
            request.setLinkId(scriptCsvCreateTaskEntity.getLinkId());
            request.setStartTime(templateResponse.getStartTime());
            request.setEndTime(templateResponse.getEndTime());
            request.setAppName(templateResponse.getAppName());
            request.setServiceName(templateResponse.getServiceName());
            request.setMethodName(templateResponse.getMethodName());
            request.setCount(templateResponse.getCount());
            List<TrafficRecorderExtResponse> trafficRecorderData = this.getTrafficRecorderData(request, currentCreateScheduleDTO.getPage(), currentCreateScheduleDTO.getSize());

            // 3.落盘
            Integer writeNum = this.writeFile(scriptCsvCreateTaskEntity, trafficRecorderData, templateResponse, currentCreateScheduleDTO);

            // 更新下一集进度
            currentCreateScheduleDTO.setPage(currentCreateScheduleDTO.getPage() + 1);
            currentCreateScheduleDTO.setCurrent(writeNum + current);
            currentCreateScheduleDTO.setIgnoreFirstLine(null);

            scriptCsvCreateTaskEntity.setCurrentCreateSchedule(JSON.toJSONString(currentCreateScheduleDTO));
            int i = scriptCsvCreateTaskMapper.updateById(scriptCsvCreateTaskEntity);
        } catch (Exception e) {
            log.error("收集csv失败：", e);
        }

    }

    private void completeTask(ScriptCsvCreateTaskEntity scriptCsvCreateTaskEntity, CurrentCreateScheduleDTO currentCreateScheduleDTO) {
        if (scriptCsvCreateTaskEntity != null) {
            scriptCsvCreateTaskEntity.setUpdateTime(LocalDateTime.now());
            ScriptCsvDataSetEntity scriptCsvDataSetEntity = scriptCsvDataSetMapper.selectById(scriptCsvCreateTaskEntity.getScriptCsvDataSetId());
            if (scriptCsvDataSetEntity == null) {
                // 如果组件变更，还在执行，把这个设置成备注 组件已变更
                RedisHelper.delete(CSV_TASK_REDIS_KEY);
                scriptCsvCreateTaskEntity.setCreateStatus(ScriptCsvCreateTaskState.CANCELLED);
                scriptCsvCreateTaskEntity.setRemark("组件已删除");
                scriptCsvCreateTaskMapper.updateById(scriptCsvCreateTaskEntity);
                return;
            }
            scriptCsvCreateTaskEntity.setRemark("文件生成已完成");
            scriptCsvCreateTaskEntity.setTaskEndTime(LocalDateTime.now());
            scriptCsvCreateTaskEntity.setCreateStatus(ScriptCsvCreateTaskState.GENERATED);
            scriptCsvCreateTaskMapper.updateById(scriptCsvCreateTaskEntity);

            // 组装文件 进行保存
            BusinessFlowDataFileRequest request = new BusinessFlowDataFileRequest();
            request.setId(scriptCsvCreateTaskEntity.getBusinessFlowId());
            FileManageUpdateRequest fileManageUpdateRequest = new FileManageUpdateRequest();
            fileManageUpdateRequest.setUploadId(currentCreateScheduleDTO.getUploadId());
            fileManageUpdateRequest.setFileName(scriptCsvDataSetEntity.getScriptCsvFileName());
            fileManageUpdateRequest.setFileType(1);
            fileManageUpdateRequest.setMd5(Md5Util.md5File(currentCreateScheduleDTO.getTempPath()));
            fileManageUpdateRequest.setDataCount(currentCreateScheduleDTO.getCurrent());
            fileManageUpdateRequest.setIsSplit(Boolean.TRUE.equals(scriptCsvDataSetEntity.getIsSplit()) ? 1 : 0);
            fileManageUpdateRequest.setIsOrderSplit(Boolean.TRUE.equals(scriptCsvDataSetEntity.getIsOrderSplit()) ? 1 : 0);
            fileManageUpdateRequest.setIsDeleted(0);
            fileManageUpdateRequest.setUploadTime(new Date());
            fileManageUpdateRequest.setIsBigFile(0);
            fileManageUpdateRequest.setCreateType(1);
            fileManageUpdateRequest.setDownloadUrl(currentCreateScheduleDTO.getTempPath());
            fileManageUpdateRequest.setScriptCsvDataSetId(scriptCsvDataSetEntity.getId());
            fileManageUpdateRequest.setAliasName(scriptCsvCreateTaskEntity.getAliasName());
            fileManageUpdateRequest.setDeptId(scriptCsvCreateTaskEntity.getDeptId());
            request.setFileManageUpdateRequests(Collections.singletonList(fileManageUpdateRequest));

            this.uploadDataFile(request);

        }
        RedisHelper.delete(CSV_TASK_REDIS_KEY);
    }

    private Integer writeFile(ScriptCsvCreateTaskEntity scriptCsvCreateTaskEntity, List<TrafficRecorderExtResponse> trafficRecorderData,
                              ScriptCsvDataTemplateResponse templateResponse, CurrentCreateScheduleDTO currentCreateScheduleDTO) {
        // 先建文件
        // 创建
        File file = null;
        try {
            file = new File(currentCreateScheduleDTO.getTempPath());
            if (!file.exists()) {
                FileUtils.touch(file);
            }
        } catch (IOException e) {
            log.error("创建文件失败");
        }
        if (CollectionUtils.isEmpty(trafficRecorderData)) {
            return 0;
        }
        // 是否忽略第一行
        Object ignoreFirstLine = currentCreateScheduleDTO.getIgnoreFirstLine();
        Long count = currentCreateScheduleDTO.getCount();
        Long current = currentCreateScheduleDTO.getCurrent();
        List<String> lines = Lists.newArrayList();
        String scriptCsvVariableName = currentCreateScheduleDTO.getScriptCsvVariableName();

        if (ignoreFirstLine != null && Boolean.parseBoolean(ignoreFirstLine.toString())) {
            lines.add(scriptCsvVariableName);
        }
        String scriptCsvVariableJsonPath = scriptCsvCreateTaskEntity.getScriptCsvVariableJsonPath();
        // 获取变量关系
        Map<String, String> scriptCsvVariableJsonPathMap = JSON.parseObject(scriptCsvVariableJsonPath, Map.class);
        String[] variables = scriptCsvVariableName.split(",");

        // 初始化ApiMatcher 这个因为每次只执行一次，所以就不会混杂了
        ApiMatcher.setConfigRestfulExpression(templateResponse.getServiceName());

        for (TrafficRecorderExtResponse response : trafficRecorderData) {
            List<String> result = Lists.newArrayList();
            for (String variable : variables) {
                String variableJsonPath = scriptCsvVariableJsonPathMap.get(variable);
                String[] keys = variableJsonPath.split("#");
                switch (keys[0]) {
                    case "header":
                        String header = CsvTemplateUtils.readJsonValue(response.getRequestHeader(), keys[1]).toString();
                        result.add(header);
//                        if (StringUtils.isNotBlank(header)) {
//
//                        }
                        break;
                    case "requestBody":
                        String requestBody = CsvTemplateUtils.readJsonValue(response.getRequestBody(), keys[1]).toString();
                        result.add(requestBody);
//                        if (StringUtils.isNotBlank(requestBody)) {
//                            result.add(requestBody);
//                        }
                        break;
                    case "url":
                        String restfulValue = CsvTemplateUtils.readRestfulValue(response.getServiceName(), keys[1]).toString();
                        result.add(restfulValue);
//                        if (StringUtils.isNotBlank(restfulValue)) {
//                            result.add(restfulValue);
//                        }
                        break;
                    default: {

                    }
                }
            }
            // 校验下，如果全部都是空，则丢弃
            if (result.stream().distinct().count() == 1 && StringUtils.isBlank(result.get(0))) {
                continue;
            }
            if (result.size() == variables.length) {
                lines.add(String.join(",", result));
            }

            if (lines.size() + current == count) {
                break;
            }
        }

        // 写入
        try {
            FileUtils.writeLines(file, lines, true);
        } catch (IOException e) {
            // 写入失败
            log.error("csv写入失败：", e);
            return 0;
        }

        return lines.size();

    }

    private void updateSceneManage(Long scriptDeployId) {

        CloudUpdateSceneFileRequest sceneFileRequest = new CloudUpdateSceneFileRequest();
        sceneFileRequest.setOldScriptId(scriptDeployId);
        sceneFileRequest.setNewScriptId(scriptDeployId);
        sceneFileRequest.setScriptType(0);

        // 用于更新场景的对象
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDAO.selectFileIdsByScriptDeployId(scriptDeployId);
        List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(Collectors.toList());
        List<FileManageEntity> files = fileManageMapper.selectBatchIds(fileIds);
        List<UploadFileDTO> uploadFiles = files.stream().map(file -> {
            UploadFileDTO uploadFileDTO = new UploadFileDTO();
            BeanUtils.copyProperties(file, uploadFileDTO);
            uploadFileDTO.setIsDeleted(0);
            uploadFileDTO.setUploadTime(DateUtil.format(file.getUploadTime(), AppConstants.DATE_FORMAT_STRING));
            return uploadFileDTO;
        }).collect(Collectors.toList());
        sceneFileRequest.setUploadFiles(uploadFiles);

        // cloud 更新
        ResponseResult<Object> response = sceneManageApi.updateSceneFileByScriptId(sceneFileRequest);
        if (!response.getSuccess()) {
            log.error("脚本更新 --> 对应的 cloud 场景, 脚本文件更新失败, 错误信息: {}", JSONUtil.toJsonStr(response));
            throw ScriptManageExceptionUtil.getUpdateValidError(
                    String.format("对应的 cloud 脚本更新失败, 错误信息: %s", response.getError().getSolution()));
        }
    }

    private String getIndex(ScriptCsvDataSetEntity scriptCsvDataSetEntity) {
        return scriptCsvDataSetEntity.getScriptCsvDataSetName() + "#" + scriptCsvDataSetEntity.getScriptCsvFileName() + "#" + scriptCsvDataSetEntity.getScriptCsvVariableName();
    }

    private List<ScriptCsvDataSetEntity> distinct(List<ScriptCsvDataSetEntity> setEntityList) {
        // 去重
        return setEntityList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(
                                () -> new TreeSet<>(
                                        Comparator.comparing(ScriptCsvDataSetEntity::getScriptCsvFileName))), ArrayList::new));
    }

    private List<TrafficRecorderExtResponse> getTemplateData(ScriptCsvDataTemplateRequest request) {
        TrafficRecorderExtApi trafficRecorderExtApi = pluginManager.getExtension(TrafficRecorderExtApi.class);
        if (trafficRecorderExtApi != null) {
            TrafficRecorderQueryExt queryExt = new TrafficRecorderQueryExt();
            BeanUtils.copyProperties(request, queryExt);
            // 这里对于restful格式的数据进行处理下
            if (ApiMatcher.isRestful(queryExt.getServiceName()) > 0) {
                //将 {} 全部替换成 % ，因为数据like 查询的时候，如果是{} 是不生效的
                queryExt.setServiceName(ApiMatcher.replaceAll(queryExt.getServiceName(), "%"));
            }
            return trafficRecorderExtApi.getTemplate(queryExt);
        }
        // 后续获取数据
        return Lists.newArrayList();
    }

    private List<TrafficRecorderExtResponse> getTrafficRecorderData(ScriptCsvDataTemplateRequest request, Integer page, Integer size) {
        try {
            TrafficRecorderExtApi trafficRecorderExtApi = pluginManager.getExtension(TrafficRecorderExtApi.class);
            if (trafficRecorderExtApi != null) {
                TrafficRecorderQueryExt queryExt = new TrafficRecorderQueryExt();
                BeanUtils.copyProperties(request, queryExt);
                queryExt.setCurrentPage(page);
                queryExt.setPageSize(size);
                // 这里对于restful格式的数据进行处理下
                if (ApiMatcher.isRestful(queryExt.getServiceName()) > 0) {
                    //将 {} 全部替换成 % ，因为数据like 查询的时候，如果是{} 是不生效的
                    queryExt.setServiceName(ApiMatcher.replaceAll(queryExt.getServiceName(), "%"));
                }
                return trafficRecorderExtApi.listTrafficRecorder(queryExt);
            }
        } catch (BeansException e) {
            // 后续获取数据
            return Lists.newArrayList();
        }
        // 后续获取数据
        return Lists.newArrayList();
    }


    private Long getTemplateDataCount(ScriptCsvDataTemplateRequest request) {
        TrafficRecorderExtApi trafficRecorderExtApi = pluginManager.getExtension(TrafficRecorderExtApi.class);
        if (trafficRecorderExtApi != null) {
            TrafficRecorderQueryExt queryExt = new TrafficRecorderQueryExt();
            BeanUtils.copyProperties(request, queryExt);
            // 这里对于restful格式的数据进行处理下
            if (ApiMatcher.isRestful(queryExt.getServiceName()) > 0) {
                //将 {} 全部替换成 % ，因为数据like 查询的时候，如果是{} 是不生效的
                queryExt.setServiceName(ApiMatcher.replaceAll(queryExt.getServiceName(), "%"));
            }
            return trafficRecorderExtApi.getTemplateCount(queryExt);
        }
        // 后续获取数据
        return 0L;
    }


}
