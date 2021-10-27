package io.shulie.takin.web.data.dao.filemanage;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;

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
        return fileManageEntities.stream().map(fileManageEntity -> {
            FileManageResult fileManageResult = new FileManageResult();
            fileManageResult.setId(fileManageEntity.getId());
            fileManageResult.setFileName(fileManageEntity.getFileName());
            fileManageResult.setFileSize(fileManageEntity.getFileSize());
            fileManageResult.setFileExt(fileManageEntity.getFileExt());
            fileManageResult.setFileType(fileManageEntity.getFileType());
            fileManageResult.setFileExtend(fileManageEntity.getFileExtend());
            fileManageResult.setCustomerId(fileManageEntity.getCustomerId());
            fileManageResult.setUploadTime(fileManageEntity.getUploadTime());
            fileManageResult.setUploadPath(fileManageEntity.getUploadPath());
            fileManageResult.setIsDeleted(fileManageEntity.getIsDeleted());
            fileManageResult.setGmtCreate(fileManageEntity.getGmtCreate());
            fileManageResult.setGmtUpdate(fileManageEntity.getGmtUpdate());
            return fileManageResult;
        }).collect(Collectors.toList());
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
}
