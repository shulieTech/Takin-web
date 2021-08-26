package io.shulie.takin.web.biz.service;

import io.shulie.takin.web.data.param.opsscript.OpsUploadFileParam;
import io.shulie.takin.web.data.result.opsscript.OpsScriptFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 运维脚本文件(OpsScriptFile)表服务接口
 *
 * @author caijy
 * @since 2021-06-16 10:47:10
 */
public interface OpsScriptFileService {

    List<OpsScriptFileVO> upload(List<MultipartFile> file, Integer fileType);

    Boolean delete(String uploadId);

    OpsScriptFileVO editFile(OpsUploadFileParam param);

    String viewFile(String path);
}
