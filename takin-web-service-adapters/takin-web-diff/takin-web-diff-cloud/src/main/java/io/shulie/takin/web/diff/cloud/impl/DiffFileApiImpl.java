package io.shulie.takin.web.diff.cloud.impl;

import java.util.Map;

import io.shulie.takin.adapter.api.entrypoint.file.CloudFileApi;
import io.shulie.takin.adapter.api.model.request.filemanager.FileContentParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileCopyParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileCreateByStringParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileDeleteParamReq;
import io.shulie.takin.adapter.api.model.request.filemanager.FileZipParamReq;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.diff.api.DiffFileApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author shiyajian
 * create: 2020-10-19
 */
@Component
@Slf4j
public class DiffFileApiImpl implements DiffFileApi {
    @Autowired
    private CloudFileApi cloudFileApi;

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
        if (CollectionUtils.isEmpty(req.getPaths())) {return true;}
        try {
            return cloudFileApi.deleteFile(req);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean copyFile(FileCopyParamReq req) {
        if (CollectionUtils.isEmpty(req.getSourcePaths())
            || StringUtil.isBlank(req.getTargetPath())) {return true;}
        try {
            return cloudFileApi.copyFile(req);
        } catch (Exception e) {
            return false;
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

}
