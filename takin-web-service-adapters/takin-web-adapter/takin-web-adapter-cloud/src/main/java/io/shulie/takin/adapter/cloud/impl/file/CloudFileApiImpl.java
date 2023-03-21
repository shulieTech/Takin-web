package io.shulie.takin.adapter.cloud.impl.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import io.shulie.takin.adapter.api.entrypoint.file.CloudFileApi;
import io.shulie.takin.adapter.api.model.request.file.DeleteTempRequest;
import io.shulie.takin.adapter.api.model.request.file.UploadRequest;
import io.shulie.takin.adapter.api.model.request.filemanager.*;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.FileUtils;
import io.shulie.takin.cloud.common.utils.LinuxUtil;
import io.shulie.takin.cloud.common.utils.Md5Util;
import io.shulie.takin.utils.file.FileManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author shiyajian
 * @author 张天赐
 */
@Service
@Slf4j
public class CloudFileApiImpl implements CloudFileApi {

    @Value("${script.temp.path}")
    private String tempPath;

    @Override
    public Map<String, Object> getFileContent(FileContentParamReq req) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            for (String filePath : req.getPaths()) {
                if (new File(filePath).exists()) {
                    result.put(filePath, FileManagerHelper.readFileToString(new File(filePath), "UTF-8"));
                }
            }
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件内容获取异常 --> 异常信息: {}", TakinCloudExceptionEnum.FILE_READ_ERROR, e);
        }
        return result;
    }

    @Override
    public Boolean deleteFile(FileDeleteParamReq req) {
        return FileManagerHelper.deleteFiles(req.getPaths());
    }

    @Override
    public void deleteTempFile(DeleteTempRequest req) {
        if (req.getUploadId() != null) {
            String targetDir = tempPath + SceneManageConstant.FILE_SPLIT + req.getUploadId();
            LinuxUtil.executeLinuxCmd("rm -rf " + targetDir);
        }
    }

    @Override
    public Boolean copyFile(FileCopyParamReq req) {
        try {
            FileManagerHelper.copyFiles(req.getSourcePaths(), req.getTargetPath());
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件复制异常 --> 异常信息: {}", TakinCloudExceptionEnum.FILE_COPY_ERROR, e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }

    @Override
    public Boolean zipFile(FileZipParamReq req) {
        try {
            FileManagerHelper.zipFiles(req.getSourcePaths(), req.getTargetPath(), req.getZipFileName(), req.getIsCovered());
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：文件打包失败 --> 异常信息: {}", TakinCloudExceptionEnum.FILE_ZIP_ERROR, e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public String createFileByPathAndString(FileCreateByStringParamReq req) {
        Boolean fileCreateResult = FileManagerHelper.createFileByPathAndString(req.getFilePath(), req.getFileContent());
        return fileCreateResult ? Md5Util.md5File(req.getFilePath()) : null;
    }

    /**
     * 上传文件
     *
     * @param req 请求
     * @return 上传结果
     */
    @Override
    public List<UploadResponse> upload(UploadRequest req) {
        if (req == null) {
            throw new RuntimeException("调用SDK进行文件上传时,请求不能为空");
        }
        if (StrUtil.isBlank(req.getFieldName())) {
            throw new RuntimeException("调用SDK进行文件上传时,form表单名称不能为空");
        }
        List<MultipartFile> fileList = req.getFileList();
        if (CollUtil.isEmpty(fileList)) {
            throw new RuntimeException("调用SDK进行文件上传时,文件不能为空");
        }
        return fileList.stream().map(t -> {
            String uploadId = UUID.randomUUID().toString();
            File targetDir = new File(tempPath + SceneManageConstant.FILE_SPLIT + uploadId);
            if (!targetDir.exists()) {
                boolean mkdirResult = targetDir.mkdirs();
                log.debug("io.shulie.takin.adapter.cloud.impl.file.CloudFileApiImpl.upload:{}", mkdirResult);
            }
            File targetFile = new File(tempPath + SceneManageConstant.FILE_SPLIT
                    + uploadId + SceneManageConstant.FILE_SPLIT + t.getOriginalFilename());
            UploadResponse dto = new UploadResponse();
            try {
                if (StrUtil.isBlank(t.getOriginalFilename())) {
                    throw new FileAlreadyExistsException("不允许空名文件");
                }
                // 保存文件
                t.transferTo(targetFile);

                dto.setUploadId(uploadId);
                setDataCount(targetFile, dto);
                dto.setMd5(Md5Util.md5File(targetFile));
                dto.setFileName(t.getOriginalFilename());
                dto.setFileType(t.getOriginalFilename().endsWith("jmx") ? 0 : 1);
                dto.setDownloadUrl(targetDir + SceneManageConstant.FILE_SPLIT + t.getOriginalFilename());
                // 默认数据
                dto.setIsSplit(0);
                dto.setIsDeleted(0);
                dto.setUploadResult(true);
                dto.setUploadTime(DateUtil.formatDateTime(new Date()));
            } catch (IOException e) {
                log.error("文件处理异常:【{}】\n", TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
                dto.setUploadResult(false);
                dto.setErrorMsg(e.getMessage());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public UploadResponse saveJmxStringToFile(String processName, String jmxString) {
        String uploadId = UUID.randomUUID().toString();
        File targetDir = new File(tempPath + SceneManageConstant.FILE_SPLIT + uploadId);
        if (!targetDir.exists()) {
            boolean mkdirResult = targetDir.mkdirs();
            log.debug("io.shulie.takin.adapter.cloud.impl.file.CloudFileApiImpl.upload:{}", mkdirResult);
        }
        String originalFilename = processName + ".jmx";
        String targetFile = tempPath + SceneManageConstant.FILE_SPLIT + uploadId + SceneManageConstant.FILE_SPLIT + originalFilename;
        UploadResponse dto = new UploadResponse();
        try {
            if (StrUtil.isBlank(originalFilename)) {
                throw new FileAlreadyExistsException("不允许空名文件");
            }
            // 保存文件
            FileUtils.writeTextFile(jmxString, targetFile);
            dto.setUploadId(uploadId);
            dto.setMd5(Md5Util.md5File(targetFile));
            dto.setFileName(originalFilename);
            dto.setFileType(originalFilename.endsWith(".jmx") ? 0 : 1);
            dto.setDownloadUrl(targetDir + SceneManageConstant.FILE_SPLIT + originalFilename);
            // 默认数据
            dto.setIsSplit(0);
            dto.setIsDeleted(0);
            dto.setUploadResult(true);
            dto.setUploadTime(DateUtil.formatDateTime(new Date()));
        } catch (IOException e) {
            log.error("文件处理异常:【{}】\n", TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
            dto.setUploadResult(false);
            dto.setErrorMsg(e.getMessage());
        }
        return dto;
    }

    private void setDataCount(File file, UploadResponse dto) {
        String topic = SceneManageConstant.SCENE_TOPIC_PREFIX + System.currentTimeMillis();
        if (file.getName().endsWith("xlsx") || file.getName().endsWith("xls")) {
            //dto.setUploadedData(FileUtils.getFileCount(file.getAbsolutePath()));
            dto.setTopic(topic);
            return;
        }
        if (file.getName().endsWith(".csv")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                Long length = 0L;
                while (br.readLine() != null) {
                    length++;
                }
                dto.setUploadedData(length);
                dto.setTopic(topic);
            } catch (FileNotFoundException e) {
                log.error("异常代码【{}】,异常内容：找不到对应的文件 --> 异常信息: {}",
                        TakinCloudExceptionEnum.FILE_NOT_FOUND_ERROR, e);
            } catch (IOException e) {
                log.error("异常代码【{}】,异常内容：文件处理异常 --> 异常信息: {}",
                        TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        log.error("异常代码【{}】,异常内容：文件流关闭异常 --> 异常信息: {}",
                                TakinCloudExceptionEnum.FILE_CLOSE_ERROR, e);
                    }
                }
            }
            return;
        }
    }

}
