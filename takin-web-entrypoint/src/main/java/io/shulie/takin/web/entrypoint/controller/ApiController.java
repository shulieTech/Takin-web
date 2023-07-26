package io.shulie.takin.web.entrypoint.controller;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.biz.cache.DictionaryCache;
import io.shulie.takin.web.biz.pojo.request.file.FileUploadRequest;
import io.shulie.takin.web.biz.pojo.response.FileInfo;
import io.shulie.takin.web.biz.pojo.response.common.FileUploadResponse;
import io.shulie.takin.web.biz.pojo.response.common.IsNewAgentResponse;
import io.shulie.takin.web.biz.service.ApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.shulie.takin.web.common.common.Response;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qianshui
 * @date 2020/5/14 下午7:34
 */
@RestController
@RequestMapping("/api/")
@Api(tags = "接口: 公共API")
public class ApiController {

    @Autowired
    private ApiService apiService;

    @Autowired
    private DictionaryCache dictionaryCache;

    @Value("${file.upload.path:/data/nfs_dir/pressure_file_uoload}")
    private String fileUploadPath;

    @Value("${takin.web.url:http://192.168.1.201/takin-web}")
    private String takinWebPath;

    @ApiOperation("|_ 数据字典")
    @GetMapping("link/dictionary")
    public Map<String, List<EnumResult>> dictionary(String key) {
        return dictionaryCache.getDicMap(key);
    }

    @ApiOperation("|_ 上传文件")
    @PostMapping("v2/file/upload")
    public FileUploadResponse uploadFile(@Validated FileUploadRequest request) {
        return apiService.uploadFile(request);
    }

    @ApiOperation("|_ 配置-应用是否是新版本agent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applicationId", value = "应用id", required = true,
                    dataType = "long", paramType = "query")
    })
    @GetMapping("config/application/newAgent")
    public IsNewAgentResponse isNewAgent(@RequestParam Long applicationId) {
        return apiService.isNewAgentByApplication(applicationId);
    }

    @ApiOperation("|_ 服务全局配置")
    @GetMapping("serverConfig")
    public Map<String, Object> serverConfig() {
        return apiService.getServerConfig();
    }

    @ApiOperation("|_ 获取文件列表")
    @GetMapping("file/list")
    public Response<List<FileInfo>> getFileInfoList() {
        try {
            List<Path> fileList = Files.list(Paths.get(fileUploadPath)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(fileList)) {
                return Response.success(Collections.emptyList());
            }
            // takinWebPath 去掉后面的path只留下ip，并且拼接上testTool

            String takinWebIp = takinWebPath.substring(0, takinWebPath.lastIndexOf("/"));

            List<FileInfo> fileInfoList = new ArrayList<>();
            for (Path path1 : fileList) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(path1.getFileName().toString());
                fileInfo.setFilePath(takinWebIp + "/testTool/" + path1.getFileName().toString());
                long len = Files.size(path1);
                if (len > 0) {
                    double mb = len / (1024.0 * 1024);
                    fileInfo.setFileSize(String.format("%.2f", mb) + "MB");
                } else {
                    fileInfo.setFileSize("0MB");
                }
                fileInfoList.add(fileInfo);
            }
            return Response.success(fileInfoList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
