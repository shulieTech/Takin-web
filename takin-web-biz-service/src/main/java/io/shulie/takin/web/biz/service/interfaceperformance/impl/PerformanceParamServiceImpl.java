package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.google.common.collect.Sets;
import io.shulie.takin.cloud.entrypoint.file.CloudFileApi;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileCopyParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileDeleteParamReq;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamDetailRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamDetailResponse;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceParamService;
import io.shulie.takin.web.biz.utils.xlsx.ExcelUtils;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.FileUtils;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceParamVO;
import io.shulie.takin.web.data.dao.filemanage.FileManageDAO;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceConfigDAO;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceParamDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.param.filemanage.FileManageCreateParam;
import io.shulie.takin.web.data.param.interfaceperformance.PerformanceParamQueryParam;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
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
    private PerformanceParamDAO performanceParamDAO;

    @Resource
    private DiffFileApi fileApi;

    @Resource
    private FileManageDAO fileManageDAO;

    @Resource
    private DiffFileApi diffFileApi;

    /**
     * 更新接口压测数据文件
     *
     * @param request
     */
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
        // 2、处理文件
        List<FileManageUpdateRequest> fileLists = request.getFileManageUpdateRequests();
        if (CollectionUtils.isNotEmpty(fileLists)) {
            /**
             * 新增的,id为空，uploadId不为空
             * 删除的,Id不为空,IsDeleted为1
             * 未修改的，不处理
             */
            // a、找到已删除的文件信息
            List<Long> deleteIdList = fileLists.stream().
                    filter(file -> file.getIsDeleted() == 1 && file.getId() != null)
                    .map(file -> file.getId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deleteIdList)) {
                // 清理掉这部分数据
                performanceParamDAO.deleteByIds(deleteIdList);
            }
            // b、新增的
            List<FileManageUpdateRequest> insertFileList = fileLists.stream().
                    filter(file -> file.getUploadId() != null && file.getIsDeleted() == 0)
                    .collect(Collectors.toList());
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
                List<InterfacePerformanceParamEntity> insertList = paramList.stream().map(paramRequest -> {
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
        }
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
        List<PerformanceParamVO> resultVos = performanceParamDAO.queryParamByCondition(param);
        Set<Long> fileIds = Sets.newHashSet();
        List<PerformanceParamRequest> params = resultVos.stream().map(vo -> {
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
        List<FileManageResponse> fileManageResponseList = getFileManageResponseByFileIds(new ArrayList<>(fileIds));
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
                    if (!fileName.endsWith("csv")) {
                        throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_FILE_TYPE_ERROR, fileName);
                    }

                    // 读取第一行数据
                    Map<String, String> firstRow = FileUtils.readCsvFirstRows(path);
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
            List<PerformanceParamVO> paramVOS = performanceParamDAO.queryParamByCondition(queryParam);
            if (CollectionUtils.isNotEmpty(paramVOS)) {
                paramVOS.stream().forEach(vo -> {
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
        return response;
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
}
