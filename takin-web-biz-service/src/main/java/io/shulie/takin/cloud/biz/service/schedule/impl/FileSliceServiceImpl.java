package io.shulie.takin.cloud.biz.service.schedule.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.cloud.entity.domain.vo.file.FileSliceRequest;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneContactFileOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneContactFileOutput.ContactFileInfo;
import io.shulie.takin.cloud.biz.pojo.FileSplitInfo;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneTaskService;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.biz.utils.FileSplitUtil;
import io.shulie.takin.cloud.common.enums.FileSliceStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum.StartEndPair;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneBigFileSliceDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author xr.l
 */
@Service
@Slf4j
public class FileSliceServiceImpl implements FileSliceService {

    @Resource
    SceneBigFileSliceDAO fileSliceDAO;
    @Resource
    private CloudSceneTaskService cloudSceneTaskService;

    @Value("${script.path}")
    private String nfsDir;
    private static final String DEFAULT_PATH_SEPARATOR = "/";
    private static final String DEFAULT_FILE_COLUMN_SEPARATOR = "@@@";

    @Override
    public boolean fileSlice(FileSliceRequest request) throws TakinCloudException {
        Integer fileSliceStatusCode = this.isFileSliced(request);
        FileSliceStatusEnum fileSliceStatus = FileSliceStatusEnum.getFileSliceStatusEnumByCode(
            fileSliceStatusCode);
        SceneBigFileSliceParam param = new SceneBigFileSliceParam() {{
            setSceneId(request.getSceneId());
            setFileName(request.getFileName());
            setStatus(FileSliceStatusEnum.SLICING.getCode());
        }};
        updateFileRefExtend(request, param);
        if (Objects.isNull(fileSliceStatus)) {
            log.error("【文件分片】--场景ID：【{}】，文件名：【{}】 文件分片异常，未查询到文件分片信息.", request.getSceneId(), request.getFileName());
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_START_ERROR_FILE_SLICING, "文件分片异常，未查询到分片信息");
        }
        switch (fileSliceStatus) {
            case SLICED:
                log.info("【文件分片】--场景ID：【{}】，文件名：【{}】 文件已分片.", request.getSceneId(), request.getFileName());
                return true;
            case UNSLICED:
                log.info("【文件分片】--场景ID：【{}】，文件名：【{}】 文件未分片，执行分片", request.getSceneId(), request.getFileName());
                fileSliceDAO.create(param);
                return slice(request, param);
            case SLICING:
            case FILE_CHANGED:
                log.info("【文件分片】--场景ID：【{}】，文件名：【{}】 文件或场景变更，重新分片.", request.getSceneId(), request.getFileName());
                fileSliceDAO.update(param);
                return slice(request, param);
            default:
                log.error("【文件分片】--场景ID：【{}】，文件名：【{}】 文件分片异常，未查询到文件分片信息.", request.getSceneId(), request.getFileName());
                throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_START_ERROR_FILE_SLICING,
                    "文件分片异常，未查询到分片信息");
        }
    }

    private boolean slice(FileSliceRequest request, SceneBigFileSliceParam param) throws TakinCloudException {
        if (request.getSplit() && request.getOrderSplit() != null && request.getOrderSplit()) {
            sliceFileByOrder(request, param);
        } else {
            sliceFileWithoutOrder(request, param);
        }
        return fileSliceDAO.update(param) == 1;
    }

    @Override
    public SceneBigFileSliceEntity getOneByParam(FileSliceRequest request) {
        return fileSliceDAO.selectOne(new SceneBigFileSliceParam() {{
            setSceneId(request.getSceneId());
            setFileName(request.getFileName());
        }});
    }

    @Override
    public Integer isFileSliced(FileSliceRequest request) {
        return fileSliceDAO.isFileSliced(new SceneBigFileSliceParam() {{
            setSceneId(request.getSceneId());
            setFileName(request.getFileName());
            setSliceCount(request.getPodNum());
            setFileMd5(request.getFileMd5());
            setFilePath(request.getFilePath());
        }});
    }

    @Override
    public Boolean updateFileRefExtend(FileSliceRequest request, SceneBigFileSliceParam param) {
        boolean hasChange = false;
        SceneScriptRefEntity entity = fileSliceDAO.selectRef(new SceneBigFileSliceParam() {{
            setSceneId(request.getSceneId());
            setFileName(request.getFileName());
        }});
        JSONObject jsonObject;
        Date uploadTime = new Date();
        if (entity == null || entity.getId() == null) {
            SceneScriptRef insertParam = new SceneScriptRef();
            insertParam.setFileName(request.getFileName());
            insertParam.setSceneId(request.getSceneId());
            insertParam.setScriptType(0);
            insertParam.setFileType(1);
            insertParam.setUploadTime(new Date());
            insertParam.setUploadTime(uploadTime);
            insertParam.setUploadPath(request.getSceneId() + DEFAULT_PATH_SEPARATOR + request.getFileName());
            jsonObject = new JSONObject();
            if (request.getSplit()) {
                jsonObject.put(FileSliceService.IS_SPLIT, 1);
            }
            if (request.getOrderSplit()) {
                jsonObject.put(FileSliceService.IS_ORDER_SPLIT, 1);
            }
            insertParam.setFileExtend(jsonObject.toJSONString());
            Long refId = fileSliceDAO.createRef(insertParam);
            request.setRefId(refId);
            request.setFilePath(request.getSceneId() + DEFAULT_PATH_SEPARATOR + request.getFileName());
            param.setFileUploadTime(uploadTime);
            param.setFileRefId(refId);
            return refId > 0;
        }
        jsonObject = JSONObject.parseObject(entity.getFileExtend());
        if (request.getSplit() && !jsonObject.containsKey(FileSliceService.IS_SPLIT)) {
            jsonObject.put(FileSliceService.IS_SPLIT, 1);
            hasChange = true;
        }
        if (request.getOrderSplit() && !jsonObject.containsKey(FileSliceService.IS_ORDER_SPLIT)) {
            jsonObject.put(FileSliceService.IS_ORDER_SPLIT, 1);
            hasChange = true;
        }
        param.setFileUploadTime(entity.getUploadTime());
        entity.setFileExtend(jsonObject.toJSONString());
        param.setFileRefId(entity.getId());
        request.setRefId(entity.getId());
        request.setFilePath(entity.getUploadPath());
        if (hasChange) {
            return fileSliceDAO.updateRef(entity) == 1;
        }
        return true;
    }

    @Override
    public void updateFileMd5(SceneBigFileSliceParam param) {
        SceneScriptRefEntity sceneScriptRefEntity = fileSliceDAO.selectRef(new SceneBigFileSliceParam() {{
            setSceneId(param.getSceneId());
            setFileName(param.getFileName());
        }});
        if (Objects.nonNull(sceneScriptRefEntity)) {
            sceneScriptRefEntity.setFileMd5(param.getFileMd5());
            fileSliceDAO.updateRef(sceneScriptRefEntity);
        }
    }

    @Override
    public SceneContactFileOutput contactScene(SceneBigFileSliceParam param) throws TakinCloudException {
        SceneContactFileOutput output = new SceneContactFileOutput();
        output.setSceneId(param.getSceneId());
        String fileDir = nfsDir + DEFAULT_PATH_SEPARATOR + param.getSceneId() + DEFAULT_PATH_SEPARATOR;
        File dir = new File(fileDir);
        File[] files;
        File targetFile = null;
        String errorInfo;
        if (dir.exists() && dir.isDirectory()) {
            files = dir.listFiles((dir1, name) -> name.endsWith(".csv"));
            //场景文件夹下没有文件
            if (Objects.isNull(files) || files.length == 0) {
                errorInfo = String.format("更新关联文件异常，场景[%s]文件夹下未找到数据文件", param.getSceneId());
                throw new TakinCloudException(TakinCloudExceptionEnum.EMPTY_DIRECTORY_ERROR, errorInfo);
            }
            //场景文件夹下存在多个文件，入参文件名为空
            else if (StringUtils.isBlank(param.getFileName()) && files.length > 1) {
                output.setFiles(Arrays.stream(files).map(file -> new ContactFileInfo() {{
                    setFileName(file.getName());
                    setFilePath(file.getAbsolutePath());
                    setSize(file.length());
                }}).collect(Collectors.toList()));
                errorInfo = String.format("更新文件关联异常，参数未输入文件名，场景文件夹[%s]下有多个csv数据文件", param.getSceneId());
                throw new TakinCloudException(TakinCloudExceptionEnum.TOO_MUCH_FILE_ERROR, errorInfo);
            }
            //场景文件夹下只有一个数据文件，入参文件名为空
            else if (StringUtils.isBlank(param.getFileName()) && files.length == 1) {
                targetFile = files[0];
            }
            //入参文件名不为空，多个文件，匹配文件
            else if (StringUtils.isNotBlank(param.getFileName())) {
                for (File file : files) {
                    if (param.getFileName().equals(file.getName())) {
                        targetFile = file;
                        break;
                    }
                }
            }
            if (Objects.isNull(targetFile)) {
                errorInfo = String.format("在场景[%s]文件夹下未找到对应的文件[%s]", param.getSceneId(), param.getFileName());
                throw new TakinCloudException(TakinCloudExceptionEnum.EMPTY_DIRECTORY_ERROR, errorInfo);
            } else {
                param.setFileName(targetFile.getName());
                ContactFileInfo contactFileInfo = new ContactFileInfo();
                boolean b = contactFileAndScene(targetFile, param, contactFileInfo);
                if (b) {
                    contactFileInfo.setSize(targetFile.length());
                    contactFileInfo.setFileName(targetFile.getName());
                    contactFileInfo.setFilePath(targetFile.getAbsolutePath());
                    output.setFiles(Lists.newArrayList(contactFileInfo));
                }
            }
        } else {
            log.error("未找到场景文件夹{}，请检查场景ID是否正确！", param.getSceneId());
            throw new TakinCloudException(TakinCloudExceptionEnum.EMPTY_DIRECTORY_ERROR, "未找到场景文件夹，请检查场景ID是否正确！");
        }
        return output;
    }

    /**
     * 文件顺序拆分，包含排序字段，排序列号如果没有传，默认按最后一列
     *
     * @param request 文件拆分参数
     * @param param   大文件拆分参数
     */
    private void sliceFileByOrder(FileSliceRequest request, SceneBigFileSliceParam param) throws TakinCloudException {
        try {
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】 文件【顺序分片】任务执开始.", request.getSceneId(), request.getFileName());
            List<FileSplitInfo> fileSplitInfos = FileSplitUtil.splitFileByPartition(
                nfsDir + DEFAULT_PATH_SEPARATOR + request.getFilePath(),
                StringUtils.isBlank(request.getDelimiter()) ? DEFAULT_FILE_COLUMN_SEPARATOR
                    : request.getDelimiter());
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】 文件【顺序分片】任务执结束.", request.getSceneId(), request.getFileName());
            if (CollectionUtils.isNotEmpty(fileSplitInfos)) {
                String sliceInfo = JSONObject.toJSONString(fileSplitInfos);
                param.setStatus(FileSliceStatusEnum.SLICED.getCode());
                param.setSliceInfo(sliceInfo);
                param.setSliceCount(fileSplitInfos.size());
            }
        } catch (Exception e) {
            log.error("【文件分片】--场景ID：【{}】，文件名：【{}】 文件【顺序分片】任务异常,异常信息：【{}】", request.getSceneId(), request.getFileName(),
                e.getMessage());
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR, e.getMessage());
        }
    }

    /**
     * 根据pod数量拆分文件
     *
     * @param request 文件拆分参数
     * @param param   大文件拆分参数
     */
    private void sliceFileWithoutOrder(FileSliceRequest request, SceneBigFileSliceParam param) {
        try {
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】，传入pod数量：【{}】 文件分片任务执开始.", request.getSceneId(), request.getFileName(),
                request.getPodNum());
            FileSliceByPodNum build = new FileSliceByPodNum.Builder(
                nfsDir + DEFAULT_PATH_SEPARATOR + request.getFilePath())
                .withPartSize(request.getPodNum())
                .build();
            ArrayList<StartEndPair> startEndPairs = build.getStartEndPairs();
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】，实际分片数量：【{}】 文件分片任务执结束.", request.getSceneId(), request.getFileName(),
                startEndPairs.size());
            if (!startEndPairs.isEmpty()) {
                String sliceInfo = JSONObject.toJSONString(startEndPairs);
                param.setStatus(FileSliceStatusEnum.SLICED.getCode());
                param.setSliceInfo(sliceInfo);
                param.setSliceCount(startEndPairs.size());
            }
        } catch (Exception e) {
            log.error("【文件分片】--场景ID：【{}】，文件名：【{}】， 文件分片任务异常，异常信息【{}】.", request.getSceneId(), request.getFileName(),
                e);
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR, e);
        }
    }

    private boolean contactFileAndScene(File file, SceneBigFileSliceParam param, ContactFileInfo info) {
        Date currentDate = new Date();
        SceneScriptRefEntity entity = fileSliceDAO.selectRef(param);
        if (Objects.isNull(entity)) {
            Date finalCurrentDate = currentDate;
            SceneScriptRef sceneScriptRef = new SceneScriptRef() {{
                setFileName(param.getFileName());
                setSceneId(param.getSceneId());
                setUploadPath(param.getSceneId() + DEFAULT_PATH_SEPARATOR + param.getFileName());
                JSONObject extJson = new JSONObject();
                if (Objects.nonNull(param.getIsOrderSplit()) && param.getIsOrderSplit() == 1) {
                    extJson.put("isSplit", 1);
                    info.setIsSplit(1);
                } else {
                    extJson.put("isSplit", param.getIsSplit());
                    info.setIsSplit(param.getIsSplit());
                }
                info.setIsSplit(1);
                extJson.put("isOrderSplit", param.getIsOrderSplit());
                info.setIsOrderSplit(param.getIsOrderSplit());
                setFileSize(String.valueOf(file.length()));
                setFileExtend(extJson.toJSONString());
                setUploadTime(finalCurrentDate);
                setFileType(1);
                setScriptType(0);
                setIsDeleted(0);
            }};
            fileSliceDAO.createRef(sceneScriptRef);
            entity = fileSliceDAO.selectRef(param);
        } else {
            JSONObject extJson = JSONObject.parseObject(entity.getFileExtend());
            if (Objects.nonNull(param.getIsOrderSplit()) && param.getIsOrderSplit() == 1) {
                extJson.put("isSplit", 1);
                info.setIsSplit(1);
            } else {
                extJson.put("isSplit", param.getIsSplit());
                info.setIsSplit(param.getIsSplit());
            }
            info.setIsSplit(1);
            extJson.put("isOrderSplit", param.getIsOrderSplit());
            info.setIsOrderSplit(param.getIsOrderSplit());
            entity.setFileSize(String.valueOf(file.length()));
            currentDate = entity.getUploadTime();
            entity.setFileExtend(extJson.toJSONString());
            fileSliceDAO.updateRef(entity);
        }

        //加文件分片清除位点缓存的逻辑
        cloudSceneTaskService.cleanCachedPosition(param.getSceneId());
        //按顺序拆分的文件进行预分片
        if (Objects.nonNull(param.getIsOrderSplit()) && param.getIsOrderSplit() == 1) {
            SceneScriptRefEntity finalEntity = entity;
            this.sliceFileByOrder(new FileSliceRequest() {{
                setSceneId(param.getSceneId());
                setFilePath(param.getSceneId() + DEFAULT_PATH_SEPARATOR + param.getFileName());
                setRefId(finalEntity.getId());
            }}, param);
            param.setFileRefId(entity.getId());
            param.setFileUploadTime(currentDate);
            param.setStatus(FileSliceStatusEnum.SLICED.getCode());
            param.setFilePath(param.getSceneId() + DEFAULT_PATH_SEPARATOR + param.getFileName());
            SceneBigFileSliceEntity sliceEntity = fileSliceDAO.selectOne(param);
            if (sliceEntity == null) {
                return fileSliceDAO.create(param) == 1;
            } else {
                return fileSliceDAO.update(param) == 1;
            }
        }
        return true;
    }

}
