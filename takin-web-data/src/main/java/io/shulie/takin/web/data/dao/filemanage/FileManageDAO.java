package io.shulie.takin.web.data.dao.filemanage;

import java.util.List;

import io.shulie.takin.web.data.param.filemanage.FileManageCreateParam;
import io.shulie.takin.web.data.result.filemanage.FileManageResult;

/**
 * @author zhaoyong
 */
public interface FileManageDAO {

    /**
     * 根据id查询文件信息
     * @param fileIds
     * @return
     */
    List<FileManageResult> selectFileManageByIds(List<Long> fileIds);

    /**
     * 批量删除
     * @param fileIds
     */
    void deleteByIds(List<Long> fileIds);

    /**
     * 批量创建文件
     * @param fileManageCreateParams
     * @return
     */
    List<Long> createFileManageList(List<FileManageCreateParam> fileManageCreateParams);


}
