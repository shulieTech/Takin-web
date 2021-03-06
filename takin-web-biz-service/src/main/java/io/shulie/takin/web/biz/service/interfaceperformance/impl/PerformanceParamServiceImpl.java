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
 * @date 2022/5/20 10:20 ??????
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
     * ??????????????????????????????
     *
     * @param request
     */
    //  @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePerformanceData(PerformanceDataFileRequest request) {
        // 1?????????????????????????????????????????????
        Long configId = request.getId();
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_PARAM_ERROR, "??????Id?????????");
        }
        InterfacePerformanceConfigEntity configEntity = interfacePerformanceConfigMapper.selectById(configId);
        if (configEntity == null || configEntity.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "???????????????");
        }
        // 1????????????????????????????????????
        PerformanceParamDetailRequest allRequest = new PerformanceParamDetailRequest();
        allRequest.setConfigId(configId);
        PerformanceParamDetailResponse fileResponse = this.detail(allRequest);
        List<FileManageResponse> fileManageResponses = fileResponse.getFileManageResponseList();

        // 2??????????????????????????????,??????????????????????????????
        List<FileManageUpdateRequest> fileLists = request.getFileManageUpdateRequests();

        // a?????????????????????????????????
        Map<String, List<FileManageUpdateRequest>> un_delete_files = fileLists.stream().
                filter(s -> s.getId() != null).collect(Collectors.groupingBy(f -> {
            return String.valueOf(f.getId());
        }));

        // ??????????????????,??????????????????????????????
        List<FileManageResponse> delete_file_Param = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(fileManageResponses)) {
            for (int i = 0; i < fileManageResponses.size(); i++) {
                FileManageResponse tmpResponse = fileManageResponses.get(i);
                if (!un_delete_files.containsKey(String.valueOf(tmpResponse.getId()))) {
                    delete_file_Param.add(tmpResponse);
                }
            }
        }

        // ????????????????????????????????????????????????
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
         * ??????????????????
         */
        this.bindDataFile(request);

        // ????????????????????????,?????????????????????,?????????????????????????????????????????????
        // ????????????????????????
        PerformanceParamQueryParam deleteParams = new PerformanceParamQueryParam();
        deleteParams.setConfigId(configId);
        performanceParamDAO.deleteByParam(deleteParams);

        // b??????????????????
        if (CollectionUtils.isNotEmpty(fileLists)) {
            // ???????????????????????????????????????
            List<FileManageEntity> fileEntitys = getNewFileManage(request.getId());
            // ??????????????????,???????????????????????????????????????
            Map<String, List<FileManageEntity>> fileNameMap = fileEntitys.stream().collect(Collectors.groupingBy(FileManageEntity::getFileName));
            // ??????????????????????????????????????????
            List<PerformanceParamRequest> paramList = request.getParamList();
            // ?????????????????????????????????????????????????????????Map???????????????
            List<InterfacePerformanceParamEntity> insertList = paramList.stream()
                    .filter(paramRequest -> StringUtils.isNotBlank(paramRequest.getParamName())
                            && fileNameMap.containsKey(paramRequest.getParamValue()))
                    .map(paramRequest -> {
                        InterfacePerformanceParamEntity paramEntity = new InterfacePerformanceParamEntity();

                        paramEntity.setConfigId(configId);
                        paramEntity.setParamName(paramRequest.getParamName());
                        paramEntity.setFileColumnIndex(paramRequest.getFileColumnIndex());
                        paramEntity.setType(2);
                        // ????????????????????????
                        paramEntity.setParamValue(paramRequest.getParamValue());
                        // ?????????????????????????????????Id
                        Optional<FileManageEntity> entity = fileNameMap.get(paramRequest.getParamValue()).stream().findFirst();
                        paramEntity.setFileId(Optional.ofNullable(entity.get()).orElse(new FileManageEntity()).getId());
                        paramEntity.setGmtCreate(new Date());
                        return paramEntity;
                    }).collect(Collectors.toList());
            // ????????????
            performanceParamDAO.add(insertList);
        }
        /**
         * ????????????????????????????????????
         */
        pressureService.update(request);
    }

    @Override
    public void updatePerformanceData_ext(PerformanceDataFileRequest request) {
        // 1?????????????????????????????????????????????
        Long configId = request.getId();
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_PARAM_ERROR, "??????Id?????????");
        }
        InterfacePerformanceConfigEntity configEntity = interfacePerformanceConfigMapper.selectById(configId);
        if (configEntity == null || configEntity.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "???????????????");
        }
        // 1????????????????????????????????????
        PerformanceParamDetailRequest allRequest = new PerformanceParamDetailRequest();
        allRequest.setConfigId(configId);
        PerformanceParamDetailResponse fileResponse = this.detail(allRequest);
        List<FileManageResponse> fileManageResponses = fileResponse.getFileManageResponseList();

        // 2??????????????????????????????
        List<FileManageUpdateRequest> fileLists = request.getFileManageUpdateRequests();
        // a?????????????????????????????????Id??????
        List<Long> idList = fileLists.stream()
                .filter(file -> file.getId() != null)
                .map(file -> file.getId()).collect(Collectors.toList());
        // ???????????????????????????
        List<PerformanceParamRequest> deleteParam = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(fileManageResponses)) {
            for (int i = 0; i < fileManageResponses.size(); i++) {
                FileManageResponse tmpResponse = fileManageResponses.get(i);
                if (!idList.contains(tmpResponse.getId())) {
                    PerformanceParamRequest tmp = new PerformanceParamRequest();
                    tmp.setFileId(tmpResponse.getId());
                    tmp.setConfigId(configId);
                    tmp.setFilePath(tmpResponse.getUploadPath());
                    // ?????????????????????
                    deleteParam.add(tmp);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(deleteParam)) {
            // ????????????
            List<Long> deleteIdList = deleteParam.stream().map(file -> file.getFileId()).collect(Collectors.toList());
            // ????????????????????????
            PerformanceParamQueryParam deleteParams = new PerformanceParamQueryParam();
            deleteParams.setConfigId(configId);
            deleteParams.setFileIds(deleteIdList);
            performanceParamDAO.deleteByParam(deleteParams);
            // ????????????????????????
            List<String> filePaths = deleteParam.stream().map(file -> file.getFilePath()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(filePaths)) {
                FileDeleteParamReq fileDeleteParamReq = new FileDeleteParamReq();
                fileDeleteParamReq.setPaths(filePaths);
                fileApi.deleteFile(fileDeleteParamReq);
            }
        }

        // b????????????
        List<FileManageUpdateRequest> insertFileList = fileLists.stream().
                filter(file -> file.getUploadId() != null && file.getId() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(insertFileList)) {
            // ?????? + ??????Id
            String targetFilePath = String.format("%s/%s/%s/", ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_SCRIPT_PATH), "interfacePerformance", configId);
            copyFile(insertFileList, targetFilePath);

            // ??????????????????????????????
            List<FileManageCreateParam> fileManageCreateParams = getFileManageCreateParamsByUpdateReq(
                    insertFileList, targetFilePath);
            // ??????????????????, ????????????ids
            List<FileManageEntity> fileEntitys = fileManageDAO.createFileManageList_ext(fileManageCreateParams);
            // ??????????????????,???????????????????????????????????????
            Map<String, List<FileManageEntity>> fileNameMap = fileEntitys.stream().collect(Collectors.groupingBy(FileManageEntity::getFileName));
            // ??????????????????????????????????????????
            List<PerformanceParamRequest> paramList = request.getParamList();
            // ?????????????????????????????????????????????????????????Map???????????????
            List<InterfacePerformanceParamEntity> insertList = paramList.stream()
                    .filter(paramRequest -> StringUtils.isNotBlank(paramRequest.getParamName())
                            && fileNameMap.containsKey(paramRequest.getParamValue()))
                    .map(paramRequest -> {
                        InterfacePerformanceParamEntity paramEntity = new InterfacePerformanceParamEntity();

                        paramEntity.setConfigId(configId);
                        paramEntity.setParamName(paramRequest.getParamName());
                        paramEntity.setFileColumnIndex(paramRequest.getFileColumnIndex());
                        paramEntity.setType(2);
                        // ????????????????????????
                        paramEntity.setParamValue(paramRequest.getParamValue());
                        // ?????????????????????????????????Id
                        Optional<FileManageEntity> entity = fileNameMap.get(paramRequest.getParamValue()).stream().findFirst();
                        paramEntity.setFileId(Optional.ofNullable(entity.get()).orElse(new FileManageEntity()).getId());
                        paramEntity.setGmtCreate(new Date());
                        return paramEntity;
                    }).collect(Collectors.toList());
            // ????????????
            performanceParamDAO.add(insertList);
        }

        /**
         * ????????????????????????????????????
         */
        pressureService.update(request);
    }

    private List<FileManageEntity> getNewFileManage(Long configId) {
        InterfacePerformanceConfigSceneRelateShipEntity entity =
                performanceRelateshipDAO.relationShipEntityById(configId);
        // ????????????Id
        Long flowId = entity.getFlowId();
        // ???????????????????????????
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
        //??????????????????Id
        Long apiId = request.getId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("api_id", apiId);
        queryWrapper.eq("is_deleted", 0);
        InterfacePerformanceConfigSceneRelateShipEntity entity =
                performanceConfigSceneRelateShipMapper.selectOne(queryWrapper);
        if (Objects.isNull(entity)) {
            throw new RuntimeException("??????????????????,???????????????.");
        }
        Long flowId = entity.getFlowId();
        flowDataFileRequest.setId(flowId);
        //????????????
        flowDataFileRequest.setFileManageUpdateRequests(request.getFileManageUpdateRequests());
        pressureService.uploadDataFile(flowDataFileRequest);
    }

    @Override
    public PerformanceParamDetailResponse detail(PerformanceParamDetailRequest request) {
        Long configId = request.getConfigId();
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_PARAM_ERROR, "??????Id?????????");
        }
        InterfacePerformanceConfigEntity configEntity = interfacePerformanceConfigMapper.selectById(configId);
        if (configEntity == null && configEntity.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "???????????????");
        }
        PerformanceParamDetailResponse response = new PerformanceParamDetailResponse();

        // ???????????????????????????
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
            // ?????????
            tmpParam.setParamValue(vo.getParamValue());
            tmpParam.setType(vo.getType());
            tmpParam.setFileColumnIndex(vo.getFileColumnIndex());
            tmpParam.setFileId(vo.getFileId());
            // ?????????Id?????????
            fileIds.add(vo.getFileId());
            return tmpParam;
        }).collect(Collectors.toList());
        // ??????????????????
        List<FileManageResponse> fileManageResponseList = fileManageDAO.getFileManageResponseByFileIds(new ArrayList<>(fileIds));
        response.setConfigId(request.getConfigId());
        response.setFileManageResponseList(fileManageResponseList);
        response.setParamList(params);
        return response;
    }

    /**
     * ??????????????????
     *
     * @param request
     * @return
     */
    @Override
    public PerformanceParamDetailResponse fileContentDetail(PerformanceParamDetailRequest request) {
        /**
         * ???????????????????????????????????????????????????????????????????????????
         * ???????????????????????????????????????,???????????????????????????????????????????????????
         */
        List<FileManageUpdateRequest> fileList = request.getFileManageUpdateRequests();
        PerformanceParamDetailResponse response = new PerformanceParamDetailResponse();
        if (CollectionUtils.isEmpty(fileList)) {
            return response;
        }
        // 1????????????????????????????????????
        List<String> filePaths = fileList.stream().filter(file -> file.getUploadId() != null).map(file -> file.getDownloadUrl()).collect(Collectors.toList());
        List<PerformanceParamRequest> fileParams = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(filePaths)) {
            // ???????????????????????????????????????
            for (int i = 0; i < filePaths.size(); i++) {
                String path = filePaths.get(i);
                try {
                    File file = FileUtil.file(path);
                    //???????????????
                    String fileName = FileUtil.getName(file);
                    // ??????csv???Excel??????
                    if (!supportFileType(fileName)) {
                        throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_FILE_TYPE_ERROR, fileName);
                    }
                    // ?????????????????????
                    Map<String, String> firstRow = FileUtils.readFirstRow(path);
                    Iterator<String> it = firstRow.keySet().iterator();
                    int index = 1;
                    while (it.hasNext()) {
                        String next = it.next();
                        PerformanceParamRequest paramRequest = new PerformanceParamRequest();
                        // ?????????
                        paramRequest.setFileColumnIndex(index);
                        paramRequest.setParamName(next);
                        // ????????? test.csv
                        paramRequest.setParamValue(fileName);
                        // ????????????
                        paramRequest.setType(2);
                        // ???????????????
                        fileParams.add(paramRequest);
                        index++;
                    }
                } catch (Throwable e) {
                    log.error("??????????????????,{},{}", path, ExceptionUtils.getStackTrace(e));
                }
            }
        }

        // 2??????????????????????????????????????????,????????????
        List<Long> fileIds = fileList.stream().filter(file -> file.getId() != null).map(file -> file.getId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fileIds)) {
            PerformanceParamQueryParam queryParam = new PerformanceParamQueryParam();
            queryParam.setConfigId(request.getConfigId());
            queryParam.setFileIds(fileIds);
            List<PerformanceParamDto> dtoList = performanceParamDAO.queryParamByCondition(queryParam);
            if (CollectionUtils.isNotEmpty(dtoList)) {
                dtoList.stream().forEach(vo -> {
                    PerformanceParamRequest paramRequest = new PerformanceParamRequest();
                    // ?????????
                    paramRequest.setFileColumnIndex(vo.getFileColumnIndex());
                    paramRequest.setParamName(vo.getParamName());
                    // ????????? test.csv
                    paramRequest.setParamValue(vo.getParamValue());
                    // ????????????
                    paramRequest.setType(vo.getType());
                    // ???????????????
                    fileParams.add(paramRequest);
                });
            }
        }
        response.setParamList(fileParams);
        // ???????????????????????????
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
                        "???????????????," + repeatParamNames);
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
     * ???????????????????????????
     */
    private void copyFile(List<FileManageUpdateRequest> fileManageUpdateRequest, String targetScriptPath) {
        List<String> sourcePaths = new ArrayList<>();
        String tmpFilePath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH);
        List<String> tmpFilePaths = fileManageUpdateRequest.stream().map(
                o -> tmpFilePath + "/" + o.getUploadId() + "/" + o.getFileName()).collect(Collectors.toList());
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
