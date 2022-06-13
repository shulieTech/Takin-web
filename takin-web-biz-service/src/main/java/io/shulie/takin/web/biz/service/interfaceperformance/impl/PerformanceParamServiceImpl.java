package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Sets;
import io.shulie.takin.adapter.api.model.request.filemanager.FileCopyParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileDeleteParamReq;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamDetailRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamDetailResponse;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceParamService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformancePressureService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.FileUtils;
import io.shulie.takin.web.biz.service.interfaceperformance.vo.PerformanceParamVO;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceParamDto;
import io.shulie.takin.web.data.dao.filemanage.FileManageDAO;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceParamDAO;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceRelateshipDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigSceneRelateShipMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.param.filemanage.FileManageCreateParam;
import io.shulie.takin.web.data.param.interfaceperformance.PerformanceParamQueryParam;
import io.shulie.takin.web.data.result.filemanage.FileManageResponse;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.diff.api.DiffFileApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/20 10:20 上午
 */
@Slf4j
@Service
public class PerformanceParamServiceImpl implements PerformanceParamService {
    @Autowired
    private InterfacePerformanceConfigMapper interfacePerformanceConfigMapper;

    @Autowired
    private PerformanceRelateshipDAO performanceRelateshipDAO;

    @Autowired
    private PerformanceParamDAO performanceParamDAO;

    @Resource
    private DiffFileApi fileApi;

    @Resource
    private FileManageDAO fileManageDAO;

    @Resource
    PerformancePressureService pressureService;

    @Autowired
    private SceneService sceneService;

    @Resource
    InterfacePerformanceConfigSceneRelateShipMapper performanceConfigSceneRelateShipMapper;

