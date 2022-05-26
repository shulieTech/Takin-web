package io.shulie.takin.web.data.dao.filemanage;

import java.util.List;

import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResponse;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import io.shulie.takin.web.data.param.filemanage.FileManageCreateParam;

/**
 * @author zhaoyong
 */
public interface FileManageDAO {

    /**
     * 根据id查询文件信息
     *
     * @param fileIds 文件主键集合
     * @return 文件信息集合
     */
    List<FileManageResult> selectFileManageByIds(List<Long> fileIds);

    /**
     * 批量删除
     *
     * @param fileIds 文件主键集合
     */
    void deleteByIds(List<Long> fileIds);

    /**
     * 批量创建文件
     *
     * @param fileList 文件集合
     * @return 文件主键集合
     */
    List<Long> createFileManageList(List<FileManageCreateParam> fileList);

    /**
     * 批量创建文件
     *
     * @param fileList 文件集合
     * @return 文件主键集合
     */
    List<FileManageEntity> createFileManageList_ext(List<FileManageCreateParam> fileList);

    /**
     * 根据id查询文件
     * @param id
     * @return
     */
    FileManageResult selectFileManageById(Long id);

    /**
     * 查询所有有签名值的文件
     */
    List<FileManageEntity> getAllFile();

    List<FileManageResponse> getFileManageResponseByFileIds(List<Long> fileIds);
}
