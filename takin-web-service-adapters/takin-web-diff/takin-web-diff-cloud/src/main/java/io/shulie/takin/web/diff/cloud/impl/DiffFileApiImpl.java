package io.shulie.takin.web.diff.cloud.impl;

import io.shulie.takin.cloud.entrypoint.file.CloudFileApi;
import io.shulie.takin.cloud.sdk.model.request.filemanager.*;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.diff.api.DiffFileApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author shiyajian
 * create: 2020-10-19
 */
@Component
@Slf4j
public class DiffFileApiImpl implements DiffFileApi {
    @Autowired
    private CloudFileApi cloudFileApi;

    @Value("${file.upload.openFileValid:true}")
    private boolean openFileValid;

    @Override
    public String getFileManageContextPath(FileContentParamReq req) {
        Map<String, Object> fileContent = cloudFileApi.getFileContent(req);
        if (fileContent != null && fileContent.containsKey(String.join(",", req.getPaths()))) {
            return fileContent.get(String.join(",", req.getPaths())).toString();
        }
        return "";
    }

    @Override
    public Boolean deleteFile(FileDeleteParamReq req) {
        if (CollectionUtils.isEmpty(req.getPaths())) {
            return true;
        }
        try {
            return cloudFileApi.deleteFile(req);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean copyFile(FileCopyParamReq req) {
        if (CollectionUtils.isEmpty(req.getSourcePaths())
                || StringUtil.isBlank(req.getTargetPath())) {
            return true;
        }
        try {
            if (!this.isExist(req.getSourcePaths())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "文件校验失败！");
            }
            return cloudFileApi.copyFile(req);
        } catch (Exception e) {
            log.error("copyFile is fail ", ExceptionUtils.getStackTrace(e));
            // 这里直接抛异常处理,不能吃掉异常信息
            throw e;
        }
    }

    @Override
    public Boolean zipFile(FileZipParamReq req) {
        if (CollectionUtils.isEmpty(req.getSourcePaths()) ||
                StringUtil.isBlank(req.getTargetPath()) || StringUtil.isBlank(req.getZipFileName())) {
            return false;
        }
        try {
            return cloudFileApi.zipFile(req);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String createFileByPathAndString(FileCreateByStringParamReq req) {
        if (StringUtil.isBlank(req.getFileContent()) || StringUtil.isBlank(req.getFilePath())) {
            return "";
        }
        return cloudFileApi.createFileByPathAndString(req);
    }

    /**
     * 批量验证文件
     *
     * @param pathList
     * @return
     */
    @Override
    public boolean isExist(List<String> pathList) {
        if (!openFileValid) {
            // 关闭的时候直接返回成功
            return true;
        }
        if (CollectionUtils.isEmpty(pathList)) {
            return false;
        }
        boolean isExist = true;
        for (int i = 0; i < pathList.size(); i++) {
            File file = new File(pathList.get(i));
            if (file != null && file.isFile()) {
                // 继续验证下一个
            } else {
                isExist = false;
                log.error("复制的文件不存在,", pathList.get(i));
                break;
            }
        }
        return isExist;
    }

}
