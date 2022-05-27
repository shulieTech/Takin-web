package io.shulie.takin.web.data.dao.filemanage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.data.result.filemanage.FileManageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.mapper.mysql.FileManageMapper;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import io.shulie.takin.web.data.param.filemanage.FileManageCreateParam;

/**
 * @author zhaoyong
 */
@Component
public class FileManageDAOImpl implements FileManageDAO {

    @Resource
    private FileManageMapper fileManageMapper;

    @Override
    public List<FileManageResult> selectFileManageByIds(List<Long> fileIds) {
        LambdaQueryWrapper<FileManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            FileManageEntity::getFileExtend,
            FileManageEntity::getFileName,
            FileManageEntity::getFileSize,
            FileManageEntity::getFileType,
            FileManageEntity::getUploadPath,
            FileManageEntity::getUploadTime,
            FileManageEntity::getId,
            FileManageEntity::getIsDeleted
        );
        wrapper.in(FileManageEntity::getId, fileIds);
        List<FileManageEntity> fileManageEntities = fileManageMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(fileManageEntities)) {
            return null;
        }
        return fileManageEntities.stream().map(this::toFileManageResult).collect(Collectors.toList());
    }

    @Override
    public void deleteByIds(List<Long> fileIds) {
        if (CollectionUtils.isNotEmpty(fileIds)) {
            fileManageMapper.deleteBatchIds(fileIds);
        }
    }

    @Override
    public List<Long> createFileManageList(List<FileManageCreateParam> fileList) {
        // 收集 fileIds, 并赋值给 params
        return fileList.stream().map(t -> {
            FileManageEntity entity = BeanUtil.copyProperties(t, FileManageEntity.class);
            fileManageMapper.insert(entity);
            return entity.getId();
        }).collect(Collectors.toList());
    }

    @Override
    public List<FileManageEntity> createFileManageList_ext(List<FileManageCreateParam> fileList) {
        return fileList.stream().map(t -> {
            FileManageEntity entity = BeanUtil.copyProperties(t, FileManageEntity.class);
            fileManageMapper.insert(entity);
            return entity;
        }).collect(Collectors.toList());
    }

    @Override
    public FileManageResult selectFileManageById(Long id) {
        if (id == null) {
            return null;
        }
        FileManageEntity fileManageEntity = fileManageMapper.selectById(id);
        return toFileManageResult(fileManageEntity);
    }
	
	@Override
    public List<FileManageResponse> getFileManageResponseByFileIds(List<Long> fileIds) {
        List<FileManageResult> fileManageResults = this.selectFileManageByIds(fileIds);
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
        return Collections.EMPTY_LIST;
    }
	
    @Override
    public List<FileManageEntity> getAllFile() {
        LambdaQueryWrapper<FileManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                FileManageEntity::getUploadPath,
                FileManageEntity::getMd5
        );
        wrapper.isNotNull(FileManageEntity::getMd5);
        List<FileManageEntity> fileList = fileManageMapper.selectList(wrapper);
        return fileList;
    }

    private FileManageResult toFileManageResult(FileManageEntity fileManageEntity) {
        if (fileManageEntity == null) {
            return null;
        }
        FileManageResult fileManageResult = new FileManageResult();
        fileManageResult.setId(fileManageEntity.getId());
        fileManageResult.setFileName(fileManageEntity.getFileName());
        fileManageResult.setFileSize(fileManageEntity.getFileSize());
        fileManageResult.setFileExt(fileManageEntity.getFileExt());
        fileManageResult.setFileType(fileManageEntity.getFileType());
        fileManageResult.setFileExtend(fileManageEntity.getFileExtend());
        fileManageResult.setTenantId(fileManageEntity.getTenantId());
        fileManageResult.setUploadTime(fileManageEntity.getUploadTime());
        fileManageResult.setUploadPath(fileManageEntity.getUploadPath());
        fileManageResult.setIsDeleted(fileManageEntity.getIsDeleted());
        fileManageResult.setGmtCreate(fileManageEntity.getGmtCreate());
        fileManageResult.setGmtUpdate(fileManageEntity.getGmtUpdate());
        return fileManageResult;
    }
}
