package io.shulie.takin.web.entrypoint.controller.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.pamirs.takin.entity.domain.dto.file.FileDTO;
import io.shulie.takin.cloud.entrypoint.file.CloudFileApi;
import io.shulie.takin.cloud.sdk.model.request.file.DeleteTempRequest;
import io.shulie.takin.cloud.sdk.model.request.file.UploadRequest;
import io.shulie.takin.cloud.sdk.model.response.file.UploadResponse;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.FileUtil;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qianshui
 * @date 2020/4/17 下午5:50
 */
@RestController
@RequestMapping("/api/file")
@Api(tags = "接口: 文件管理")
@Slf4j
public class FileController {

    /**
     * 上传文件的路径
     */
    @Value("${takin.data.path}")
    private String uploadPath;

    @Resource
    private CloudFileApi cloudFileApi;

    @ApiOperation("|_ 文件下载")
    @GetMapping("/download")
    public void download(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        if (!this.filePathValidate(filePath)) {
            log.error("非法下载路径文件，禁止下载：{}", filePath);
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("文件不存在，地址：{}", filePath);
            return;
        }

        CommonUtil.zeroCopyDownload(file, response);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public List<UploadResponse> upload(List<MultipartFile> file) {
        if (CollectionUtil.isEmpty(file)) {throw new RuntimeException("上传文件不能为空");}
        return cloudFileApi.upload(new UploadRequest() {{
            setFileList(FileUtil.convertMultipartFileList(file));
        }});
    }

    @PostMapping("/attachment/upload")
    @ApiOperation(value = "文件上传")
    public WebResponse<List<FileDTO>> uploadAttachment(List<MultipartFile> file) {
        if (file == null || file.size() == 0) {
            return WebResponse.fail("上传文件不能为空");
        }
        List<UploadResponse> response = cloudFileApi.upload(new UploadRequest() {{
            setFileList(FileUtil.convertMultipartFileList(file));
        }});
        FileUtil.deleteTempFile(file);
        List<FileDTO> dtoList = response.stream()
            .map(t -> BeanUtil.copyProperties(t, FileDTO.class))
            .peek(t -> t.setFileType(2))
            .collect(Collectors.toList());
        return WebResponse.success(dtoList);
    }

    @DeleteMapping
    @ApiOperation(value = "文件删除")
    public WebResponse<?> delete(@RequestBody DeleteTempRequest vo) {
        if (vo.getUploadId() == null) {
            return WebResponse.fail("删除文件不能为空");
        }
        cloudFileApi.deleteTempFile(vo);
        return WebResponse.success();
    }

    @ApiOperation("文件下载")
    @GetMapping("/downloadFileByPath")
    public void downloadFileByPath(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        this.download(filePath, response);
        // 删除文件
        //cn.hutool.core.io.FileUtil.del(filePath);
    }

    /**
     * 文件路径是否管理策略
     *
     * @param filePath 文件路径
     * @return 是/否
     */
    private boolean filePathValidate(String filePath) {
        return this.pathInit().stream().anyMatch(filePath::startsWith);
    }

    /**
     * 文件路径初始化
     *
     * @return 文件路径列表
     */
    private List<String> pathInit() {
        List<String> arrayList = new ArrayList<>();
        //arrayList.add(ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_USER_DATA_DIR));
        arrayList.add(ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_TMP_PATH));
        arrayList.add(ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_UPLOAD_SCRIPT_PATH));
        //arrayList.add(uploadPath);
        return arrayList;
    }

}
