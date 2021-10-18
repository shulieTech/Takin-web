package io.shulie.takin.web.biz.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.utils.file.FileManagerHelper;
import io.shulie.takin.utils.linux.LinuxHelper;
import io.shulie.takin.web.biz.service.OpsScriptFileService;
import io.shulie.takin.web.biz.utils.FileUtils;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ConfigServerHelper;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptFileDAO;
import io.shulie.takin.web.data.model.mysql.OpsScriptFileEntity;
import io.shulie.takin.web.data.param.opsscript.OpsUploadFileParam;
import io.shulie.takin.web.data.result.opsscript.OpsScriptFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 运维脚本文件(OpsScriptFile)表服务实现类
 *
 * @author caijy
 * @since 2021-06-16 10:47:10
 */
@Service
public class OpsScriptFileServiceImpl implements OpsScriptFileService {

    @Autowired
    private OpsScriptFileDAO opsScriptFileDAO;

    private String tempPath;

    @PostConstruct
    public void init() {
        tempPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_DATA_PATH) + ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_OPS_SCRIPT_PATH);
    }

    @Override
    public List<OpsScriptFileVO> upload(List<MultipartFile> file, Integer fileType) {
        return uploadFile(file, fileType);
    }

    @Override
    public Boolean delete(String uploadId) {
        if (uploadId != null) {
            String targetDir = tempPath + SceneManageConstant.FILE_SPLIT + uploadId;
            LinuxHelper.executeLinuxCmd("rm -rf " + targetDir);
            return opsScriptFileDAO.lambdaUpdate().eq(OpsScriptFileEntity::getUploadId, uploadId).set(OpsScriptFileEntity::getIsDeleted, 1).update();
        }
        return false;
    }

    @Override
    public OpsScriptFileVO editFile(OpsUploadFileParam param) {
        File file = new File(param.getPath());
        OpsScriptFileVO fileVO = new OpsScriptFileVO();
        try {
            if (file.exists()) {
                file.delete();
            }
            FileManagerHelper.createFileByPathAndString(param.getPath(), param.getContent() + "\n");
            File current = new File(param.getPath());
            fileVO.setFileName(current.getName());
        } catch (Exception e) {
            Log.error("文件编辑失败，原因：{}", e.getMessage());
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_FILE_UPDATE_ERROR, e.getLocalizedMessage());
        }
        fileVO.setDownloadUrl(param.getPath());
        fileVO.setFileType(1);
        return fileVO;
    }

    @Override
    public String viewFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return FileUtils.readTextFileContent(file);
        }
        return "";
    }

    private List<OpsScriptFileVO> uploadFile(List<MultipartFile> files, Integer fileType) {
        List<OpsScriptFileVO> fileVOList = Lists.newArrayList();
        for (MultipartFile mf : files) {
            String uploadId = UUID.randomUUID().toString();
            File dir = new File(tempPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    LinuxHelper.executeLinuxCmd("mkdir -m777 -p " + dir.getAbsolutePath());
                }
            }
            File targetDir = new File(tempPath + "/" + uploadId);
            if (!targetDir.exists()) {
                if (!targetDir.mkdirs()) {
                    LinuxHelper.executeLinuxCmd("mkdir -m777 -p " + targetDir.getAbsolutePath());
                }
            }
            File targetFile = new File(tempPath + "/"
                + uploadId + "/" + mf.getOriginalFilename());
            try {
                OpsScriptFileVO fileVO = new OpsScriptFileVO();
                fileVO.setUploadId(uploadId);
                fileVO.setUploadTime(new Date());
                fileVO.setFileName(mf.getOriginalFilename());
                fileVO.setDownloadUrl(targetFile.getAbsolutePath());
                fileVO.setFileType(fileType);
                fileVO.setFileSize(this.getFileSize(mf.getSize()));
                mf.transferTo(new File(targetFile.getAbsolutePath()));
                fileVOList.add(fileVO);

                LinuxHelper.executeLinuxCmd("chmod 777" + targetDir.getAbsolutePath());
            } catch (IOException e) {
                throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_FILE_UPLOAD_ERROR, "文件上传失败！原因：" + e.getMessage());
            }
        }
        return fileVOList;
    }

    private String getFileSize(Long size) {
        if (size >= 1024) {
            size = size / 1024;
            if (size >= 1024) {
                size = size / 1024;
                return size + "MB";
            } else {
                return size + "KB";
            }
        }
        return size + "B";
    }
}
