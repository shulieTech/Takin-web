package io.shulie.takin.adapter.api.entrypoint.file;

import io.shulie.takin.adapter.api.model.request.file.DeleteTempRequest;
import io.shulie.takin.adapter.api.model.request.file.UploadRequest;
import io.shulie.takin.adapter.api.model.request.filemanager.*;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;

import java.util.List;
import java.util.Map;

/**
 * @author shiyajian
 * create: 2020-10-19
 */
public interface CloudFileApi {

    /**
     * 获取文件内容
     *
     * @param fileContentParamReq -
     * @return -
     */
    Map<String, Object> getFileContent(FileContentParamReq fileContentParamReq);

    /**
     * 删除文件
     *
     * @param fileDeleteParamReq -
     * @return -
     */
    Boolean deleteFile(FileDeleteParamReq fileDeleteParamReq);

    /**
     * 删除临时文件
     *
     * @param req -
     */
    void deleteTempFile(DeleteTempRequest req);

    /**
     * 复制文件到指定目录
     *
     * @param fileCopyParamReq -
     * @return -
     */
    Boolean copyFile(FileCopyParamReq fileCopyParamReq);

    /**
     * 将指定文件打包到指定目录
     *
     * @param fileZipParamReq -
     * @return -
     */
    Boolean zipFile(FileZipParamReq fileZipParamReq);

    /**
     * 将字符串转为指定文件
     *
     * @param fileCreateByStringParamReq -
     * @return -
     */
    String createFileByPathAndString(FileCreateByStringParamReq fileCreateByStringParamReq);

    /**
     * 上传文件
     *
     * @param req 请求
     * @return 上传结果
     */
    List<UploadResponse> upload(UploadRequest req);

    /**
     * 保存String到文件
     */
    UploadResponse saveJmxStringToFile(String processName, String jmxString);
}
