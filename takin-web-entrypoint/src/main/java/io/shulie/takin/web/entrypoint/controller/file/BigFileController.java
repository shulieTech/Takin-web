package io.shulie.takin.web.entrypoint.controller.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.pamirs.takin.cloud.entity.domain.vo.file.Part;
import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.cloud.biz.service.cloud.server.BigFileService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mubai
 * @date 2020-05-12 14:47
 */
@Slf4j
@RestController
@RequestMapping("api/" + EntrypointUrl.MODULE_FILE_BIG)
public class BigFileController {
    @Resource
    private BigFileService bigFileService;
    @Resource
    private ThreadPoolExecutor bigFileThreadPool;

    private static void writeFile(HttpServletResponse response, File file) {
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        try(InputStream inputStream= new FileInputStream(file);) {
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[64];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    @PostMapping(EntrypointUrl.METHOD_BIG_FILE_UPLOAD)
    public ResponseResult<?> upload(String param, @RequestBody List<MultipartFile> file) {
        Part uploadVO = JSON.parseObject(param, Part.class);
        if (uploadVO.getUserAppKey() == null || uploadVO.getSceneId() == null || uploadVO.getFileName() == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR,
                "userAppKey | sceneId | fileName can not be null");
        }
        if (file.size() == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR,
                "upload file can not be null");
        }

        Future<ResponseResult> responseResultFuture = bigFileThreadPool.submit(() -> {
            try {
                MultipartFile multipartFile = file.get(0);
                byte[] bytes = multipartFile.getBytes();
                uploadVO.setByteData(bytes);
                return bigFileService.upload(uploadVO);
            } catch (IOException e) {
                log.error("文件上传失败！", e);
                return ResponseResult.fail("0", "文件上传失败!", "");
            }
        });

        try {
            return responseResultFuture.get();
        } catch (InterruptedException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "文件上传失败:文件上传线程出现中断", e);
        } catch (ExecutionException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR,
                "文件上传失败:获取异常线程的执行结果，发生异常", e);
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "文件上传失败:线程处理出现未知异常", e);
        }
    }

    @PostMapping(EntrypointUrl.METHOD_BIG_FILE_COMPACT)
    public ResponseResult<Map<String, Object>> compact(@RequestBody Part param) {
        if (param.getUserAppKey() == null || param.getSceneId() == null || param.getOriginalName() == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR,
                "userAppKey | sceneId | fileName can not be null");
        }
        return bigFileService.compact(param);
    }

    @ApiOperation("客户端下载")
    @GetMapping(value = EntrypointUrl.METHOD_BIG_FILE_DOWNLOAD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void downloadFile(HttpServletResponse response) {
        log.info("上传客户端下载...");
        File pradarUploadFile = bigFileService.getPradarUploadFile();
        if (pradarUploadFile != null) {
            writeFile(response, pradarUploadFile);
        }
    }
}