    /**
     * 更新接口压测数据文件
     *
     * @param request
     */
    //  @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePerformanceData(PerformanceDataFileRequest request) {
        // 1、查看当前接口压测场景是否存在
        Long configId = request.getId();
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_PARAM_ERROR, "参数Id未传递");
        }
        InterfacePerformanceConfigEntity configEntity = interfacePerformanceConfigMapper.selectById(configId);
        if (configEntity == null || configEntity.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "配置未找到");
        }
        // 1、获取原来对应的文件信息
        PerformanceParamDetailRequest allRequest = new PerformanceParamDetailRequest();
        allRequest.setConfigId(configId);
        PerformanceParamDetailResponse fileResponse = this.detail(allRequest);
        List<FileManageResponse> fileManageResponses = fileResponse.getFileManageResponseList();

        // 2、获取最新的文件处理,客户端每次都是最新的
        List<FileManageUpdateRequest> fileLists = request.getFileManageUpdateRequests();

        // a、找到未删除的文件信息
        Map<String, List<FileManageUpdateRequest>> un_delete_files = fileLists.stream().
                filter(s -> s.getId() != null).collect(Collectors.groupingBy(f -> {
            return String.valueOf(f.getId());
        }));

        // 遍历文件操作,找到已删除的文件信息
        List<FileManageResponse> delete_file_Param = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(fileManageResponses)) {
            for (int i = 0; i < fileManageResponses.size(); i++) {
                FileManageResponse tmpResponse = fileManageResponses.get(i);
                if (!un_delete_files.containsKey(String.valueOf(tmpResponse.getId()))) {
                    delete_file_Param.add(tmpResponse);
                }
            }
        }

        // 构造删除的数据文件到流程接口里面
        if (CollectionUtils.isNotEmpty(delete_file_Param)) {
            List<FileManageUpdateRequest> delete_flag_files = delete_file_Param.stream().map(delete_file -> {
                FileManageUpdateRequest updateFile = new FileManageUpdateRequest();
                BeanUtils.copyProperties(delete_file, updateFile);
                updateFile.setIsDeleted(1);
                return updateFile;
            }).collect(Collectors.toList());
            request.getFileManageUpdateRequests().addAll(delete_flag_files);
        }
        /**
         * 绑定数据文件
         */
        this.bindDataFile(request);

        // 将原有数据清理掉,流程处理的时候,文件已经更改了。直接只能清理掉
        // 清理掉这部分数据
        PerformanceParamQueryParam deleteParams = new PerformanceParamQueryParam();
        deleteParams.setConfigId(configId);
        performanceParamDAO.deleteByParam(deleteParams);

        // b、最新的插入
        if (CollectionUtils.isNotEmpty(fileLists)) {
            // 获取流程绑定的最新文件信息
            List<FileManageEntity> fileEntitys = getNewFileManage(request.getId());
            // 按文件名分组,后续这里用文件名去匹配字段
            Map<String, List<FileManageEntity>> fileNameMap = fileEntitys.stream().collect(Collectors.groupingBy(FileManageEntity::getFileName));
            // 把解析的参数给放到参数表里面
            List<PerformanceParamRequest> paramList = request.getParamList();
            // 过滤掉那种未修改的，这种就在新增的文件Map里面找不到
            List<InterfacePerformanceParamEntity> insertList = paramList.stream()
                    .filter(paramRequest -> StringUtils.isNotBlank(paramRequest.getParamName())
                            && fileNameMap.containsKey(paramRequest.getParamValue()))
                    .map(paramRequest -> {
                        InterfacePerformanceParamEntity paramEntity = new InterfacePerformanceParamEntity();

                        paramEntity.setConfigId(configId);
                        paramEntity.setParamName(paramRequest.getParamName());
                        paramEntity.setFileColumnIndex(paramRequest.getFileColumnIndex());
                        paramEntity.setType(2);
                        // 参数值放文件名称
                        paramEntity.setParamValue(paramRequest.getParamValue());
                        // 根据名称获取存入的文件Id
                        Optional<FileManageEntity> entity = fileNameMap.get(paramRequest.getParamValue()).stream().findFirst();
                        paramEntity.setFileId(Optional.ofNullable(entity.get()).orElse(new FileManageEntity()).getId());
                        paramEntity.setGmtCreate(new Date());
                        return paramEntity;
                    }).collect(Collectors.toList());
            // 新增参数
            performanceParamDAO.add(insertList);
        }
        /**
         * 更新脚本和场景和业务流程
         */
        pressureService.update(request);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePerformanceData_ext(PerformanceDataFileRequest request) {
        // 1、查看当前接口压测场景是否存在
        Long configId = request.getId();
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_PARAM_ERROR, "参数Id未传递");
        }
        InterfacePerformanceConfigEntity configEntity = interfacePerformanceConfigMapper.selectById(configId);
        if (configEntity == null || configEntity.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "配置未找到");
        }
        // 1、获取原来对应的文件信息
        PerformanceParamDetailRequest allRequest = new PerformanceParamDetailRequest();
        allRequest.setConfigId(configId);
        PerformanceParamDetailResponse fileResponse = this.detail(allRequest);
        List<FileManageResponse> fileManageResponses = fileResponse.getFileManageResponseList();

        // 2、获取最新的文件处理
        List<FileManageUpdateRequest> fileLists = request.getFileManageUpdateRequests();
        // a、找到未删除的文件信息Id集合
        List<Long> idList = fileLists.stream()
                .filter(file -> file.getId() != null)
                .map(file -> file.getId()).collect(Collectors.toList());
        // 遍历最新的文件操作
        List<PerformanceParamRequest> deleteParam = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(fileManageResponses)) {
            for (int i = 0; i < fileManageResponses.size(); i++) {
                FileManageResponse tmpResponse = fileManageResponses.get(i);
                if (!idList.contains(tmpResponse.getId())) {
                    PerformanceParamRequest tmp = new PerformanceParamRequest();
                    tmp.setFileId(tmpResponse.getId());
                    tmp.setConfigId(configId);
                    tmp.setFilePath(tmpResponse.getUploadPath());
                    // 需要删除的数据
                    deleteParam.add(tmp);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(deleteParam)) {
            // 清理数据
            List<Long> deleteIdList = deleteParam.stream().map(file -> file.getFileId()).collect(Collectors.toList());
            // 清理掉这部分数据
            PerformanceParamQueryParam deleteParams = new PerformanceParamQueryParam();
            deleteParams.setConfigId(configId);
            deleteParams.setFileIds(deleteIdList);
            performanceParamDAO.deleteByParam(deleteParams);
            // 删除对应路径文件
            List<String> filePaths = deleteParam.stream().map(file -> file.getFilePath()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(filePaths)) {
                FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
                fileDeleteParamReq.setPaths(filePaths);
                fileApi.deleteFile(fileDeleteParamReq);
            }
        }

        // b、新增的
        List<FileManageUpdateRequest> insertFileList = fileLists.stream().
                filter(file -> file.getUploadId() != null && file.getId() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(insertFileList)) {
            // 目录 + 配置Id
            String targetFilePath = String.format("%s/%s/%s/", ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_SCRIPT_PATH), "interfacePerformance", configId);
            copyFile(insertFileList, targetFilePath);

            // 插入文件记录所需参数
            List<FileManageCreateParam> fileManageCreateParams = getFileManageCreateParamsByUpdateReq(
                    insertFileList, targetFilePath);
            // 创建文件记录, 获得文件ids
            List<FileManageEntity> fileEntitys = fileManageDAO.createFileManageList_ext(fileManageCreateParams);
            // 按文件名分组,后续这里用文件名去匹配字段
            Map<String, List<FileManageEntity>> fileNameMap = fileEntitys.stream().collect(Collectors.groupingBy(FileManageEntity::getFileName));
            // 把解析的参数给放到参数表里面
            List<PerformanceParamRequest> paramList = request.getParamList();
            // 过滤掉那种未修改的，这种就在新增的文件Map里面找不到
            List<InterfacePerformanceParamEntity> insertList = paramList.stream()
                    .filter(paramRequest -> StringUtils.isNotBlank(paramRequest.getParamName())
                            && fileNameMap.containsKey(paramRequest.getParamValue()))
                    .map(paramRequest -> {
                        InterfacePerformanceParamEntity paramEntity = new InterfacePerformanceParamEntity();

                        paramEntity.setConfigId(configId);
                        paramEntity.setParamName(paramRequest.getParamName());
                        paramEntity.setFileColumnIndex(paramRequest.getFileColumnIndex());
                        paramEntity.setType(2);
                        // 参数值放文件名称
                        paramEntity.setParamValue(paramRequest.getParamValue());
                        // 根据名称获取存入的文件Id
                        Optional<FileManageEntity> entity = fileNameMap.get(paramRequest.getParamValue()).stream().findFirst();
                        paramEntity.setFileId(Optional.ofNullable(entity.get()).orElse(new FileManageEntity()).getId());
                        paramEntity.setGmtCreate(new Date());
                        return paramEntity;
                    }).collect(Collectors.toList());
            // 新增参数
            performanceParamDAO.add(insertList);
        }

        /**
         * 更新脚本和场景和业务流程
         */
        pressureService.update(request);
    }

    private List<FileManageEntity> getNewFileManage(Long configId) {
        InterfacePerformanceConfigSceneRelateShipEntity entity =
                performanceRelateshipDAO.relationShipEntityById(configId);
        // 获取流程Id
        Long flowId = entity.getFlowId();
        // 读取流程关联文件表
        BusinessFlowDetailResponse detail = sceneService.getBusinessFlowDetail(flowId);
        List<io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse> fileManageResponseList = detail.getFileManageResponseList();
        List<FileManageEntity> fileManageEntityList = fileManageResponseList.stream().map(newFile -> {
            FileManageEntity fileManageEntity = new FileManageEntity();
            BeanUtils.copyProperties(newFile, fileManageEntity);
            return fileManageEntity;
        }).collect(Collectors.toList());
        return fileManageEntityList;
    }

    private void bindDataFile(PerformanceDataFileRequest request) {
        BusinessFlowDataFileRequest flowDataFileRequest = new BusinessFlowDataFileRequest();
        //查询业务流程Id
        Long apiId = request.getId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("api_id", apiId);
        queryWrapper.eq("is_deleted", 0);
        InterfacePerformanceConfigSceneRelateShipEntity entity =
                performanceConfigSceneRelateShipMapper.selectOne(queryWrapper);
        if (Objects.isNull(entity)) {
            throw new RuntimeException("请先保存场景,再上传文件.");
        }
        Long flowId = entity.getFlowId();
        flowDataFileRequest.setId(flowId);
        //绑定文件
        flowDataFileRequest.setFileManageUpdateRequests(request.getFileManageUpdateRequests());
        pressureService.uploadDataFile(flowDataFileRequest);
    }

    @Override
    public PerformanceParamDetailResponse detail(PerformanceParamDetailRequest request) {
        Long configId = request.getConfigId();
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_PARAM_ERROR, "参数Id未传递");
        }
        InterfacePerformanceConfigEntity configEntity = interfacePerformanceConfigMapper.selectById(configId);
        if (configEntity == null && configEntity.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "配置未找到");
        }
        PerformanceParamDetailResponse response = new PerformanceParamDetailResponse();

        // 读取所有的参数信息
        PerformanceParamQueryParam param = new PerformanceParamQueryParam();
        param.setConfigId(request.getConfigId());
        List<PerformanceParamDto> dtoList = performanceParamDAO.queryParamByCondition(param);
        if (CollectionUtils.isEmpty(dtoList)) {
            return response;
        }
        Set<Long> fileIds = Sets.newHashSet();
        List<PerformanceParamRequest> params = dtoList.stream().map(vo -> {
            PerformanceParamRequest tmpParam = new PerformanceParamRequest();
            tmpParam.setParamName(vo.getParamName());
            // 文件名
            tmpParam.setParamValue(vo.getParamValue());
            tmpParam.setType(vo.getType());
            tmpParam.setFileColumnIndex(vo.getFileColumnIndex());
            tmpParam.setFileId(vo.getFileId());
            // 把文件Id存在来
            fileIds.add(vo.getFileId());
            return tmpParam;
        }).collect(Collectors.toList());
        // 找到文件列表
        List<FileManageResponse> fileManageResponseList = fileManageDAO.getFileManageResponseByFileIds(new ArrayList<>(fileIds));
        response.setConfigId(request.getConfigId());
        response.setFileManageResponseList(fileManageResponseList);
        response.setParamList(params);
        return response;
    }

    /**
     * 文件内容详情
     *
     * @param request
     * @return
     */
    @Override
    public PerformanceParamDetailResponse fileContentDetail(PerformanceParamDetailRequest request) {
        /**
         * 这里主要是有两类信息，一个是新增的文件读取内容出来
         * 另外一类是已经存到表里面的,已经保存的参数信息，这就读表就好了
         */
        List<FileManageUpdateRequest> fileList = request.getFileManageUpdateRequests();
        PerformanceParamDetailResponse response = new PerformanceParamDetailResponse();
        if (CollectionUtils.isEmpty(fileList)) {
            return response;
        }
        // 1、过滤出那种刚上传的文件
        List<String> filePaths = fileList.stream().filter(file -> file.getUploadId() != null).map(file -> file.getDownloadUrl()).collect(Collectors.toList());
        List<PerformanceParamRequest> fileParams = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(filePaths)) {
            // 刚上传的，从临时文件中读取
            for (int i = 0; i < filePaths.size(); i++) {
                String path = filePaths.get(i);
                try {
                    File file = FileUtil.file(path);
                    //获取文件名
                    String fileName = FileUtil.getName(file);
                    // 支持csv和Excel文件
                    if (!supportFileType(fileName)) {
                        throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_FILE_TYPE_ERROR, fileName);
                    }
                    // 读取第一行数据
                    Map<String, String> firstRow = FileUtils.readFirstRow(path);
                    Iterator<String> it = firstRow.keySet().iterator();
                    int index = 1;
                    while (it.hasNext()) {
                        String next = it.next();
                        PerformanceParamRequest paramRequest = new PerformanceParamRequest();
                        // 索引列
                        paramRequest.setFileColumnIndex(index);
                        paramRequest.setParamName(next);
                        // 文件名 test.csv
                        paramRequest.setParamValue(fileName);
                        // 数据文件
                        paramRequest.setType(2);
                        // 添加到集合
                        fileParams.add(paramRequest);
                        index++;
                    }
                } catch (Throwable e) {
                    log.error("文件解析失败,{},{}", path, ExceptionUtils.getStackTrace(e));
                }
            }
        }

        // 2、已经解析过的，存在表里面的,直接查询
        List<Long> fileIds = fileList.stream().filter(file -> file.getId() != null).map(file -> file.getId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fileIds)) {
            PerformanceParamQueryParam queryParam = new PerformanceParamQueryParam();
            queryParam.setConfigId(request.getConfigId());
            queryParam.setFileIds(fileIds);
            List<PerformanceParamDto> dtoList = performanceParamDAO.queryParamByCondition(queryParam);
            if (CollectionUtils.isNotEmpty(dtoList)) {
                dtoList.stream().forEach(vo -> {
                    PerformanceParamRequest paramRequest = new PerformanceParamRequest();
                    // 索引列
                    paramRequest.setFileColumnIndex(vo.getFileColumnIndex());
                    paramRequest.setParamName(vo.getParamName());
                    // 文件名 test.csv
                    paramRequest.setParamValue(vo.getParamValue());
                    // 数据文件
                    paramRequest.setType(vo.getType());
                    // 添加到集合
                    fileParams.add(paramRequest);
                });
            }
        }
        response.setParamList(fileParams);
        // 判断参数名是否重复
        if (CollectionUtils.isNotEmpty(fileParams)) {
            List<String> repeatParamNames = Lists.newArrayList();
            Map<String, Long> repeatDataMap = fileParams.stream().
                    collect(Collectors.groupingBy(PerformanceParamRequest::getParamName,
                            Collectors.counting()));
            for (Map.Entry<String, Long> entry : repeatDataMap.entrySet()) {
                String paramName = entry.getKey();
                Long count = entry.getValue();
                if (count > 1) {
                    repeatParamNames.add(paramName);
                }
            }
            if (CollectionUtils.isNotEmpty(repeatParamNames)) {
                throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_FILE_PARAM_ERROR,
                        "参数名重复," + repeatParamNames);
            }
        }
        return response;
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

    /**
     * 复制文件到正式目录
     */
    private void copyFile(List<FileManageUpdateRequest> fileManageUpdateRequest, String targetScriptPath) {
        List<String> sourcePaths = new ArrayList<>();
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH);
        List<String> tmpFilePaths = fileManageUpdateRequest.stream().map(
                o -> tmpFilePath + "/" + o.getUploadId() + "/" + o.getFileName()).collect(Collectors.toList());
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
    }

    public boolean supportFileType(String fileName) {
        if (fileName.endsWith("csv")
                || fileName.endsWith("xlsx")
                || fileName.endsWith("xls")) {
            return true;
        }
        return false;
    }
}
